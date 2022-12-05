import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
public class Peer {
    // PeerInfo.cfg
    int _id;
    String _hostName;
    int _portNumber;
    boolean _containsFile;

    // Common.cfg
    int _numberOfPreferredNeighbors;
    int _unchokingInterval;
    int _optimisticUnchokingInterval;
    String _fileName;
    int _fileSize;
    int _pieceSize;

    public Peer(int peerId, String hostName, int portNumber, boolean containsFile) throws FileNotFoundException {
        init();
        _id = peerId;
        _hostName = hostName;
        _portNumber = portNumber;
        _containsFile = containsFile;
    }

    private void init() throws FileNotFoundException {
        // Read data from Common.cfg
        File commonConfigFile = new File("Common.cfg");
        Scanner commonConfig = new Scanner(commonConfigFile);

        _numberOfPreferredNeighbors = Integer.parseInt(commonConfig.nextLine().split(" ")[1]);
        _unchokingInterval = Integer.parseInt(commonConfig.nextLine().split(" ")[1]);
        _optimisticUnchokingInterval = Integer.parseInt(commonConfig.nextLine().split(" ")[1]);
        _fileName = commonConfig.nextLine().split(" ")[1];
        _fileSize = Integer.parseInt(commonConfig.nextLine().split(" ")[1]);
        _pieceSize = Integer.parseInt(commonConfig.nextLine().split(" ")[1]);

        commonConfig.close();
    }

    public void displayPeerData() {
        System.out.println("Peer ID: " + _id);
        System.out.println("Host Name: " + _hostName);
        System.out.println("Port Number: " + _portNumber);
        System.out.println("Contains File: " + _containsFile);
    }

    public void displayCommonData() {
        System.out.println("Number of Preferred Neighbors: " + _numberOfPreferredNeighbors);
        System.out.println("Unchoking Interval: " + _unchokingInterval);
        System.out.println("Optimistic Unchoking Interval: " + _optimisticUnchokingInterval);
        System.out.println("File Name: " + _fileName);
        System.out.println("File Size: " + _fileSize);
        System.out.println("Piece Size: " + _pieceSize);
    }

    public void send(byte [] message, ObjectOutputStream outputStream, int remotePeerId) {
        try{
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}