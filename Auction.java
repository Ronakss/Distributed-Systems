import java.util.Timer;
import java.util.TimerTask;
import java.util.LinkedList;

public class Auction {
	
	public final static String NAME = "Ronal's Auction";
	
	private int bid_period         = 45;
	private int bid_period_tracker = bid_period;
	
	private int current_bid         = 0;
	private LinkedList<Item> items  = null;
	
	private Auction auction = null;
	private AuctionServer server = null;
	
	public Auction(AuctionServer _server){
		auction = this;
		server = _server; // Communicate with the server.
		current_bid = 0;
		items = new LinkedList<Item>();
		items.add( new Item("Latitude 5330", "Dell Laptop", 2000) );
		items.add( new Item("iPhone 13", "Apple iPhone", 3000) );
		items.add( new Item("Lenovo 33i", "Lenovo Laptop 32GB", 4000) );
		items.add( new Item("Samsung S22FE", "Samsung Phone", 5000) );
		items.add( new Item("Mac Air", "Apple Mac Laptop", 6000) );
	}
	
	// Start the auction timer.
	public void start(){
		new BidTimer();
	}
	
	// Verify the bid is valid.
	public boolean validBid(int bid){
		if(bid > current_bid) {
			bid_period_tracker = bid_period;
			current_bid = bid;
			return true;
		}
		return false;
	}
	
	// Reset the current bid & remove the item from the list.
	public Item doTransaction(){
		if(current_bid > items.element().getReserve()){
			current_bid = 0;
			return items.remove();
		}
		return null;
	}
	
	// Send the first element to the back of the queue.
	public void sendToBack(){
		Item removed = items.remove();
		items.add( removed );
	}
	
	// Get all items.
	public LinkedList<Item> getItems(){
		return items;
	}
	
	// Get the current bid.
	public int getBid(){
		return current_bid;
	}
	
	// Retrieve but not remove.
	public Item currentItem(){
		return items.element();
	}
	
	// Show the status of the current item for sale.
	public String showCurrentItem(){
		return String.format("Current Item\n%-9s%-14s%s\n%-9s%-14s%d\nCurrent Bid: %d\n%d secs remaining\n",
		"Name", "Description", "Reserve", currentItem().getName(), currentItem().getDescription(), currentItem().getReserve(), getBid(), bid_period_tracker);
	}
	
	// Show the list of items in the auction.
	@Override
	public String toString(){
		String result = String.format("Auction Items\n%-9s%-14s%s\n", "Name", "Description", "Reserve");
		for(Item item : items){
			result += String.format("%-9s%-14s%d\n", item.getName(), item.getDescription(), item.getReserve());
		}
		return result;
	}
	
	
	
	class BidTimer	{
		private Timer timer;
		
		public BidTimer() {
			timer = new Timer();
			timer.schedule(new BidTask(), 1000);
		}
		
		public void stop(){
			timer.cancel();
		}
		
		class BidTask extends TimerTask {
			@Override
			public void run() {
				
				// Show the time left every 10 seconds.
				if(bid_period_tracker < 60 && bid_period_tracker > 0 && bid_period_tracker % 5 == 0){
					// Get the list of clients.
					AuctionServerThread clients[] = server.getClients();
					// Set the output.
					String output = bid_period_tracker + " seconds left";
					// Send the output to all the clients.
					for(int i = 0; i < server.getClientsCount(); i++)
						clients[i].send(output);
					// Show the output on the server.
					System.out.println(output);
				}
					
				// The time is up.
				if(bid_period_tracker <= 0){
					stop();
					// Reset the bid_period_tracker.
					bid_period_tracker = bid_period;
					
					String output = "Time's up!\n";
					System.out.println(output);
					
					// Get the list of clients.
					AuctionServerThread clients[] = server.getClients();
					
					// Check if the item was sold.
					Item sold = auction.doTransaction();
					
					if(sold != null){
						// The item was sold.
						output = "\n************* SOLD ************\n";
						// Inform all the clients.
						for(int i = 0; i < server.getClientsCount(); i++)
							clients[i].send(output);
						// Inform the server.
						System.out.println(output);
					} else {
						// The item was not sold.
						output = "\n************* NOT SOLD ************\n";
						output += "\nItem will come back around\n";
						// Inform all the clients.
						for(int i = 0; i < server.getClientsCount(); i++)
							clients[i].send(output);
						// Inform the server.
						System.out.println(output);
						// Send the item to the back of the queue.
						auction.sendToBack();
					}
						
					// If there is still items in the auction.
					if(items.size() > 0){
						// Set the output to show the auction items & the current item.
						output = auction.toString() + "\n" + auction.showCurrentItem();
					} else {
						// Set the output to the auction closed message.
						output = NAME + " is now closed.";
					}
						
					// Show the output to all the clients.
					for(int i = 0; i < server.getClientsCount(); i++) {
						clients[i].send(output);
					}
					// Show the output to the server.
					System.out.println(output);
				}
				
				
				// If there are items in the auction.
				if(items.size() > 0){
					// Decrement the bid_period_tracker.
					bid_period_tracker--;
					new BidTimer();
				}
			}
		}
	}
	
	
}