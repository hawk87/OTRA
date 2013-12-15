package app.tree;

import app.Node;
import app.NodeTable;

public class Responder implements Runnable {
	
	public void run() {
		Node p = new Node(2000);
		Node l = new Node(500);
		Node r = new Node(1500);
		Node t = new Node(1000);
		
		NodeTable tbl = new NodeTable(t);
		//tbl.setParent(p);
		tbl.setLeftNode(l);
		tbl.setRightNode(r);
		
		TreeMaintenance tm = TreeMaintenance.getInstance(tbl);
		
		tm.joinBroadcast(p);
		
		tm.sizeFromChild(r, 1);
		tm.sizeFromChild(l, 1);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(l, 1);
	}
}
