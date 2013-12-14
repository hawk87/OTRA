package app.tree;

import app.Node;

abstract class OperationalState {

	protected static TreeMaintenance supervisor;
	{
		supervisor = TreeMaintenance.getInstance();
	}
	
	void sizeFromChild(Node n, int s) {
		//nop
		// subclasses that want to listen for this message must override
	}
	
	final void nextState(OperationalState nextState) {
		supervisor.changeState(nextState);
	}
	
}
