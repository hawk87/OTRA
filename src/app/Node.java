package app;

/**
 * This class represents every node/host in the network.
 * It associates ID number <--> IP address
 */
public class Node {
	private int identifier;
	
	public Node(int id) {
		identifier = id;
	}
	
	public int getId() {
		return identifier;
	}

}
