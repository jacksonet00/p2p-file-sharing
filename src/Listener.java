import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Acts as a server
// Handles listening for messages from connected peers
// and invokes MessagingService to handle response.
public class Listener implements Runnable {
    Peer _peer;
    Socket _socket;
    ObjectInputStream _inputStream;
    ObjectOutputStream _outputStream;

    public Listener(Peer peer) {
        _peer = peer;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(_peer._portNumber);

            while (_peer._isRunning) {
                _socket = serverSocket.accept();

                _outputStream = new ObjectOutputStream(_socket.getOutputStream());
                _outputStream.flush();

                _inputStream = new ObjectInputStream(_socket.getInputStream());

                MessagingService messagingService = new MessagingService(_peer, _socket, _inputStream, _outputStream);
                Thread serviceThread = new Thread(messagingService);
                serviceThread.start();
            }

            if (!_peer._isRunning) {
                _inputStream.close();
                _outputStream.close();
                _socket.close();
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}