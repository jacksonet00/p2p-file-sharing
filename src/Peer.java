import java.util.Hashtable;
import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.*;
public class Peer {

    int numPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String fileName;
    long fileSize;
    int pieceSize;
    // Each peer has a table of PeerData to reference for information about other peers
    Hashtable<Integer, PeerData> peerDataTable;
    // Each peer has list of ongoing connection threads
    public static List<PeerThread> Connections = Collections.synchronizedList(new ArrayList<PeerThread>());


    public Peer(int numPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, String fileName, long fileSize, int pieceSize) {

        this.numPreferredNeighbors = numPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        this.peerDataTable = new Hashtable<Integer, PeerData>();

    }

    void connectToAllpeers() {
        // When peer is initialized, attempt to connect to all other peers 
        for(int key: peerDataTable.keySet()) {
            int connectPort = peerDataTable.get(key).listeningPort;
            String host = peerDataTable.get(key).hostname;
            int id = peerDataTable.get(key).id;
            try 
            {
                PeerThread p;
                
                Socket socket = new Socket(host, connectPort);
                p = new PeerThread(socket, id);
                p.start();
                Connections.add(p);
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
    }
    void run() {
        return;
    }

    void listen() {
        return;
    }


}