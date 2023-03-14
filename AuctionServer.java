import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class AuctionServer implements Runnable {
	
	private AuctionServerThread clients[]   = new AuctionServerThread[50];
	private ServerSocket        server      = null;
	private Thread              thread      = null;
	private int                 clientCount = 0;
	private Auction             auction     = null;
	
	public AuctionServer(int port) {
		try {
			System.out.println("\nBinding to port " + port + ", please wait  ...");
			// Create the server.
			server = new ServerSocket(port);
			System.out.println("Server started: " + server.getInetAddress() + "\n");
			// Start the server.
			start();
			// Create the auction.
			auction = new Auction(this);
			// Display Auction info.
			System.out.println(Auction.NAME + "\n");
			System.out.print(auction);
			System.out.println("\n" + auction.showCurrentItem() );	
			// Start the auction.
			auction.start();
		} catch(IOException ioe) {
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
		}
	}
	
	public void run() {
		while(thread != null) {
			try {
				addThread(server.accept());
				int pause = (int)(Math.random()*3000);
				Thread.sleep(pause);
			} catch(IOException ioe){
				System.out.println("Server accept error: " + ioe);
				stop();
			} catch (InterruptedException e) {
				//System.out.println(e);
			}
		}
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop(){
		thread = null;
	}
	
	public int getClientsCount(){
		return clientCount;
	}
	
	public AuctionServerThread[] getClients(){
		return clients;
	}
	
	private int findClient(int ID) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}
	
	public synchronized void broadcast(int ID, String input) {
		
		// The client wants to exit the auction.
		if(input.equals("exit")) {
			clients[findClient(ID)].send("Thank you for visiting\n  " + Auction.NAME );
			remove(ID);
			return;
		}
		
		// The client wants to see the current item.
		if(input.equals("item")) {
			clients[findClient(ID)].send( auction.showCurrentItem() );
			return;
		}
		
		// The client wants to see all the items.
		if(input.equals("items")) {
			clients[findClient(ID)].send( auction.toString() );
			return;
		}
		
		// Ensure only Euros are entered.
		if(!isNum(input)){
			clients[findClient(ID)].send( "Please enter your bid in Euros." );
			return;
		}
		
		// Validate the bid.
		int bid = Integer.parseInt(input);
		if(!auction.validBid(bid)){
			// Inform the client that the bid was rejected.
			clients[findClient(ID)].send( String.format("\nYour bid of %d was rejected!\nThe current bid is %d\n", bid, auction.getBid()) );
		} else {
			// The bid was accepted.
			for(int i = 0; i < clientCount; i++) {
				if(clients[i].getID() != ID)
					// Inform all other clients of the accepted bid.
					clients[i].send( String.format("\nClient %d bid %d\n", ID, bid) );
				else
					// Inform the current client of the accepted bid.
					clients[i].send( "\n    Your bid was accepted!\n" );
				// Show the current item to all clients.
				clients[i].send( auction.showCurrentItem() );
			}
			// Show the updated bid on the server.
			System.out.println( String.format("\nClient %d bid %d\n", ID, bid) );
			System.out.println( auction.showCurrentItem() );
		}
		notifyAll();
	}
	
	public synchronized void remove(int ID) {
		int pos = findClient(ID);
		if(pos >= 0) {
			AuctionServerThread toTerminate = clients[pos];
			if(pos < clientCount-1)
				for(int i = pos+1; i < clientCount; i++)
					clients[i-1] = clients[i];
			clientCount--;
			try {
				toTerminate.close();
			} catch(IOException ioe) {
				System.out.println("Error closing thread: " + ioe);
			}
			toTerminate = null;
			System.out.println("Client " + ID + " has left the auction.");
			notifyAll();
		}
	}
	
	private void addThread(Socket socket) {
		if(clientCount < clients.length) {
			System.out.println("Client " + socket.getPort() + " has entered the auction.\n");
			try {
				clients[clientCount] = new AuctionServerThread(this, socket);
				clients[clientCount].open();
				clients[clientCount].start();
				// Show the auction items to the client.
				clients[clientCount].send(auction.toString());
				// Show the current item to the client.
				clients[clientCount].send(auction.showCurrentItem());
				clientCount++;
			} catch(IOException ioe) {
				System.out.println("Error opening thread: " + ioe);
			}
		} else
			System.out.println("Client refused: maximum " + clients.length + " reached.");
	}
	
	public boolean isNum(String strNum) {
		boolean ret = true;
		try {
			Integer.parseInt(strNum);
		}catch (NumberFormatException e) {
			ret = false;
		}
		return ret;
	}
	
	public static void main(String args[]) {
		AuctionServer server = null;
		if (args.length != 1)
			System.out.println("Usage: java AuctionServer port");
		else
			server = new AuctionServer(Integer.parseInt(args[0]));
	}
}