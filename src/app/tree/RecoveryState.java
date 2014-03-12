package app.tree;

import app.Debug;
import app.Node;
import app.NodeTable;

class RecoveryState extends OperationalState {
	
	RecoveryState() {
		Debug.output("Entering recovery state");
		supervisor.forceService();
	}
	
	void handleSetRight(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setRightNode(x);
	}
	
	void handleSetParent(Node from, Node p) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setParent(p);
		//in this state this mean someone replaced our parent
		//return in normal state
		nextState(new NormalState());
	}
}
