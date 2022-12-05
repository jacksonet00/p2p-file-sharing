import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

// Acts as a client
// Maintains a TCP connection between the current peer
// and a remote peer.
public class ConnectionHandler {
    Peer _peer;
    Peer _remotePeer;
    ObjectInputStream _inputStream;
    ObjectOutputStream _outputStream;

    public ConnectionHandler(Peer peer, Peer remotePeer){
        _peer = peer;
        _remotePeer = remotePeer;
    }

    public void initTcpConnection() {
        Socket socket;
        try {
            socket = new Socket(_remotePeer._hostName, _remotePeer._portNumber);

            _outputStream = new ObjectOutputStream(socket.getOutputStream());
            _outputStream.flush();

            _inputStream = new ObjectInputStream(socket.getInputStream());

            MessagingService messagingService = new MessagingService(_peer, socket, _inputStream, _outputStream);

            Thread serviceThread = new Thread(messagingService);
            serviceThread.start();
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
