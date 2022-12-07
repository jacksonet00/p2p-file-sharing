import java.net.Socket;

public class ConnectionPair {
    Socket socket;  
    Peer peer;

    public ConnectionPair(Socket socket, Peer peer)
    {  
        this.socket = socket;  
        this.peer = peer;  
    }
}
