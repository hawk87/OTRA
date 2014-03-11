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
	
	private Node latestJoinNode;
	private long latestJoinTime;

	NormalState() {
		Debug.output("Entering normal state...");
	}
	
	/* 
	 * service() in this operational state touches all connected nodes
	 */
	void service() {
		NodeTable tbl = NodeTable.getInstance();
		boolean lostParent = false;
		//check left node
		synchronized (this) {
			if(tbl.hasLeftNode()) {
				if(!MessageSystem.sendTouch(tbl.getLeftNode())) {
					Debug.output("left node failing TOUCH: " + tbl.getLeftNode());
					Debug.output("removing from NodeTable");
					tbl.setLeftNode(null);
				}
			}
		}
		// right node
		synchronized (this) {
			if(tbl.hasRightNode()){
				if(!MessageSystem.sendTouch(tbl.getRightNode())) {
					Debug.output("right node failing TOUCH: " + tbl.getRightNode());
					Debug.output("removing from NodeTable");
					tbl.setRightNode(null);
				}
			}
		}
		//check parent
		if(!tbl.isThisRoot()) {
			if(!MessageSystem.sendTouch(tbl.getParent())) {
				Debug.output("parent node failing TOUCH: " + tbl.getParent());
				lostParent = true;
				synchronized (this) {
					if(supervisor.isInRecoveryState())
						//already in recovery
						return;
					nextState(new RecoveryState());
				}
			}
		}
		//check if we are a leaf, then send height up
		if(!tbl.hasLeftNode() && !tbl.hasRightNode()) {
			if(!tbl.isThisRoot() && !lostParent)
				MessageSystem.sendHeight(tbl.getParent(), 1);
		}
	}

	void handleHeight(Node n, int h) {
		//Debug.output("received a HEIGHT message");
		//Debug.output("  id: " + n.getId() + " height: " + h);
		
		NodeTable tbl = NodeTable.getInstance();
		
		if (tbl.isLeftNode(n)) {
			supervisor.leftHeight = h;
			leftIsReady = true;
		} else {
			supervisor.rightHeight = h;
			rightIsReady = true;
		}
		
		if(!tbl.hasLeftNode()) {
			//we reset because this data can be out of date
			supervisor.leftHeight = 0;
			leftIsReady = true;
		}
		if(!tbl.hasRightNode()) {
			//we reset because this data can be out of date
			supervisor.rightHeight = 0;
			rightIsReady = true;
		}

		if (rightIsReady && leftIsReady) {
			// unbalanced to the left
			if (supervisor.leftHeight - supervisor.rightHeight >= 2) {
				// get b-balance node
				Node b = tbl.getLeftNode();
				nextState(new BalancingAState(b));
			}
			// unbalanced to the right
			else if (supervisor.rightHeight - supervisor.leftHeight >= 2) {
				// get b-balance node
				Node b = tbl.getRightNode();
				nextState(new BalancingAState(b));
			} else {
				//send a HEIGHT signal to the parent if any
				if(!tbl.isThisRoot()) {
					MessageSystem.sendHeight(
							tbl.getParent(), supervisor.computeHeight());
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
			
			if(latestJoinNode != null && n.equals(latestJoinNode))
				if(System.currentTimeMillis() - latestJoinTime < 2000)
					//then we have already handled this joining node
					return;
			
			MessageSystem.sendJoinResponse(n);
			latestJoinNode = n;
			latestJoinTime = System.currentTimeMillis();

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
	
	void handleBalance(Node from, Node anode) {
		if(!from.equals(anode)) {
			// we are the C-balancing node
			nextState(new BalancingCState(anode, from));
		} else {
			// we are the B-balancing node
			nextState(new BalancingBState(from));
		}
	}
	
	void handleSetParent(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setParent(x);
	}
	
	void handleSetLeft(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setLeftNode(x);
	}
	
	void handleSetRight(Node from, Node x) {
		NodeTable tbl = NodeTable.getInstance();
		tbl.setRightNode(x);
	}

	void handlePrint() {
		Debug.output("received a PRINT message");

		NodeTable tbl = NodeTable.getInstance();
		Node thisnode = tbl.getThisNode();
		
		if(tbl.isThisRoot())
			return;
		if (!tbl.hasLeftNode() && !tbl.hasRightNode()) {
			Debug.output("accepted");
			byte[] data = {};
			data = Utility.appendArray(data,
					Utility.intToByte(thisnode.getId()));
			data = Utility.appendArray(data, Utility.intToByte(-1));
			data = Utility.appendArray(data, Utility.intToByte(-1));

			OTRAFile file = new OTRAFile(-1, "-1_print", data);
			FileTransfer.send(tbl.getParent().getAddress(), file);
		}
	}
	
	void handleDisconnected(Node disc, Node from) {
		NodeTable tbl = NodeTable.getInstance();

		if(tbl.getParent() != null && tbl.getParent().equals(disc)) {
			synchronized (this) {
				if(supervisor.isInRecoveryState()) {
					//THIS IS VERY HARD TO EXPLAIN..:)
					supervisor.handleDisconnected(disc, from);
					return;
				}
				MessageSystem.sendDscnnResponse(from.getAddress(), tbl.getThisNode());
				nextState(new RecoveryState(from));
			}
		} else if(tbl.hasLeftNode() && tbl.getLeftNode().equals(disc)) {
			synchronized (this) {
				tbl.setLeftNode(null);
			}
		}
		else if(tbl.hasRightNode() && tbl.getRightNode().equals(disc)) {
			synchronized (this) {
				tbl.setRightNode(null);
			}
		}
	}
	
	void handleRecoveryFindMax(Node left, Node right) {
		NodeTable tbl = NodeTable.getInstance();
		
		if(tbl.hasRightNode()) {
			//then we forward the message to the right subtree
			MessageSystem.sendRecoveryFindMax(tbl.getRightNode(), left, right);
			return;
		}
		
		// ELSE we have to perform the recovery operation
		
		MessageSystem.sendSetRight(tbl.getParent(), tbl.getLeftNode());
		if(tbl.getLeftNode() != null) {
			MessageSystem.sendSetParent(tbl.getLeftNode(), tbl.getParent());
		}
		
		MessageSystem.sendSetParent(left, tbl.getThisNode());
		tbl.setLeftNode(left);
		MessageSystem.sendSetParent(right, tbl.getThisNode());
		tbl.setRightNode(right);
		
		//at this point we are the root of a orphan tree
		//we try to re-join the network if present
		nextState(new JoiningState());
	}
}
