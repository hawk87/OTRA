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
		byte[] head = Utility.intToByte(tbl.getThisNode().getId());
		
		if(tbl.isLeftNode(from) && rightTree != null) {
			leftTree = f;
			byte[] ndata;
			ndata = Utility.appendArray(head, rightTree.getData());
			ndata = Utility.appendArray(ndata, leftTree.getData());
			
			if(tbl.isThisRoot()) {
				SerialTree st = new SerialTree(
						Utility.byteArrayToIntArray(ndata));
				st.read();
			} else {
				// send the composed file to parent
				OTRAFile nfile = new OTRAFile(f.getID(), ndata);
				FileTransfer.send(tbl.getParent().getAddress(), nfile);
			}
			//set to default condition
			leftTree = null;
			rightTree = null;
		} else if (tbl.isRightNode(from) && leftTree != null) {
			rightTree = f;
			byte[] ndata;
			ndata = Utility.appendArray(head, rightTree.getData());
			ndata = Utility.appendArray(ndata, leftTree.getData());
			
			if(tbl.isThisRoot()) {
				SerialTree st = new SerialTree(
						Utility.byteArrayToIntArray(ndata));
				st.read();
			} else {
				// send the composed file to parent
				OTRAFile nfile = new OTRAFile(f.getID(), ndata);
				FileTransfer.send(tbl.getParent().getAddress(), nfile);
			}
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
}
