import java.io.*;
import java.net.*;


public class Server {
    private ServerSocket serverSocket;
    

    public void start(int listeningPort) {
        try {
            serverSocket = new ServerSocket(listeningPort);
        } catch(IOException ex){

        }
        while (true)
            try {
                // Create and start response hander thread once a connection is made
                new ResponseHandler(serverSocket.accept()).start();
            } catch (IOException ex) {
            }
            
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            
        }
        
    }

    private static class ResponseHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in; //stream read from the socket
        private ObjectOutputStream out;
        int peerID;

        public ResponseHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(clientSocket.getInputStream());

                try{

                    // anticipate TCP handshake
                    try {
                        // byte[] handshake = in.readNBytes(32);
                        String header = new String(in.readNBytes(18));
                        byte[] zeros = in.readNBytes(10);
                        int peerID = in.readInt();
                        this.peerID = peerID;
                    } catch(IOException ex) {

                    }

                    // read normal messages now
					while(true)
					{   
                        int length = in.readInt();
                        byte[] payload = in.readNBytes(length);

                        // determine what to send back
						// sendMessage(payload);
					}
				}
				catch(IOException ex){
						System.err.println("Data received in unknown format");
					}
            }
            catch(IOException ioException){
				System.out.println("Disconnect with Client ");
			}
			finally{
				// Close connections
				try{
					in.close();
					out.close();
					clientSocket.close();
				}
				catch(IOException ioException){
					System.out.println("Disconnect with Client ");
				}
			}
        }

        public void sendMessage(byte[] message)
		{
			try{
				out.writeObject(message);
				out.flush();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

    }   
}