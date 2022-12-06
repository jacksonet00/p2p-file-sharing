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

                        // TODO: ignore handshake message if this pair of peers is already connected
                        if(_peer._connectedPeers.containsKey(peerId)) {
                            continue;
                        }
                        
                        // Add remote peer to current peer's connectedpeers table if current peer was not the one to initiate the connection
                        _peer._connectedPeers.put(peerId, _peer.peers.get(peerId));
                        Logger.logTcpConnectionIncoming(_peer._id, peerId);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}