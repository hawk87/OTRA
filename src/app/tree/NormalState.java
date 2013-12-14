package app.tree;

import app.Node;

class NormalState extends OperationalState {
	
	private int leftSize;
	private int rightSize;
	private boolean leftIsReady;
	private boolean rightIsReady;
	
	// this counts the number of received SIZE messages from a single side
	// this permit us to TOUCH the children who's keeping quiet
	private int waiting;
	
	void sizeFromChild(Node n, int s) {
		if(supervisor.getNodeTable().isLeftNode(n)) {
			leftSize = s;
			leftIsReady = true;
		} else {
			rightSize = s;
			rightIsReady = true;
		}

		if(rightIsReady && leftIsReady) {
			//unbalanced to the left
			if(leftSize - rightSize >= 2) {
				//get b-balance node
				Node b = supervisor.getNodeTable().getLeftNode();
				nextState(new BalancingState(b));
			}
			//unbalanced to the right
			else if (rightSize - leftSize >= 2) {
				//get b-balance node
				Node b = supervisor.getNodeTable().getRightNode();
				nextState(new BalancingState(b));
			} else {
				System.out.println("normal state:");
				System.out.println("  need to send SIZE to the parent.. FIXME");
				//TODO send a SIZE signal to the parent
			}
			
			leftIsReady = false;
			rightIsReady = false;
			waiting = 0;
		}
		else if(leftIsReady || rightIsReady) {
			if (waiting > 2) {
				System.out.println("normal state:");
				System.out.println("  needed touching operation.. FIXME");
				//TODO
				// send TOUCH message
			}
		}
		//normal
		//add a wait turn
		waiting++;
	}
}
