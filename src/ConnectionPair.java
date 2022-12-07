import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConnectionPair {
    ObjectInputStream _inputStream;
    ObjectOutputStream _outputStream;
    Peer _peer;

    public ConnectionPair(ObjectInputStream inputStream, ObjectOutputStream outputStream, Peer peer)
    {  
        _inputStream = inputStream;
        _outputStream = outputStream; 
        _peer = peer;  
    }
}
