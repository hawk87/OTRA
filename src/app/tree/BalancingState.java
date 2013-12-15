package app.tree;

import app.Debug;
import app.Node;

class BalancingState extends OperationalState {
	
	private Node unbalanced;
	
	BalancingState(Node unbal) {
		Debug.output("entering balancing state");
		Debug.output("id balance b-node: " + unbal.getId());
		unbalanced = unbal;
		//TODO set unbalanced to the proper state
	}

}
