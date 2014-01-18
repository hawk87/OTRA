package app.tree;

import app.Debug;
import app.Node;
import app.NodeTable;
import app.communication.MessageSystem;

class BalancingCState extends OperationalState {

	private Node bNode;
	
	BalancingCState(Node anode, Node bnode) {
		Debug.output("Entering C-balancing state");
		bNode = bnode;
		NodeTable tbl = NodeTable.getInstance();
		if(anode.getId() < tbl.getThisNode().getId()) {
			//then we are unbalanced to the right
			MessageSystem.sendSetRight(anode, tbl.getLeftNode());
			if(tbl.hasLeftNode()) {
				MessageSystem.sendSetParent(tbl.getLeftNode(), anode);
			}
			MessageSystem.sendSetLeft(bnode, tbl.getRightNode());
			if(tbl.hasRightNode()) {
				MessageSystem.sendSetParent(tbl.getRightNode(), bnode);
			}
		} else {
			//then we are unbalanced to the left
			MessageSystem.sendSetLeft(anode, tbl.getRightNode());
			if(tbl.hasRightNode()) {
				MessageSystem.sendSetParent(tbl.getRightNode(), anode);
			}
			MessageSystem.sendSetRight(bnode, tbl.getLeftNode());
			if(tbl.hasLeftNode()) {
				MessageSystem.sendSetParent(tbl.getLeftNode(), bnode);
			}
		}
	}
	
	void handleSetParent(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setParent(x);
		
		MessageSystem.sendSetParent(from, tbl.getThisNode());
		MessageSystem.sendSetParent(bNode, tbl.getThisNode());
		if(from.getId() < tbl.getThisNode().getId()) {
			tbl.setLeftNode(from);
			tbl.setRightNode(bNode);
		} else {
			tbl.setRightNode(from);
			tbl.setLeftNode(bNode);
		}
		//we have completed the double rotation, go normal..
		nextState(new NormalState());
	}
}
