package app;

import app.tree.TreeMaintenance;

public class Main {
	public static void main(String args[]) {
		Node thisnode = new Node(9999, null);
		//allocating the node/routing table
		NodeTable.getInstance(thisnode);
		//entering in maintenance state
		TreeMaintenance.startMaintenance();
		//TODO
		//call Connection ????
	}
}
