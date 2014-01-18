package app.tree;

import app.Debug;
import app.Node;
import app.NodeTable;
import app.communication.MessageSystem;

class BalancingBState extends OperationalState {
	
	private boolean singleRotation;
	
	BalancingBState(Node from) {
		Debug.output("Entering B-balancing state");
		NodeTable tbl = NodeTable.getInstance();
		if(from.getId() < tbl.getThisNode().getId()) {
			//then we are unbalanced to the right
			if(supervisor.leftHeight - supervisor.rightHeight > 0) {
				//then we are in double rotation case
				Debug.output("double rotation from the left");
				MessageSystem.sendBalance(tbl.getLeftNode(), from);
				singleRotation = false;
			} else {
				//single rotation
				Debug.output("single rotation to the left");
				//first step: move d2 to a
				MessageSystem.sendSetRight(from, tbl.getLeftNode());
				if(tbl.hasLeftNode()) {
					MessageSystem.sendSetParent(tbl.getLeftNode(), from);
				}
				singleRotation = true;
				//go waiting for next movements
			}
		} else if(from.getId() > tbl.getThisNode().getId()) {
			//then we are unbalanced to the left
			if(supervisor.rightHeight - supervisor.leftHeight > 0) {
				//then we are in double rotation case
				Debug.output("double rotation from the right");
				MessageSystem.sendBalance(tbl.getRightNode(), from);
				singleRotation = false;
			} else {
				//single rotation
				Debug.output("single rotation to the right");
				MessageSystem.sendSetLeft(from, tbl.getRightNode());
				if(tbl.hasRightNode()) {
					MessageSystem.sendSetParent(tbl.getRightNode(), from);
				}
				singleRotation = true;
				//go waiting for next movements
			}
		}
	}
	
	void handleSetParent(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setParent(x);
		if(singleRotation) {
			//single rotation
			MessageSystem.sendSetParent(from, tbl.getThisNode());
			if(from.getId() < tbl.getThisNode().getId()) {
				tbl.setLeftNode(from);
			} else {
				tbl.setRightNode(from);
			}
			//we have completed the rotation, go normal..
			nextState(new NormalState());
		} else {
			//double rotation
			nextState(new NormalState());
		}
	}
	
	void handleSetLeft(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setLeftNode(x);
	}
	
	void handleSetRight(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setRightNode(x);
	}
}
