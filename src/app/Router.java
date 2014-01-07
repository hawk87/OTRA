package app;

public class Router {
	
	public void route(OTRAFile dest, Node from) {
		
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

}
