package app.tree;

import app.Node;

/**
 * Fictional operational state.
 * Logic common to all operational state, in fact every class that wants to
 * listen for incoming messages must extends this one.
 */
abstract class OperationalState {

	/**
	 * A reference to the TreeMaintenance instance that is used, as example,
	 * for retrieving the node table, ecc..
	 */
	protected static TreeMaintenance supervisor;
	// static initializer
	{
		supervisor = TreeMaintenance.getInstance();
	}
	
	/**
	 * Pass the control to the next operational state.
	 */
	final void nextState(OperationalState nextState) {
		supervisor.changeState(nextState);
	}
	
	//
	// INCOMING MESSAGES HANDLES
	//
	//NOP operations
	//subclasses that want to listen for these messages must override
	
	void handleHeight(Node n, int s) {
	}
	
	void handleJoinBroadcast(Node n) {	
	}
	
	void handleJoinSearch(Node n) {
	}
	
	void handlePrint(){
	}
	
	void service() {
	}
	
	void handleBalance(Node from, Node anode) {
	}
	
	void handleSetParent(Node from, Node x) {
	}
	
	void handleSetLeft(Node from, Node x) {
	}
	
	void handleSetRight(Node from, Node x) {
	}
	
}
