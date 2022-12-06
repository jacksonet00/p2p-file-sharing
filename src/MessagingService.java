import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
                    if (rawMessage[4] < 8) {
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
                            // TODO: decode bitfield, maybe reorganize how the messages are being read
                            // ran into issue with buffer underflow when a message other than the handshake was received cause of how we're reading in 0-18 bytes initially
                            // so I encased it in the else block, but there may be a better way to do it

                            // int messageLength =
                            // byte[] payload = 
                            
                            // _remotePeerId is currently broken cause it is never set, not sure how to fix other than setting it in ConnectionHandler for the init case
                            //  and then setting it here on handshake retrieve for the connection accepting case
                            System.out.println(_peer._id + " receives bitfield from " + _remotePeerId);
                        }
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
                            _peer.send(bitfieldMessage, _outputStream, _remotePeerId);
                            System.out.println(_peer._id + " sends bitfield " + bitfieldMessage + " to " + _remotePeerId);
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