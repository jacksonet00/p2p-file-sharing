import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class peerProcess {

    public static void main(String[] args) throws NumberFormatException, IOException {
        int peerId = Integer.parseInt(args[0]);

        // 1. read peer config
        File peerInfoConfigFile = new File("PeerInfo.cfg");
        Scanner peerInfoConfig = new Scanner(peerInfoConfigFile);

        // 2. assemble dict of peers to be stored in current peer for synchronized use and updating
        System.out.println("Reading data from PeerInfo.cfg and Common.cfg...");
        Hashtable<Integer, Peer> peers = new Hashtable<Integer, Peer>();
        while (peerInfoConfig.hasNextLine()) {
            String[] record = peerInfoConfig.nextLine().split(" ");
            int id = Integer.parseInt(record[0]);
            String hostName = record[1];
            int portNumber = Integer.parseInt(record[2]);
            boolean containsFile = record[3].equals("1");
            Peer peer = new Peer(id, hostName, portNumber, containsFile);
            System.out.println("Initialized Peer " + id + ".");
            System.out.println("Common.cfg data: ");
            peer.displayCommonData();
            System.out.println("PeerInfo.cfg data: ");
            peer.displayPeerData();
            if(containsFile && id == peerId) {
                // TODO: read file into peer._pieces
                FileInputStream is = null;
                try {
                    is = new FileInputStream(new File("peer_"+peerId+"/"+peer._fileName));
                    for(int i = 0; i < peer._totalNumPieces; i++) {
                        byte[] buf = new byte[peer._pieceSize];
                        try {
                            int read = is.read(buf);
                            peer._pieces.put(i, buf);
                        } catch(IOException e){
                            // e.printStackTrace();
                        }              
                    }   
                } catch (FileNotFoundException e) {
                    // if file is not found, peer no longer contains a file
                    // TODO: think of how to update this for OTHER peers in bitfield sending/receiving since it will be an inconsistency with peerinfo.cfg
                    peer._containsFile = false;
                    // e.printStackTrace();
                } finally {
                    try {
                        if(is!=null) {
                            is.close();
                        }  
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                }
            }
            peers.put(id, peer);
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
                System.out.println("Initializing Connection Handler...");
                ConnectionHandler connectionHandler = new ConnectionHandler(peers.get(peerId), peer);
                connectionHandler.initTcpConnection();
                System.out.println("Initialized. Peer " + peerId + " can now make TCP connection to Peer " + peer._id + ".");
            }
        }
        peers.get(peerId).runPreferredNeighbors();
        peers.get(peerId).runOptimisticUnchoke();
    }
}