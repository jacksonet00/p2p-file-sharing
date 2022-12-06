import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class peerProcess {

    public static void main(String[] args) throws NumberFormatException, FileNotFoundException {
        int peerId = Integer.parseInt(args[0]);

        // 1. read peer config
        File peerInfoConfigFile = new File("PeerInfo.cfg");
        Scanner peerInfoConfig = new Scanner(peerInfoConfigFile);

        // 2. assemble dict of peers to be stored in current peer for synchronized use and updating
        Hashtable<Integer, Peer> peers = new Hashtable<Integer, Peer>();
        while (peerInfoConfig.hasNextLine()) {
            String[] record = peerInfoConfig.nextLine().split(" ");

            int id = Integer.parseInt(record[0]);
            String hostName = record[1];
            int portNumber = Integer.parseInt(record[2]);
            boolean containsFile = record[3] == "1";

            peers.put(id, new Peer(id, hostName, portNumber, containsFile));
        }
        peerInfoConfig.close();
        peers.get(peerId)._peers = peers;

        // 3. init server side of peer
        Listener listener = new Listener(peers.get(peerId));
        Thread listenerThread = new Thread(listener);
        listenerThread.start();
        
        // 4. iterate over peers and establish client connection between this peer and each other peer
        for (Map.Entry<Integer, Peer> entry : peers.entrySet()) {
            Peer peer = entry.getValue();
            if(peer._id < peerId) {
                ConnectionHandler connectionHandler = new ConnectionHandler(peers.get(peerId), peer);
                connectionHandler.initTcpConnection();
            }
        }
    }
}