import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class peerProcess {
    
    public static void main(String args[]) throws UnknownHostException, IOException {

        int peerId = Integer.parseInt(args[0]);

        File peerInfoConfig = new File("PeerInfo.cfg");
        File commonConfig = new File("Common.cfg");

        List<Peer> peers = new ArrayList<Peer>();
       
        Scanner commonScanner = new Scanner(commonConfig);
        // Read in Client parameters from Common cfg
        int numPreferredNeighbors = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        int unchokingInterval = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        int optimisticUnchokingInterval = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        String fileName = commonScanner.nextLine().split(" ")[1];
        long fileSize = Long.parseLong(commonScanner.nextLine().split(" ")[1]);
        int pieceSize = Integer.parseInt(commonScanner.nextLine().split(" ")[1]);
        commonScanner.close();

        Client client = new Client(numPreferredNeighbors, unchokingInterval, optimisticUnchokingInterval, fileName, fileSize, pieceSize);

        Scanner peerInfoScanner = new Scanner(peerInfoConfig);
        // Initialize peer table by reading PeerInfo.cfg
        while (peerInfoScanner.hasNextLine()) {
            String record = peerInfoScanner.nextLine();
            String[] entries = record.split(" ");

            Peer peer = new Peer(Integer.parseInt(entries[0]), entries[1], Integer.parseInt(entries[2]), Integer.parseInt(entries[3]) == 1);

            client.peerTable.put(peer.id, peer);

            if (peerId != peer.id) {
                peers.add(peer);
            }
        }
        peerInfoScanner.close();

        client.peerTable.get(peerId).init();
        client.peerTable.get(peerId).connectToPeers(peers);
    }
}