import java.util.Hashtable;

public class Peer 
{

    int numPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String fileName;
    long fileSize;
    int pieceSize;
    Hashtable<Integer, PeerData> peerDataTable;

    public Peer(int numPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, String fileName, long fileSize, int pieceSize) 
    {

        this.numPreferredNeighbors = numPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        this.peerDataTable = new Hashtable<Integer, PeerData>();
    }

    void run() 
    {
        return;
    }

    void listen() 
    {
        return;
    }

    
}