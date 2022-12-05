import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer implements Runnable {
    Peer _peer;
    Socket _socket;
    ObjectInputStream _inputStream;
    ObjectOutputStream _outputStream;

    public PeerServer(Peer peer) {
        _peer = peer;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(_peer._portNumber);

            while (true) {
                _socket = serverSocket.accept();

                _outputStream = new ObjectOutputStream(_socket.getOutputStream());
                _outputStream.flush();

                _inputStream = new ObjectInputStream(_socket.getInputStream());

                MessagingService messagingService = new MessagingService(_peer, _socket, _inputStream, _outputStream);
                Thread serviceThread = new Thread(messagingService);
                serviceThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
