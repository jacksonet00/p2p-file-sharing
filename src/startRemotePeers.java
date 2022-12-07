import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class startRemotePeers {

    // Configuration for startRemotePeers
    // 1. Set the path to the src directory (ex. "~/Folder/Folder/p2p-file-sharing/src")
    //    - it is important to start this path with '~'
    // 2. Set the password as your uf password for canvas (DO NOT COMMIT TO GITHUB)
    public static String PROJECT_PATH = "~/";
    public static String UF_PASSWORD = "";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // 1. read PeerInfo.cfg
        File peerInfoConfigFile = new File("PeerInfo.cfg");
        Scanner peerInfoConfig = new Scanner(peerInfoConfigFile);

        ArrayList<Peer> peers = new ArrayList<Peer>();
        while (peerInfoConfig.hasNextLine()) {
            String[] record = peerInfoConfig.nextLine().split(" ");

            int id = Integer.parseInt(record[0]);
            String hostName = record[1];
            int portNumber = Integer.parseInt(record[2]);
            boolean containsFile = record[3].equals("1");

            peers.add(new Peer(id, hostName, portNumber, containsFile));
        }
        peerInfoConfig.close();

        // 2. start remote peers
        System.out.println("Starting remote peers...");

        for (Peer peer : peers) {
            System.out.println("Starting remote peer " + peer._id + ".");
            try {
                Process process = Runtime.getRuntime().exec("ssh " + peer._hostName + " cd " + PROJECT_PATH + "; java peerProcess " + peer._id);

                try {
                    process.waitFor(5, TimeUnit.SECONDS);
                    BufferedReader res = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    if (res.readLine() != null) {
                        Runtime.getRuntime().exec("yes");
                    }
                    res.close();
                } catch (Exception e) {

                }

				try {
                    process.waitFor(5, TimeUnit.SECONDS);
                    BufferedReader res = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    if (res.readLine() != null) {
                        Runtime.getRuntime().exec(UF_PASSWORD);
                    }
                    res.close();
                } catch (Exception e) {
                    
                }

                System.out.println("Started remote peer " + peer._id);
            } catch (Exception e) {
                System.out.println("Failed to start remote peer " + peer._id + ".");
            }
        }

        System.out.println("Remote peers started.");
        System.out.println("Running indefinitely. Press Ctrl+C to exit.");
        while(true) {}
    }
}