package app.tree;

import app.Utility;
import app.Node;
import app.Debug;
import app.NodeTable;
import app.OTRAFile;
import app.communication.FileTransfer;
import app.communication.MessageSystem;

/**
 * Normal operational state. Under normal conditions: without any balancing call
 * or recovery operation. From children nodes we receive non-stop SIZE messages
 * that we use to inspect if the connection is set up and if some balancing
 * operation must occur.
 */
class NormalState extends OperationalState {

	private boolean leftIsReady;
	private boolean rightIsReady;

	NormalState() {
		Debug.output("Entering normal state...");
		NodeTable tbl = NodeTable.getInstance();
		// signal to parent if any the height
		if(!tbl.isThisRoot()) {
			int s = supervisor.maxSubtreeHeight() + 1;
			MessageSystem.sendHeight(tbl.getParent(), s);
		}
	}
	
	/* 
	 * service() in this operational state touches all connected nodes
	 */
	void service() {
		NodeTable tbl = NodeTable.getInstance();
		//check left node
		if(tbl.hasLeftNode()) {
			if(!MessageSystem.sendTouch(tbl.getLeftNode())) {
				Debug.output("left node failing TOUCH: " + tbl.getLeftNode());
				Debug.output("removing from NodeTable");
				tbl.setLeftNode(null);
			}
		}
		// right node
		if(tbl.hasRightNode()) {
			if(!MessageSystem.sendTouch(tbl.getRightNode())) {
				Debug.output("right node failing TOUCH: " + tbl.getRightNode());
				Debug.output("removing from NodeTable");
				tbl.setRightNode(null);
			}
		}
		//check parent
		if(!tbl.isThisRoot()) {
			if(!MessageSystem.sendTouch(tbl.getParent())) {
				Debug.output("parent node failing TOUCH: " + tbl.getParent());
				Debug.output("TODO: regenerate a joining signal");
				tbl.setParent(null);
			}
		}
	}

	void handleHeight(Node n, int h) {
		Debug.output("received a HEIGHT message");
		Debug.output("  id: " + n.getId() + " height: " + h);
		
		NodeTable tbl = NodeTable.getInstance();
		
		if (tbl.isLeftNode(n)) {
			supervisor.leftHeight = h;
			leftIsReady = true;
		} else {
			supervisor.rightHeight = h;
			rightIsReady = true;
		}
		
		if(!tbl.hasLeftNode()) {
			leftIsReady = true;
		}
		if(!tbl.hasRightNode()) {
			rightIsReady = true;
		}

		if (rightIsReady && leftIsReady) {
			// unbalanced to the left
			if (supervisor.leftHeight - supervisor.rightHeight >= 2) {
				// get b-balance node
				Node b = tbl.getLeftNode();
				nextState(new BalancingState(b));
			}
			// unbalanced to the right
			else if (supervisor.rightHeight - supervisor.leftHeight >= 2) {
				// get b-balance node
				Node b = tbl.getRightNode();
				nextState(new BalancingState(b));
			} else {
				//send a HEIGHT signal to the parent if any
				if(!tbl.isThisRoot()) {
					MessageSystem.sendHeight(
							tbl.getParent(), supervisor.maxSubtreeHeight() + 1);
				}
			}

			leftIsReady = false;
			rightIsReady = false;
		} 
	}

	void handleJoinBroadcast(Node n) {
		NodeTable tbl = NodeTable.getInstance();
		
		if (tbl.isThisRoot()) {
			// ok, this node is root, so generate a JOIN_SEARCH
			// and forward if necessary
			Debug.output("root received a join broadcast message");
			Node thisnode = tbl.getThisNode();
			if (n.getId() < thisnode.getId()) {
				if (tbl.hasLeftNode()) {
					// send JOIN_SEARCH to the left child
					MessageSystem.sendJoinSearch(tbl.getLeftNode(), n);
				} else {
					// joining node can be attached here, to the left
					Debug.output("node id: " + n.getId()
							+ " attaches here to the left");
					// set joining node as left child
					tbl.setLeftNode(n);
					// signal to joining node that it can attach itself here
					MessageSystem.sendJoinSearch(n, thisnode);
				}
			} else if (n.getId() > thisnode.getId()) {
				if (tbl.hasRightNode()) {
					// send JOIN_SEARCH to the right child
					MessageSystem.sendJoinSearch(tbl.getRightNode(), n);
				} else {
					// joining node can be attached here, to the right
					Debug.output("node id: " + n.getId()
							+ " attaches here to the right");
					// set joining node as right child
					tbl.setRightNode(n);
					// signal to joining node that it can attach itself here
					MessageSystem.sendJoinSearch(n, thisnode);
				}
			} else {
				// n has the same id of thisnode. ERROR
				System.out.println("ERROR: joining node has got the same id"
						+ " as this node: " + n.getId());
				System.exit(1);
			}
		}
		// else { this is not root -> do nothing }
	}

	void handleJoinSearch(Node n) {
		Debug.output("received a JOIN_SEARCH message");
		Debug.output("joining node id: " + n.getId());

		NodeTable tbl = NodeTable.getInstance();
		Node thisnode = tbl.getThisNode();
		if (n.getId() < thisnode.getId()) {
			if (tbl.hasLeftNode()) {
				// forward the JOIN_SEARCH to the left child
				MessageSystem.sendJoinSearch(tbl.getLeftNode(), n);
			} else {
				// joining node can attach here, to the left
				Debug.output("node id: " + n.getId()
						+ " attaches here to the left");
				// set joining node as left child
				tbl.setLeftNode(n);
				// signal to joining node that it can attach itself here
				MessageSystem.sendJoinSearch(n, thisnode);
			}
		} else if (n.getId() > thisnode.getId()) {
			if (tbl.hasRightNode()) {
				// forward the JOIN_SEARCH to the right child
				MessageSystem.sendJoinSearch(tbl.getRightNode(), n);
			} else {
				// joining node can attach here, to the right
				Debug.output("node id: " + n.getId()
						+ " attaches here to the right");
				// set joining node as right child
				tbl.setRightNode(n);
				// signal to joining node that it can attach itself here
				MessageSystem.sendJoinSearch(n, thisnode);
			}
		} else {
			// n has the same id of thisnode. ERROR
			System.out.println("ERROR: joining node has got the same id"
					+ "as this node: " + n.getId());
			System.exit(1);
		}
	}
	
	void handleBalance() {
		//TODO
	}

	void handlePrint() {
		Debug.output("received a PRINT message");

		NodeTable tbl = NodeTable.getInstance();
		Node thisnode = tbl.getThisNode();
		
		if(tbl.isThisRoot()) {
			//we are root then we have to print nothing
			Debug.output("dropped");
		}
		else if (!tbl.hasLeftNode() && !tbl.hasRightNode()) {
			Debug.output("accepted");
			byte[] data = {};
			data = Utility.appendArray(data,
					Utility.intToByte(thisnode.getId()));
			data = Utility.appendArray(data, Utility.intToByte(-1));
			data = Utility.appendArray(data, Utility.intToByte(-1));

			OTRAFile file = new OTRAFile(-1, data);
			FileTransfer.send(tbl.getParent().getAddress(), file);
		} else {
			Debug.output("dropped");
		}
	}
}
