package app;

import java.net.InetAddress;

/**
 * This is the main table that every node/host maintain.
 * It serves two purposes: tree maintenance and routing algorithm.
 */
public class NodeTable {
	
	private Node thisNode;
	
	private Node parent;
	private Node leftChild;
	private Node rightChild;
	
	public NodeTable(Node n) {
		thisNode = n;
	}
	
	/**
	 * This method matches against its entries to find the proper node which is
	 * mapped to this InetAddress
	 * 
	 * @return null if adr is not associated with any Node 
	 */
	public Node getNodeFromAddress(InetAddress adr) {
		if (parent.getAddress().equals(adr)) 
			return parent;
		else if (leftChild.getAddress().equals(adr))
			return leftChild;
		else if (rightChild.getAddress().equals(adr))
			return rightChild;
		else {
			// this address is not in the NodeTable
			return null;
		}	
	}
	
	public Node getThisNode() {
		return thisNode;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Node getLeftNode() {
		return leftChild;
	}
	
	public Node getRightNode() {
		return rightChild;
	}
	
	public void setParent(Node p) {
		parent = p;
	}
	
	public void setLeftNode(Node l) {
		leftChild = l;
	}
	
	public void setRightNode(Node r) {
		rightChild = r;
	}
	
	public boolean isLeftNode(Node x) {
		return (leftChild == x);
	}
	
	public boolean isRightNode(Node x) {
		return (rightChild == x);
	}
	
	public boolean isThisRoot() {
		return (parent == null);
	}
	
	public boolean hasLeftNode() {
		return (leftChild != null);
	}
	
	public boolean hasRightNode() {
		return (rightChild != null);
	}

}
