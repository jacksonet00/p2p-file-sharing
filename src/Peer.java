import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Peer {
    // PeerInfo.cfg data members
    int _id;
    String _hostName;
    int _portNumber;
    boolean _containsFile;

    // Common.cfg data members
    int _numberOfPreferredNeighbors;
    int _unchokingInterval;
    int _optimisticUnchokingInterval;
    String _fileName;
    int _fileSize;
    int _pieceSize;

    // provide access to synchronized list to current peer for realtime updating
    Hashtable<Integer, Peer> _peers;
    Hashtable<Integer, ConnectionPair> _connectedPeers;
    // current pieces retrieved
    Hashtable<Integer, byte[]> _pieces;

    // general member variables
    // int _pieceCount;
    int _totalNumPieces;
    BitSet _bitfield; // https://stackoverflow.com/questions/17545601/how-to-represent-bit-fields-and-send-them-in-java
    Set<Integer> _interestedPeers;
    Set<Integer> _chokedPeers;
    
    
    public Peer(int peerId, String hostName, int portNumber, boolean containsFile) throws FileNotFoundException {
        init();
        _id = peerId;
        _hostName = hostName;
        _portNumber = portNumber;
        _containsFile = containsFile;
        
        _connectedPeers = new Hashtable<Integer, ConnectionPair>();
        _pieces = new Hashtable<Integer, byte[]>();
        _bitfield = new BitSet(_totalNumPieces);
        if(_containsFile) {
            _bitfield.set(0, _totalNumPieces, true);
        } 
        _interestedPeers =  new HashSet<Integer>();
        _chokedPeers = new HashSet<Integer>();
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

        _totalNumPieces = (int)Math.ceil((double)_fileSize/_pieceSize); // https://stackoverflow.com/questions/7446710/how-to-round-up-integer-division-and-have-int-result-in-java
    }

    // For debugging purposes
    public void displayPeerData() {
        System.out.println("Peer ID: " + _id);
        System.out.println("Host Name: " + _hostName);
        System.out.println("Port Number: " + _portNumber);
        System.out.println("Contains File: " + _containsFile);
    }

    // For debugging purposes
    public void displayCommonData() {
        System.out.println("Number of Preferred Neighbors: " + _numberOfPreferredNeighbors);
        System.out.println("Unchoking Interval: " + _unchokingInterval);
        System.out.println("Optimistic Unchoking Interval: " + _optimisticUnchokingInterval);
        System.out.println("File Name: " + _fileName);
        System.out.println("File Size: " + _fileSize);
        System.out.println("Piece Size: " + _pieceSize);
    }

    public void send(byte[] message, ObjectOutputStream outputStream, int remotePeerId) {
        try{
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void setBitfield(int pieceIndex, boolean exists){
        _bitfield.set(pieceIndex, exists);
        if(_bitfield.nextClearBit(0) >= _totalNumPieces) { // https://stackoverflow.com/questions/36308666/check-if-all-bits-in-bitset-are-set-to-true
           // Once bitfield is all true (all pieces have been received) then the peer now has the file
            _containsFile = true;
        }
    }
    public BitSet getInterestedPieces(int remotePeerId) {
        BitSet piecesToRequest = (BitSet)_bitfield.clone();
        // Get pieces that you are interested in by (ALL PIECES BETWEEN YOU AND REMOTE) XOR (YOUR PIECES) = PIECES YOU NEED
        piecesToRequest.or(_connectedPeers.get(remotePeerId)._peer._bitfield);
        piecesToRequest.xor(_bitfield);
        return piecesToRequest;
    }

    public int getIndexToRequest(int remotePeerId) {
        BitSet piecesToRequest = getInterestedPieces(remotePeerId);
        BitSet interestedPieces = (BitSet)piecesToRequest.clone();
        piecesToRequest.flip(0, _totalNumPieces);
        piecesToRequest.and(interestedPieces);

        ArrayList<Integer> pieceIndices = new ArrayList<Integer>();
        for(int i =0; i < piecesToRequest.length(); i++) {
            if(piecesToRequest.get(i)) {
                pieceIndices.add(i);
            }
        }
        Random random_method = new Random();
        int index = random_method.nextInt(pieceIndices.size());

        return pieceIndices.get(index);
    }

    public void broadcastHavePiece(int pieceIndex) {
        for(int tempRemotePeerId: _connectedPeers.keySet()) {
            try {
                byte[] haveMessage = MessageFactory.genHaveMessage(pieceIndex);
                Socket tempSocket = _connectedPeers.get(tempRemotePeerId)._socket;
                ObjectOutputStream tempOutputStream = new ObjectOutputStream(tempSocket.getOutputStream());
                // TODO: test if flush causes any issues; may become a concurency issue
                tempOutputStream.flush();
                send(haveMessage, tempOutputStream, tempRemotePeerId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void savePiecesToFile() {
        String directory = "peer_"+_id;
        try {
            Files.createDirectories(Paths.get("/Your/Path/Here"));
        } catch (IOException e) {
            System.out.println("No write access to directory.");
            e.printStackTrace();
        }

        if (Files.isDirectory(Paths.get(directory))) {
            File file = new File(directory + "/" + _fileName);
            FileOutputStream fileOutput = null;
            try{
                fileOutput = new FileOutputStream(file.getAbsoluteFile());
                for(int i =0; i < _totalNumPieces; i++) {
                    fileOutput.write(_pieces.get(i));
                }
            }
            catch (IOException e){
                e.printStackTrace();
            } catch(NullPointerException e) {
                System.out.println("Missing piece in _pieces variable");
                e.printStackTrace();
            } finally {
                try {
                    if(fileOutput != null) {
                        fileOutput.close();
                    }
                } catch (IOException e) {
                    System.out.println("Error closing file");
                    e.printStackTrace();
                }
                
            }
        }
    }
}