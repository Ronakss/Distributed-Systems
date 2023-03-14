import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class AuctionClient implements Runnable {
	
	private Socket socket = null;
	private Thread thread = null;
	private BufferedReader console = null;
	private DataOutputStream streamOut = null;
	private AuctionClientThread client = null;
	
	public AuctionClient(String address, int port) {
		System.out.println("Establishing connection. Please wait ...");
		try{
			socket = new Socket(address, port);
			// Show a welcome message.
			System.out.println("\n Welcome to Ronak's Auction\n");
			start();
			// Show the client their id.
			System.out.println("Your ID is: " + socket.getLocalPort() + "\n");
		} catch(UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch(IOException ioe){
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}
	
	public void run() {
		while(thread != null) {
			try {
				String message = console.readLine();
				streamOut.writeUTF(message);
				streamOut.flush();
			} catch(IOException ioe) {
				System.out.println("Sending error: " + ioe.getMessage());
				stop();
			}
		}
	}
	
	public void handle(String msg) {
		System.out.println(msg);
	}
	
	public void start() throws IOException {
		console = new BufferedReader(new InputStreamReader(System.in));
		streamOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null) {
			client = new AuctionClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop() {
		try {
			if (console != null)  console.close();
			if (streamOut != null)  streamOut.close();
			if (socket != null)  socket.close();
		} catch(IOException ioe) {
			System.out.println("Error closing ...");
		}
		client.close();
		thread = null;
	}
	
	public static void main(String args[]) {
		AuctionClient client = null;
		if (args.length != 2)
			System.out.println("Usage: java AuctionClient host port");
		else
			client = new AuctionClient(args[0], Integer.parseInt(args[1]));
	}
}