import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;



public class PeerThread extends Thread{
	Socket connectTo;
	BufferedOutputStream out;
    BufferedInputStream in;
    Socket requestSocket;

    byte[] message;
    int peerID;

	public PeerThread(Socket connectTo, int peerID) 
    {   
        this.connectTo = connectTo;
        this.peerID = peerID;

        try 
        {
            out = new BufferedOutputStream(connectTo.getOutputStream());
            in = new BufferedInputStream(connectTo.getInputStream());
        } 
        catch (IOException error) 
        {
           System.out.println(error.getMessage());
        }
    }



    // private static class PeerThreadExecutor extends Thread {
    //     private Socket clientSocket;

    // }
	public void run()
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 6996);
			//initialize inputStream and outputStream
			out = new BufferedOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new BufferedInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{

                message = MessageFactory.genHandshakeMessage(peerID);

                sendMessage(message);


			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				connectTo.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
	void sendMessage(byte[] message)
	{
		try{
			//stream write the message
			out.write(message);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}


}





