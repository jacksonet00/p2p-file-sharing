import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class peerProcess {

    public static void main(String[] args) throws NumberFormatException, FileNotFoundException {
        int peerId = Integer.parseInt(args[0]);

        // 1. read peer config
        File peerInfoConfigFile = new File("PeerInfo.cfg");
        Scanner peerInfoConfig = new Scanner(peerInfoConfigFile);

        // 2. assemble dict of peers
        HashMap<Integer, Peer> peers = new HashMap<Integer, Peer>();
        while (peerInfoConfig.hasNextLine()) {
            String[] record = peerInfoConfig.nextLine().split(" ");
            int id = Integer.parseInt(record[0]);
            Peer peer = new Peer(id, record[1], Integer.parseInt(record[2]), record[3] == "1");
            peers.put(id, peer);
        }
        peerInfoConfig.close();

        // 3. init server side of peer
        PeerServer peerServer = new PeerServer(peers.get(peerId));
        Thread peerServerThread = new Thread(peerServer);
        peerServerThread.start();
        
        // 4. iterate over peers and establish client connection between this peer and each other peer
        peers.get(peerId).displayCommonData();
        for (Map.Entry<Integer, Peer> entry : peers.entrySet()) {
            Peer peer = entry.getValue();
            PeerClient peerClient = new PeerClient(peers.get(peerId), peer);
            peerClient.initTcpConnection();
        }
    }
}