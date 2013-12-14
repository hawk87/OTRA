package app.tree;

import app.Node;

class BalancingState extends OperationalState {
	
	private Node unbalanced;
	
	BalancingState(Node unbal) {
		System.out.println("entering balancing state");
		System.out.println("  id balance b-node: " + unbal.getId());
		unbalanced = unbal;
		//TODO set unbalanced to the proper state
	}

}
