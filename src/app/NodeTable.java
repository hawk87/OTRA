package app;

public class NodeTable {
	
	private Node parent;
	private Node leftChild;
	private Node rightChild;
	
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

}
