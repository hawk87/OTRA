package app.tree;

import java.net.InetAddress;

import app.Debug;
import app.Node;
import app.NodeTable;
import app.communication.MessageSystem;

class RecoveryState extends OperationalState {
	private Node sibling;
	
	RecoveryState() {
		Debug.output("Entering recovery state");
		supervisor.forceService();
	}
	
	RecoveryState(Node sib) {
		sibling = sib;
		supervisor.forceService();
	}
	
	void service() {
		NodeTable tbl = NodeTable.getInstance();
		
		if(sibling == null) { //only execute once
			for(int i=0; i < 5; i++) {
				try {
					MessageSystem.sendDisconnected(tbl.getParent());
					Thread.sleep(300);
				} catch (InterruptedException e) { }
				//check if we received a response
				if(sibling != null)
					break;
			}
		}
		if(sibling == null) {
			//then we can deduce that there is no sibling network
			Debug.output("no sibling network...");
			//then we try to rejoin the network, if any
			tbl.setParent(null);
			nextState(new JoiningState());
		} else if(tbl.getThisNode().getId() < tbl.getParent().getId()) {
			//then there are two subtree that have to be merged
			if(tbl.hasRightNode()) {
				MessageSystem.sendRecoveryFindMax(tbl.getRightNode(),
						tbl.getThisNode(), sibling);
			} else {
				MessageSystem.sendSetParent(sibling, tbl.getThisNode());
				tbl.setParent(null);
				nextState(new JoiningState());
			}
		}
	}
	
	void handleSetParent(Node from, Node p) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setParent(p);
		//in this state this mean someone replaced our parent
		nextState(new NormalState());
	}
	
	void handleDscnnResponse(Node sib) {
		sibling = sib;
	}
	
	void handleDisconnected(Node disc, InetAddress from) {
		NodeTable tbl = NodeTable.getInstance();
		
		if(tbl.getThisNode().getAddress().equals(from))
			//because we received the broadcast message we sent
			return;
		
		if(disc.equals(tbl.getParent())) {
			//we are the sibling
			MessageSystem.sendDscnnResponse(from, tbl.getThisNode());
		}
	}
}
