package app.tree;

import app.Node;
import app.Debug;

class NormalState extends OperationalState {
	
	private int leftSize;
	private int rightSize;
	private boolean leftIsReady;
	private boolean rightIsReady;
	
	// this counts the number of received SIZE messages from a single side
	// this permit us to decide to TOUCH the children who's keeping silence
	private int waiting;
	
	void handleSize(Node n, int s) {
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
				Debug.output("need to send SIZE to the parent.. FIXME");
				//TODO send a SIZE signal to the parent
			}
			
			leftIsReady = false;
			rightIsReady = false;
			waiting = 0;
		}
		else if(leftIsReady || rightIsReady) {
			if (waiting > 2) {
				Debug.output("needed touching operation.. FIXME");
				//TODO
				// send TOUCH message
			}
		}
		//normal
		//add a wait turn
		waiting++;
	}

	void handleJoinBroadcast(Node n) {
		if(supervisor.getNodeTable().isThisRoot()) {
			//ok, this node is root, so generate a JOIN_SEARCH
			//and forward if necessary
			Debug.output("root received a join broadcast message");
			
			Node thisnode = supervisor.getNodeTable().getThisNode();
			if(n.getId() < thisnode.getId()) {
				if(supervisor.getNodeTable().hasLeftNode()) {
					//TODO
					//send JOIN_SEARCH to the left child
				} else {
					//TODO
					// joining node can be attached here, to the left
				}
			} else if(n.getId() > thisnode.getId()) {
				if(supervisor.getNodeTable().hasRightNode()) {
					//TODO
					//send JOIN_SEARCH to the right child
				} else {
					//TODO
					// joining node can be attached here, to the right
				}
			} else {
				// n has the same id of thisnode. ERROR
				//TODO
				//send a FUCK message to n
			}
		}
		// else { this is not root -> do nothing }
	}
	
	void handleJoinSearch(Node n) {
		Debug.output("received a JOIN_SEARCH message");
		Debug.output("joining node id: " + n.getId());
		
		Node thisnode = supervisor.getNodeTable().getThisNode();
		if(n.getId() < thisnode.getId()) {
			if(supervisor.getNodeTable().hasLeftNode()) {
				//TODO
				//forward the JOIN_SEARCH to the left child
			} else {
				//TODO
				//joining node can attach here, to the left
			}
		} else if(n.getId() > thisnode.getId()) {
			if(supervisor.getNodeTable().hasRightNode()) {
				//TODO
				//forward the JOIN_SEARCH to the right child
			} else {
				//TODO
				//joining node can attach here, to the right
			}
		} else {
			// n has the same id of thisnode. ERROR
			//TODO
			//send a FUCK message to n
		}
	}
}
