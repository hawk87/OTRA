package app;

import java.net.InetAddress;

/**
 * This class represents every node/host in the network.
 * It associates ID number <--> IP address
 */
public class Node {
	private int identifier;
	private InetAddress address;
	
	public Node(int id, InetAddress adr) {
		identifier = id;
		address = adr;
	}
	
	public int getId() {
		return identifier;
	}
	
	public InetAddress getAddress() {
		return address;
	}

}
