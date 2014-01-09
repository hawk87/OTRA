package app.tree;

import app.Utility;
import app.Node;
import app.Debug;
import app.NodeTable;
import app.OTRAFile;
import app.communication.FileTransfer;
import app.communication.Message;

/**
 * Normal operational state. Under normal conditions: without any balancing call
 * or recovery operation. From children nodes we receive non-stop SIZE messages
 * that we use to inspect if the connection is set up and if some balancing
 * operation must occur.
 */
class NormalState extends OperationalState {

	private boolean leftIsReady;
	private boolean rightIsReady;

	/**
	 * This counts the number of received SIZE messages from a single side when
	 * the other is not sending anything. That permit us to decide to TOUCH the
	 * children who's keeping silence
	 */
	private int waiting;

	NormalState() {
		Debug.output("Entering normal state...");
		NodeTable tbl = supervisor.getNodeTable();
		// signal to parent if any the size
		if(!tbl.isThisRoot()) {
			int s = supervisor.getLeftSize() + supervisor.getRightSize() + 1;
			Message.sendSize(tbl.getParent(), s);
		}
	}

	void handleSize(Node n, int s) {
		Debug.output("received a SIZE message");
		Debug.output("  id: " + n.getId() + " size: " + s);
		if (supervisor.getNodeTable().isLeftNode(n)) {
			supervisor.setLeftSize(s);
			leftIsReady = true;
		} else {
			supervisor.setRightSize(s);
			rightIsReady = true;
		}

		if (rightIsReady && leftIsReady) {
			// unbalanced to the left
			if (supervisor.getLeftSize() - supervisor.getRightSize() >= 2) {
				// get b-balance node
				Node b = supervisor.getNodeTable().getLeftNode();
				nextState(new BalancingState(b));
			}
			// unbalanced to the right
			else if (supervisor.getRightSize() - supervisor.getLeftSize() >= 2) {
				// get b-balance node
				Node b = supervisor.getNodeTable().getRightNode();
				nextState(new BalancingState(b));
			} else {
				Debug.output("need to send SIZE to the parent.. FIXME");
				// TODO send a SIZE signal to the parent
			}

			leftIsReady = false;
			rightIsReady = false;
			waiting = 0;
		} else if (leftIsReady || rightIsReady) {
			if (waiting > 2) {
				Debug.output("needed touching operation.. FIXME");
				// TODO
				// send TOUCH message
			}
		}
		// normal
		// add a wait turn
		waiting++;
	}

	void handleJoinBroadcast(Node n) {
		if (supervisor.getNodeTable().isThisRoot()) {
			// ok, this node is root, so generate a JOIN_SEARCH
			// and forward if necessary
			Debug.output("root received a join broadcast message");

			NodeTable tbl = supervisor.getNodeTable();
			Node thisnode = tbl.getThisNode();
			if (n.getId() < thisnode.getId()) {
				if (tbl.hasLeftNode()) {
					// send JOIN_SEARCH to the left child
					Message.sendJoinSearch(tbl.getLeftNode(), n);
				} else {
					// joining node can be attached here, to the left
					Debug.output("node id: " + n.getId()
							+ " attaches here to the left");
					// set joining node as left child
					tbl.setLeftNode(n);
					// signal to joining node that it can attach itself here
					Message.sendJoinSearch(n, thisnode);
				}
			} else if (n.getId() > thisnode.getId()) {
				if (tbl.hasRightNode()) {
					// send JOIN_SEARCH to the right child
					Message.sendJoinSearch(tbl.getRightNode(), n);
				} else {
					// joining node can be attached here, to the right
					Debug.output("node id: " + n.getId()
							+ " attaches here to the right");
					// set joining node as right child
					tbl.setRightNode(n);
					// signal to joining node that it can attach itself here
					Message.sendJoinSearch(n, thisnode);
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

		NodeTable tbl = supervisor.getNodeTable();
		Node thisnode = tbl.getThisNode();
		if (n.getId() < thisnode.getId()) {
			if (tbl.hasLeftNode()) {
				// forward the JOIN_SEARCH to the left child
				Message.sendJoinSearch(tbl.getLeftNode(), n);
			} else {
				// joining node can attach here, to the left
				Debug.output("node id: " + n.getId()
						+ " attaches here to the left");
				// set joining node as left child
				tbl.setLeftNode(n);
				// signal to joining node that it can attach itself here
				Message.sendJoinSearch(n, thisnode);
			}
		} else if (n.getId() > thisnode.getId()) {
			if (tbl.hasRightNode()) {
				// forward the JOIN_SEARCH to the right child
				Message.sendJoinSearch(tbl.getRightNode(), n);
			} else {
				// joining node can attach here, to the right
				Debug.output("node id: " + n.getId()
						+ " attaches here to the right");
				// set joining node as right child
				tbl.setRightNode(n);
				// signal to joining node that it can attach itself here
				Message.sendJoinSearch(n, thisnode);
			}
		} else {
			// n has the same id of thisnode. ERROR
			System.out.println("ERROR: joining node has got the same id"
					+ "as this node: " + n.getId());
			System.exit(1);
		}
	}

	void handlePrint() {
		Debug.output("received a PRINT message");

		NodeTable tbl = supervisor.getNodeTable();
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
