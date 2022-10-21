// this is a helper class that stores the info
// for the Peer class.
public class PeerData 
{

    public int id;
    public String hostname;
    int listeningPort;
    boolean hasFile;


    public PeerData(int id, String hostname, int listeningPort, boolean hasFile) 
    {
        this.id = id;
        this.hostname = hostname;
        this.listeningPort = listeningPort;
        this.hasFile = hasFile;
    }
    
}
