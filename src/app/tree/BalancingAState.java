package app.tree;

import app.Debug;
import app.Node;
import app.NodeTable;
import app.communication.MessageSystem;

class BalancingAState extends OperationalState {
	
	private Node bNode;
	
	BalancingAState(Node bnode) {
		Debug.output("Entering A-balancing state");
		bNode = bnode;
		MessageSystem.sendBalance(bNode, NodeTable.getInstance().getThisNode());
	}
	
	void handleSetParent(Node from, Node x) {
		Debug.output("received SET_PARENT. id: " + (x==null ? "null" : x.getId()));
		NodeTable tbl = NodeTable.getInstance();
		tbl.setParent(x);
		//we have completed the rotation, go normal..
		nextState(new NormalState());
	}
	
	void handleSetLeft(Node from, Node x) {
		Debug.output("received a SET_LEFT. id: " + (x==null ? "null" : x.getId()));
		NodeTable tbl = NodeTable.getInstance();
		tbl.setLeftNode(x);
		//then transfer the parent
		MessageSystem.sendSetParent(from, tbl.getParent());
		if(!tbl.isThisRoot()) {
			if(tbl.getThisNode().getId() < tbl.getParent().getId()) {
				//then we are in the left subtree of the parent
				MessageSystem.sendSetLeft(tbl.getParent(), from);
			} else
				//then we are in the right subtree of the parent
				MessageSystem.sendSetRight(tbl.getParent(), from);
		}
	}
	
	void handleSetRight(Node from, Node x) {
		Debug.output("received a SET_RIGHT. id: " + (x==null ? "null" : x.getId()));
		NodeTable tbl = NodeTable.getInstance();
		tbl.setRightNode(x);
		//then transfer the parent
		MessageSystem.sendSetParent(from, tbl.getParent());
		if(!tbl.isThisRoot()) {
			if(tbl.getThisNode().getId() < tbl.getParent().getId()) {
				//then we are in the left subtree of the parent
				MessageSystem.sendSetLeft(tbl.getParent(), from);
			} else
				//then we are in the right subtree of the parent
				MessageSystem.sendSetRight(tbl.getParent(), from);
		}
	}
}
