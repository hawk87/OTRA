package app;

public class Router {
	
private static Router INSTANCE = new Router();
	
	private OTRAFile leftTree;
	private OTRAFile rightTree;
			
	private Router() {
		
	}
	
	public static Router getInstance() {
		return INSTANCE;
	}
	
	public void route(OTRAFile dest, Node from) {
		if(dest.getID() == -1) {
			//this file is for printing
			routePrintingFile(dest, from);
			//TODO perche' non tanto bella sta cosa
			return;
		}
		
		NodeTable tbl = NodeTable.getInstance();
		// if(my_ID == dest_ID)
		if(dest.getID() == tbl.getThisNode().getId()) {
			// TODO *service*
		}
		
		// if(from == lchild_ID)
		else if(tbl.hasLeftNode())
			if(from.getId() == tbl.getLeftNode().getId()) {
				if(tbl.isThisRoot() || dest.getID() < tbl.getParent().getId())
					// forward to rchild
					FileTransfer.send(tbl.getRightNode().getAddress(), dest);
				else // forward to parent
					FileTransfer.send(tbl.getParent().getAddress(), dest);
			}

		// if(from == rchild_ID)
		else if(tbl.hasRightNode())
			if(from.getId() == tbl.getRightNode().getId()) {
				if(tbl.isThisRoot() || dest.getID() < tbl.getParent().getId())
					// forward to lchild
					FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
				else // forward to parent
					FileTransfer.send(tbl.getParent().getAddress(), dest);
	
		}
		
		// if(from == parent_ID)
		else if(tbl.isLeftNode(tbl.getThisNode()) || tbl.isRightNode(tbl.getThisNode())) {
			if(dest.getID() < tbl.getThisNode().getId())
				// forward to lchild
				FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
			else //forward to rchild
				FileTransfer.send(tbl.getRightNode().getAddress(), dest);
			
		}
			
	}
	
	private void routePrintingFile(OTRAFile f, Node from) {
		NodeTable tbl = NodeTable.getInstance();
		
		if(tbl.isLeftNode(from) && rightTree != null) {
			leftTree = f;
			
			
			//set to default condition
			leftTree = null;
			rightTree = null;
		} else if (tbl.isRightNode(from) && leftTree != null) {
			
			
			//set to default condition
			leftTree = null;
			rightTree = null;
		}
		
		if(tbl.isLeftNode(from)) {
			//then we have to wait for information coming from right subtree
			leftTree = f;
		}
		
		if(tbl.isRightNode(from)) {
			// wait for information coming from left subtree
			rightTree = f;
		}
	}
	
	/**
	 * Utility to append an array to another.
	 * @param first
	 * @param second
	 * @return a (first.length + second.length) long array containing the two
	 * concatenated.
	 */
	private static byte[] appendArray(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}
	
}
