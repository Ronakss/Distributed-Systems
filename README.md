
To Starting the sever
The server can be started by typing java Auction Server<port number>. is the desired port. Auction Server 8000. A server.bat file is provided. Running this file will compile all the Java classes and start the server on port 1234.
Starting the Clients
start the client You can start the client from the command line by typing Java Auction Client. where is the IP address of the Auction Server and is the port on which the Auction Server is running. Auction Client 0.0.0.0 8000. Contains the client.bat file, which when run opens a console terminal and connects to the Auction Server at IP address 0.0.0.0 and port number 1234.

Introduction 
This application is a simulator of an online auction system that allows multiple clients to connect, participate in auctions, and bid on items. When the client connects, it will display a list of items and what is currently on sale. When a customer bids, it must be higher than the current bid or they will be notified. The current auction item has a bid time of 1 minute, and when a customer makes a bid, the bid time is reset to 1 minute. At the end of the billing period, the item is either sold or unsold, and if the item is sold, it is removed from the auction and all customers are notified of the winning bid. If an item does not sell or does not reach the reserve price, all customers will be notified, and the item will be moved to the bottom of the auction item list. Customers can enter an item to view items currently for sale, enter an item to view all items in the auction, or exit to end the auction. The auction ends when all items are sold.

Relationships
The Auction can have many items.
The Auction can have one Bid Timer.
The Bid Timer can have one Bid Task.
The Auction Server can have one Auction.
The Auction Server can have many AuctionServerThreads.
The Auction Server Thread has a one-to-one relationship with an Auction Client Thread.
The Auction Client has a one-to-one relationship with an Auction Client Thread.

Implementation
The Auction Server receives incoming connections from the Auction Client, creates an Auction Server Thread and stores it in a list for further communication with the client. Auction Server uses the Auction class to handle all transactions for a list of items. The Auction Server can send and receive messages to individual clients or send messages to all clients simultaneously.
The Auction class contains her LinkedList queue of items, and the item at the top of the queue is the current item. This includes a Bid Timer to track the bidding period for items currently on sale. Bid Timer has a Bid Task class that implements the Runnable interface. Bid Task’s run method runs every second, allowing the auction to track the bidding period.
The Auction Client connects to the Auction Server and creates an Auction Client Thread for further communication with the server.
A client can send a bid to the server. The server uses her Auction class to validate bids and execute the necessary transactions. The server notifies the client of the result and displays the status of the auction.
