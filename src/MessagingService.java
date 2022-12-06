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
    ObjectOutputStream _outputStream;

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
<<<<<<< HEAD
                        if (rawMessage[4] == 0) {
                           Logger.logChokeNeighbor(_peer._id, _remotePeerId);
                        }
                        else if (rawMessage[4] == 1) {
                            // when unchoked, a peer sends a ‘request’ message
                            // for requesting  a  piece  that  it  does  not  have
                            // and  has  not  requested  from  other  neighbors
                            // random selection strategy
                            Logger.logUnchokedNeighbor(_peer._id, _remotePeerId);
                            while (true) {
                                
                            }
                            //MessageFactory.genRequestMessage();
                        }
                        else if (rawMessage[4] == 2) {
                            Logger.logReceiveInterestedMessage(_peer._id, _remotePeerId);
                        }
                        else if (rawMessage[4] == 3) {
                            Logger.logReceiveNotInterestedMessage(_peer._id, _remotePeerId);
                        }

                        else if (rawMessage[4] == 4) {
                            // Each peer maintains bitfields for all neighbors and updates them
                            // whenever it receives ‘have’ messages from its neighbors
                        }
                        // Receiving bitfield message!
                        else if(rawMessage[4] == 5) {
=======
                        ByteBuffer message =  ByteBuffer.wrap(rawMessage);
                        int messagePayloadLength = message.getInt();
                        byte messageType = message.get();
                        // Receiving bitfield message!
                        if (messageType == 0) {}
                        else if (messageType == 1) {}
                        else if (messageType == 2) {}
                        else if (messageType == 3) {}
                        else if (messageType == 4) {}
                        else if(messageType == 5) {
>>>>>>> d9e717b162061657492b3e45091335808c521021
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
                            _peer._connectedPeers.get(_remotePeerId)._bitfield = (BitSet) remotePeerBitfield.clone();

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
                            System.out.println(_peer._id + " receives bitfield from " + _remotePeerId);
                        }
<<<<<<< HEAD
                        else if (rawMessage[4] == 6) {}
                        else if (rawMessage[4] == 7) {
                            // completely downloading the  piece
                            // peer  A  sends  another  ‘request’  message  to  peer  B
                            // check edge case for choking
=======
                        else if (messageType == 6) {}
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
                            for(int key: _peer._connectedPeers.keySet()) {
                                try {
                                    byte[] haveMessage = MessageFactory.genHaveMessage(pieceIndex);
                                    _peer.send(haveMessage, _outputStream, _remotePeerId);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // TODO: send request message of random piece that we do NOT have
>>>>>>> d9e717b162061657492b3e45091335808c521021
                        }
                        else {
                            System.out.println("Message not of valid type");
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
                                // _peer.send(bitfieldMessage, _outputStream, _remotePeerId);
                                continue;
                            }
                            
                            // Add remote peer to current peer's connectedpeers table if current peer was not the one to initiate the connection
                            _peer._connectedPeers.put(_remotePeerId, _peer._peers.get(_remotePeerId));
                            Logger.logTcpConnectionIncoming(_peer._id, _remotePeerId);

                            // Once TCP connection has been established, send bitfield message (receiving case)
                            if (!_peer._bitfield.isEmpty()) {
                                _peer.send(bitfieldMessage, _outputStream, _remotePeerId);
                                System.out.println(_peer._id + " sends bitfield " + bitfieldMessage + " to " + _remotePeerId);
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