public class Peer {

    public int id;
    public String hostname;
    int listeningPort;
    boolean hasFile;

    public Peer(int id, String hostname, int listeningPort, boolean hasFile) {
        this.id = id;
        this.hostname = hostname;
        this.listeningPort = listeningPort;
        this.hasFile = hasFile;
    }
    
}
