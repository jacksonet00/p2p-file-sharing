import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class MessagingService implements Runnable {
    Peer _peer;
    int _remotePeerId;
    Socket _socket;
    ObjectInputStream _inputStream;
    static ObjectOutputStream _outputStream;

    public MessagingService(Peer peer, Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream){
        _peer = peer;
        _socket = socket;
        _inputStream = inputStream;
        _outputStream = outputStream;
    }

    // Overridden constructor include remotePeerId.
    public MessagingService(Peer peer, int remotePeerId, Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream){
        _peer = peer;
        _remotePeerId = remotePeerId;
        _socket = socket;
        _inputStream = inputStream;
        _outputStream = outputStream;
    }

    @Override
    public void run() {
        byte[] handshakeMessage;
        try {
            handshakeMessage = MessageFactory.genHandshakeMessage(_peer._id);
        
            _peer.send(handshakeMessage, _outputStream, _remotePeerId);

            while (true) {
                    byte [] rawMessage = (byte[])_inputStream.readObject();
                    MessageFactory.decodeMessage(rawMessage);
                    
                    if (rawMessage[4] < 8) {
                        ByteBuffer message =  ByteBuffer.wrap(rawMessage);
                        int messagePayloadLength = message.getInt();
                        byte messageType = message.get();
                        if (messageType == 0) {
                            Logger.logChokeNeighbor(_peer._id, _remotePeerId);
                        }
                        else if (messageType == 1) {
                            Logger.logUnchokedNeighbor(_peer._id, _remotePeerId);
                            if(!_peer._containsFile) {
                                int requestIndex = _peer.getIndexToRequest(_remotePeerId);
                                byte[] requestMessage = MessageFactory.genRequestMessage(requestIndex);
                                _peer.send(requestMessage, _outputStream, _remotePeerId);
                            }
                        }
                        else if (messageType == 2) {
                            _peer._interestedPeers.add(_remotePeerId);
                            Logger.logReceiveInterestedMessage(_peer._id, _remotePeerId);
                            // update table that peer is interested for optimistically unchoked
                        }
                        else if (messageType == 3) {
                            _peer._interestedPeers.remove(_remotePeerId);
                            Logger.logReceiveNotInterestedMessage(_peer._id, _remotePeerId);
                            // update table that peer is not interested for optimistically unchoked
                        }
                        else if (messageType == 4) {
                            // Each peer maintains bitfields for all neighbors and updates them
                            // whenever it receives ‘have’ messages from its neighbors
                            Logger.logReceiveHaveMessage(_peer._id, _remotePeerId);
                            byte[] indexRaw = new byte[4];
                            System.arraycopy(rawMessage, 5, indexRaw, 0, 4);
                            int index = ByteBuffer.wrap(indexRaw).getInt();
                            _peer._connectedPeers.get(_remotePeerId)._peer._bitfield.set(index, true);

                            // determine whether it should send an ‘interested’ message to the neighbor
                            if (!_peer._bitfield.get(index)) {
                                _peer.send(MessageFactory.genInterestedMessage(), _outputStream, _remotePeerId);
                            }
                        }
                        else if(messageType == 5) {
                            // TODO: decode bitfield, maybe reorganize how the messages are being read

                            // Find out payload length from 4-byte message length field
                            byte[] msgLengthRaw = new byte[4];
                            System.arraycopy(rawMessage, 0, msgLengthRaw, 0, 4);
                            int msgLength = ByteBuffer.wrap(msgLengthRaw).getInt();
                            //System.out.println(msgLength);

                            // Remove 1-byte message  type  field for accuraye payload size
                            int payloadLength = msgLength - 1;
                            // System.out.println("Payload length is " + payloadLength);
                            //byte[] payload = new byte[payloadLength];

                            // Check each byte for what pieces peer has.
                            byte[] payload = new byte[payloadLength];
                            System.arraycopy(rawMessage, 5, payload, 0, payloadLength);
                            BitSet remotePeerBitfield  = BitSet.valueOf(payload);
                            _peer._connectedPeers.get(_remotePeerId)._peer._bitfield = (BitSet) remotePeerBitfield.clone();

                            BitSet bitfieldDiff = (BitSet) _peer._bitfield.clone();
                            bitfieldDiff.or(remotePeerBitfield);
                            bitfieldDiff.xor(_peer._bitfield);
                            
                            if (bitfieldDiff.isEmpty()) {
                                // send not interested msg
                                _peer.send(MessageFactory.genUninterestedMessage(), _outputStream, _remotePeerId);
                                //System.out.println("Not interested");
                            }
                            else {
                                // send interested msg
                                _peer.send(MessageFactory.genInterestedMessage(), _outputStream, _remotePeerId);
                                //System.out.println("Interested");
                            }
                            
                            // _remotePeerId is currently broken cause it is never set, not sure how to fix other than setting it in ConnectionHandler for the init case
                            //  and then setting it here on handshake retrieve for the connection accepting case
                            // System.out.println(_peer._id + " receives bitfield from " + _remotePeerId);
                        }
                        else if (messageType == 6) {
                            int pieceIndex = message.getInt();
                            if(_peer._chokedPeers.contains(_remotePeerId)) {
                                continue;
                            }
                            // TODO: peer that we received request from is not choked --> send piece to them
                        }
                        else if (messageType == 7) {
                            int pieceIndex = message.getInt();
                            byte[] piece = new byte[message.remaining()];
                            message.get(piece);
                            // Store piece in peer's current pieces (downloading the piece per se)
                            _peer._pieces.put(pieceIndex, piece);
                            Logger.logDownloadedPiece(_peer._id, _remotePeerId, pieceIndex, _peer._pieces.size());
                            // Update peer's bitfield to indicate piece retrieved
                            _peer.setBitfield(pieceIndex, true);

                            // send has piece message to all other pieces
                            // TODO: may need to put this on its own thread..? current output stream is just sent to one socket connection (remote peer that it received the piece from)
                            for(int tempRemotePeerId: _peer._connectedPeers.keySet()) {
                                try {
                                    byte[] haveMessage = MessageFactory.genHaveMessage(pieceIndex);
                                    Socket tempSocket = _peer._connectedPeers.get(tempRemotePeerId)._socket;
                                    ObjectOutputStream tempOutputStream = new ObjectOutputStream(tempSocket.getOutputStream());
                                    tempOutputStream.flush();
                                    _peer.send(haveMessage, tempOutputStream, tempRemotePeerId);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // TODO: send request message of random piece that we do NOT have
                            if(!_peer._containsFile) {
                                int requestIndex = _peer.getIndexToRequest(_remotePeerId);
                                byte[] requestMessage = MessageFactory.genRequestMessage(requestIndex);
                                _peer.send(requestMessage, _outputStream, _remotePeerId);
                            } else {
                                // TODO: save file to  disk
                                try {
                                    Logger.logDownloadComplete(_peer._id);
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            // System.out.println("Message not of valid type");
                        }
                    }
                    else { // handshake message
                        ByteBuffer message =  ByteBuffer.wrap(rawMessage);
                        byte[] messageHeader = new byte[18];
                        message.get(messageHeader,0,18);
                        String handshakeString = new String(messageHeader, StandardCharsets.UTF_8);

                        if(handshakeString.equals("P2PFILESHARINGPROJ")) {
                            byte[] remotePeerIdRaw = new byte[4];
                            System.arraycopy(rawMessage, 28, remotePeerIdRaw, 0, 4);
                            _remotePeerId = ByteBuffer.wrap(remotePeerIdRaw).getInt();
                            if (_remotePeerId == _peer._id) {
                                // prevents peer from "connecting to itself"
                                continue;
                            }
                            byte[] bitfieldMessage = MessageFactory.genBitfieldMessage(_peer._bitfield.toByteArray());
                            // ignore handshake message if this pair of peers is already connected
                            if(_peer._connectedPeers.containsKey(_remotePeerId)) {
                                // still need to send bitfield though, ex: p1 initiated p2 -> p2 sends handshake back to p1 -> p1 already connected -> send bitfield to p2
                                if(!_peer._bitfield.isEmpty()) {
                                    _peer.send(bitfieldMessage, _outputStream, _remotePeerId);
                                }
                                continue;
                            }
                            
                            // Add remote peer to current peer's connectedpeers table if current peer was not the one to initiate the connection
                            ConnectionPair connection = new ConnectionPair(_socket, _peer._peers.get(_remotePeerId));
                            _peer._connectedPeers.put(_remotePeerId, connection);
                            Logger.logTcpConnectionIncoming(_peer._id, _remotePeerId);

                            // Once TCP connection has been established, send bitfield message (receiving case)
                            if (!_peer._bitfield.isEmpty()) {
                                _peer.send(bitfieldMessage, _outputStream, _remotePeerId);
                                // System.out.println(_peer._id + " sends bitfield " + bitfieldMessage + " to " + _remotePeerId);
                            }
                        }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}