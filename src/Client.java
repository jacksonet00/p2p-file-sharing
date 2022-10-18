import java.util.Hashtable;

public class Client {

    int numPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String fileName;
    long fileSize;
    int pieceSize;
    Hashtable<Integer, Peer> peerTable;

    public Client(int numPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, String fileName, long fileSize, int pieceSize) {

        this.numPreferredNeighbors = numPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        this.peerTable = new Hashtable<Integer, Peer>();
    }

    void run() {
        return;
    }

    void listen() {
        return;
    }
}