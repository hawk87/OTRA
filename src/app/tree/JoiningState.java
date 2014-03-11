package app.tree;

import app.Debug;
import app.Node;
import app.NodeTable;
import app.communication.MessageSystem;

/**
 * Initial operational state. We send broadcast signal to inspect if there is
 * some OTRA network already up and running. Otherwise we set ourself as root
 * and listen for other joining nodes.
 */
class JoiningState extends OperationalState {
	
	private final int START_DELAY = 150;
	
	private boolean responded;
	
	JoiningState() {
		Debug.output("Entering Joining state");
		if(supervisor != null)
			supervisor.forceService();
	}
	
	void service() {
		NodeTable tbl = NodeTable.getInstance();
		
		if(!responded) { //only execute once
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {	}
			for(int i=0; i < 5; i++) {
				try {
					MessageSystem.sendJoinBroadcast(tbl.getThisNode().getId());
					Thread.sleep(START_DELAY * (int) Math.pow(1.5, i));
				} catch (InterruptedException e) { }
				//check if we received a response
				if(responded)
					break;
			}
		}
		if(!responded) {
			//then we can deduce that there is no online network
			Debug.output("no online network... we become root");
			nextState(new NormalState());
		}
		//ELSE we have received a response from root, then we have to wait
		//for the JoiningSearch signal.
	}
	
	void handleJoinResponse() {
		responded = true;
	}
	
	/*
	 * In the joining state this message comes from the node who's going to
	 * host us in the tree.
	 */
	void handleJoinSearch(Node n) {
		Debug.output("we attach to node with id: " + n.getId());
		//set as parent the node telling us that it has a free post to attach to.
		NodeTable.getInstance().setParent(n);
		//go into normal operational state
		nextState(new NormalState());
	}
}
