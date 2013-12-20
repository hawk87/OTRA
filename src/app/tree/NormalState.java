package app.tree;

import app.Message;
import app.Node;
import app.Debug;
import app.NodeTable;

/**
 * Normal operational state. Under normal conditions: without any
 * balancing call or recovery operation. From children nodes we receive non-stop
 * SIZE messages that we use to inspect if the connection is set up and if some 
 * balancing operation must occur.
 */
class NormalState extends OperationalState implements Runnable {
	
	private int leftSize;
	private int rightSize;
	private boolean leftIsReady;
	private boolean rightIsReady;
	
	private final int waitmsec = 500;
	
	/**
	 * This counts the number of received SIZE messages from a single side when
	 * the other is not sending anything.
	 * That permit us to decide to TOUCH the children who's keeping silence
	 */
	private int waiting;

	NormalState() {
		Debug.output("Entering normal state...");
		NodeTable tbl = supervisor.getNodeTable();
		if((!tbl.hasLeftNode()) && (!tbl.hasRightNode()) && (!tbl.isThisRoot())) {
			//we are leaf, then we have to start a thread to signal parent
			//about our size
			Thread thread = new Thread(this);
			thread.start();
		}
	}
	
	public void run() {
		NodeTable tbl = supervisor.getNodeTable();
		Message.sendSize(tbl.getParent(), 1);
		try {
			Thread.sleep(waitmsec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
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
			
			NodeTable tbl = supervisor.getNodeTable();
			Node thisnode = tbl.getThisNode();
			if(n.getId() < thisnode.getId()) {
				if(tbl.hasLeftNode()) {
					//send JOIN_SEARCH to the left child
					Message.sendJoinSearch(tbl.getLeftNode(), n);
				} else {
					// joining node can be attached here, to the left
					Debug.output("node id: " + n.getId() + "attaches here to the"
							+ "left");
					//set joining node as left child
					tbl.setLeftNode(n);
					//signal to joining node that it can attach itself here
					Message.sendJoinSearch(n, thisnode);
				}
			} else if(n.getId() > thisnode.getId()) {
				if(tbl.hasRightNode()) {
					//send JOIN_SEARCH to the right child
					Message.sendJoinSearch(tbl.getRightNode(), n);
				} else {
					// joining node can be attached here, to the right
					Debug.output("node id: " + n.getId() + "attaches here to the"
							+ "right");
					//set joining node as right child
					tbl.setRightNode(n);
					//signal to joining node that it can attach itself here
					Message.sendJoinSearch(n, thisnode);
				}
			} else {
				// n has the same id of thisnode. ERROR
				System.out.println("ERROR: joining node has got the same id"
						+ "as this node: " + n.getId());
				System.exit(1);
			}
		}
		// else { this is not root -> do nothing }
	}
	
	void handleJoinSearch(Node n) {
		Debug.output("received a JOIN_SEARCH message");
		Debug.output("joining node id: " + n.getId());
		
		NodeTable tbl = supervisor.getNodeTable();
		Node thisnode = tbl.getThisNode();
		if(n.getId() < thisnode.getId()) {
			if(tbl.hasLeftNode()) {
				//forward the JOIN_SEARCH to the left child
				Message.sendJoinSearch(tbl.getLeftNode(), n);
			} else {
				//joining node can attach here, to the left
				Debug.output("node id: " + n.getId() + "attaches here to the"
						+ "left");
				//set joining node as left child
				tbl.setLeftNode(n);
				//signal to joining node that it can attach itself here
				Message.sendJoinSearch(n, thisnode);
			}
		} else if(n.getId() > thisnode.getId()) {
			if(tbl.hasRightNode()) {
				//forward the JOIN_SEARCH to the right child
				Message.sendJoinSearch(tbl.getRightNode(), n);
			} else {
				//joining node can attach here, to the right
				Debug.output("node id: " + n.getId() + "attaches here to the"
						+ "right");
				//set joining node as right child
				tbl.setRightNode(n);
				//signal to joining node that it can attach itself here
				Message.sendJoinSearch(n, thisnode);
			}
		} else {
			// n has the same id of thisnode. ERROR
			System.out.println("ERROR: joining node has got the same id"
					+ "as this node: " + n.getId());
			System.exit(1);
		}
	}
}
