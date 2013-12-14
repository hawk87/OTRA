package app.tree;

import app.Node;
import app.NodeTable;

public class Responder implements Runnable {
	
	public void run() {
		NodeTable tbl = new NodeTable();
		Node p = new Node(1000);
		tbl.setParent(p);
		Node l = new Node(500);
		tbl.setLeftNode(l);
		Node r = new Node(1500);
		tbl.setRightNode(r);
		
		TreeMaintenance tm = TreeMaintenance.getInstance(tbl);
		tm.sizeFromChild(r, 1);
		tm.sizeFromChild(l, 1);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(r, 3);
		tm.sizeFromChild(l, 1);
	}
}
