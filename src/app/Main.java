package app;

import java.util.Scanner;

import app.tree.TreeMaintenance;

public class Main {
	public static void main(String args[]) {
		System.out.println("set the ID of this host");
		Scanner sc = new Scanner(System.in);
		String str = sc.next();
		int id = Integer.parseInt(str);
		if(id <= 0) {
			System.out.println("the ID must be a positive integer number");
			System.exit(1);
		}
		Debug.output("host starting with id: " + id);
		Node thisnode = new Node(id, null);
		//allocating the node/routing table
		NodeTable.getInstance(thisnode);
		//set up Connection
		Connection.start();
		//entering in maintenance state
		TreeMaintenance.startMaintenance();
		
		sc.close();
	}
}