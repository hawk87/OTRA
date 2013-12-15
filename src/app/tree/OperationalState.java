package app.tree;

import app.Node;

abstract class OperationalState {

	protected static TreeMaintenance supervisor;
	{
		supervisor = TreeMaintenance.getInstance();
	}
	
	// pass the control to another OperationalState
	final void nextState(OperationalState nextState) {
		supervisor.changeState(nextState);
	}
	
	//
	// INCOMING MESSAGES HANDLES
	//
	
	void sizeFromChild(Node n, int s) {
		//nop
		// subclasses that want to listen for this message must override
	}
	
	void joinBroadcast(Node n) {	
	}
	
	void joinSearch(Node n) {
	}
	
}
