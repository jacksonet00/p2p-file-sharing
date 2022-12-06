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

    @Override
    public void run() {
        byte[] handshakeMessage;
        try {
            handshakeMessage = MessageFactory.genHandshakeMessage(_peer._id);
        
            _peer.send(handshakeMessage, _outputStream, _remotePeerId);

            while (true) {
                    byte [] rawMessage = (byte[])_inputStream.readObject();
                    // Receiving bitfield message!
                    if(rawMessage[4] == 5) {
                        // TODO: decode bitfield, maybe reorganize how the messages are being read
                        // ran into issue with buffer underflow when a message other than the handshake was received cause of how we're reading in 0-18 bytes initially
                        // so I encased it in the else block, but there may be a better way to do it

                        // int messageLength =
                        // byte[] payload = 
                        
                        // _remotePeerId is currently broken cause it is never set, not sure how to fix other than setting it in ConnectionHandler for the init case
                        //  and then setting it here on handshake retrieve for the connection accepting case
                        System.out.println(_peer._id + " receives bitfield from " + _remotePeerId);
                    }
                    else {
                    ByteBuffer message =  ByteBuffer.wrap(rawMessage);
                    
                    byte[] messageHeader = new byte[18];
                    message.get(messageHeader,0,18);
                    String handshakeString = new String(messageHeader, StandardCharsets.UTF_8);

                    if(handshakeString.equals("P2PFILESHARINGPROJ")) {
                        byte[] peerIdRaw = new byte[4];
                        System.arraycopy(rawMessage, 28, peerIdRaw, 0, 4);
                        int peerId = ByteBuffer.wrap(peerIdRaw).getInt();
                        if (peerId == _peer._id) {
                            // prevents peer from "connecting to itself"
                            continue;
                        }
                        byte[] bitfieldMessage = MessageFactory.genBitfieldMessage(_peer._bitfield.toByteArray());
                        // ignore handshake message if this pair of peers is already connected
                        if(_peer._connectedPeers.containsKey(peerId)) {
                            // still need to send bitfield though, ex: p1 initiated p2 -> p2 sends handshake back to p1 -> p1 already connected -> send bitfield to p2
                            // _peer.send(bitfieldMessage, _outputStream, _remotePeerId);
                            continue;
                        }
                        
                        // Add remote peer to current peer's connectedpeers table if current peer was not the one to initiate the connection
                        _peer._connectedPeers.put(peerId, _peer._peers.get(peerId));
                        Logger.logTcpConnectionIncoming(_peer._id, peerId);

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