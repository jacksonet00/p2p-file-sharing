import java.net.Socket;

public class ConnectionPair {
    Socket _socket;  
    Peer _peer;

    public ConnectionPair(Socket socket, Peer peer)
    {  
        _socket = socket;  
        _peer = peer;  
    }
}
