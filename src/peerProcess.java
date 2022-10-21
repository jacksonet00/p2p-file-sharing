import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// this class contains the init to start the processes.
public class peerProcess 
{
    
    public static void main(String args[]) throws UnknownHostException, IOException 
    {

        int peerId = Integer.parseInt(args[0]);

        File peerInfoConfig = new File("PeerInfo.cfg");
        File commonConfig = new File("Common.cfg");

        List<PeerData> peers = new ArrayList<PeerData>();
       
        Scanner commonScanner = new Scanner(commonConfig);
        // Read in Peer parameters from Common cfg
        int numPreferredNeighbors = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        int unchokingInterval = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        int optimisticUnchokingInterval = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        String fileName = commonScanner.nextLine().split(" ")[1];
        long fileSize = Long.parseLong(commonScanner.nextLine().split(" ")[1]);
        int pieceSize = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        commonScanner.close();

        Peer peer = new Peer(numPreferredNeighbors, unchokingInterval, optimisticUnchokingInterval, fileName, fileSize, pieceSize);

        Scanner peerInfoScanner = new Scanner(peerInfoConfig);
        // Initialize peer table by reading PeerInfo.cfg
        // this file is provided to us in the Project folder.
        while (peerInfoScanner.hasNextLine()) 
        {
            String record = peerInfoScanner.nextLine();
            String[] entries = record.split(" ");

            PeerData peerData = new PeerData(Integer.parseInt(entries[0]), entries[1], Integer.parseInt(entries[2]), Integer.parseInt(entries[3]) == 1);

            peer.peerDataTable.put(peerData.id, peerData);

            if (peerId != peerData.id) 
            {
                peers.add(peerData);
            }
        }
        peerInfoScanner.close();

        peer.run();

        // peer.peerDataTable.get(peerId).init();
        // peer.peerDataTable.get(peerId).connectToPeers(peers);
    }
}