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
                        byte[] peerId = new byte[4];
                        System.arraycopy(rawMessage, 28, peerId, 0, 4);
                        int peerIdInt = ByteBuffer.wrap(peerId).getInt();

                        System.out.println("Peer " + _peer._id + " received the handshake message from Peer " + peerIdInt);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}