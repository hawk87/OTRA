package app.tree;

import app.Debug;
import app.Node;
import app.NodeTable;
import app.communication.MessageSystem;

class RecoveryState extends OperationalState {
	private Node sibling;
	
	RecoveryState() {
		Debug.output("Entering recovery master state");
	}
	
	void service() {
		NodeTable tbl = NodeTable.getInstance();
		if(sibling == null) {
			//send broadcast to discover the other pending node
			MessageSystem.sendDisconnected(tbl.getParent());
		}
	}
	
	void handleDscnnResponse(Node sib) {
		NodeTable tbl = NodeTable.getInstance();
		sibling = sib;
		
		if(tbl.hasRightNode()) {
			MessageSystem.sendRecoveryFindMax(
					tbl.getRightNode(), tbl.getThisNode(), sibling);
		} else {
			MessageSystem.sendSetParent(sibling, tbl.getThisNode());
			tbl.setRightNode(sibling);
			//at this point we are the root of a orphan tree
			//we try to re-join the network if present
			nextState(new JoiningState());
		}
	}
}
