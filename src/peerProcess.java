import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Hashtable;

public class peerProcess {
    
    public static void main(String args[]) throws FileNotFoundException {

        Hashtable<Integer, Peer> peerTable = new Hashtable<Integer, Peer>();

        File peerInfoConfig = new File("PeerInfo.cfg");
        Scanner scanner = new Scanner(peerInfoConfig);

        while (scanner.hasNextLine()) {
            String record = scanner.nextLine();
            String[] entries = record.split(" ");

            Peer peer = new Peer(Integer.parseInt(entries[0]), entries[1], Integer.parseInt(entries[2]), Integer.parseInt(entries[3]) == 1);

            peerTable.put(peer.id, peer);
        }

        scanner.close();
    }
}