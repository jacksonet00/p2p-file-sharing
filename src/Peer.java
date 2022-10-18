import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Peer {

    public int id;
    public String hostname;
    public int listeningPort;
    public boolean hasFile;

    public Peer(int id, String hostname, int listeningPort, boolean hasFile) {
        this.id = id;
        this.hostname = hostname;
        this.listeningPort = listeningPort;
        this.hasFile = hasFile;
    }

    public void init(){ 
        // start listener
    }

    public void connectToPeers(List<Peer> peers) throws UnknownHostException, IOException {
        int peerIndex = 0;
        while (!peers.isEmpty()) {
            try {
                Socket socket = new Socket(peers.get(peerIndex).hostname, peers.get(peerIndex).listeningPort);
            }
            catch (ConnectException e) {
                peerIndex++;
            }

        }
    }
    
}
