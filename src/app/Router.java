package app;

import app.communication.FileTransfer;

public class Router {

	private static Router INSTANCE = new Router();

	private OTRAFile leftTree;
	private OTRAFile rightTree;

	private Router() {

	}
	
	public static Router getInstance() {
		return INSTANCE;
	}
	
	public void route(OTRAFile f, Node from) {
		if(f.getID() > 0) {
			//normal routing
			routeNormal(f, from);
		} else if(f.getID() == -1) {
			//this is a printing file
			routePrintingFile(f, from);
		}
	}
	
	private void routeNormal(OTRAFile dest, Node from) {
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
		
		if(tbl.isLeftNode(from))
			leftTree = f;
		else
			rightTree = f;
		
		//Exit conditions. We have to wait for information from
		//both subtrees to generate a routing printing message.
		if(leftTree == null && tbl.hasLeftNode())
			return;
		if(rightTree == null && tbl.hasRightNode())
			return;
		
		
		if(leftTree == null) {
			leftTree = new OTRAFile(-1, "", Utility.intToByte(-1));
		} else if(rightTree == null) {
			rightTree = new OTRAFile(-1, "", Utility.intToByte(-1));
		}
		
		byte[] head = Utility.intToByte(tbl.getThisNode().getId());
		byte[] ndata;
		
		ndata = Utility.appendArray(head, rightTree.getData());
		ndata = Utility.appendArray(ndata, leftTree.getData());
		
		if(tbl.isThisRoot()) {
			//we are root, then we have to print collected information
			SerialTree st = new SerialTree(
					Utility.byteArrayToIntArray(ndata));
			//parse and print the tree
			Command.println(st.read());
		} else {
			// send the composed file to parent
			OTRAFile nfile = new OTRAFile(-1, "", ndata);
			FileTransfer.send(tbl.getParent().getAddress(), nfile);
		}
		
		//reset to default condition
		leftTree = null;
		rightTree = null;
	}
}
