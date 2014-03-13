package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

	public void route(OTRAFile f, Node from) throws IOException {
		if (f.getID() > 0) {
			// normal routing
			Debug.output("Normal routing");
			routeNormal(f, from);
		} else if (f.getID() == -1) {
			// this is a printing file
			routePrintingFile(f, from);
		}
	}

	public void forward(OTRAFile dest) {
		NodeTable tbl = NodeTable.getInstance();
		// se non sono root, sono un nodo interno o una foglia
		if (!tbl.isThisRoot()) {
			// se sono foglia
			if (tbl.getLeftNode() == null && tbl.getRightNode() == null) {
				FileTransfer.send(tbl.getParent().getAddress(), dest);
			} else {
				// se sono figlio sx
				if (tbl.getThisNode().getId() < tbl.getParent().getId()) {
					if (dest.getID() >= tbl.getParent().getId())
						FileTransfer.send(tbl.getParent().getAddress(), dest);
					else if (dest.getID() < tbl.getParent().getId()
							&& dest.getID() > tbl.getThisNode().getId()
							&& tbl.hasRightNode())
						FileTransfer
								.send(tbl.getRightNode().getAddress(), dest);
					else if (tbl.hasLeftNode())
						FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
					else {
						Debug.output("ID " + dest.getID()
								+ " doesn't exist. File dropped");
						dest = null;
					}
				}
				// se sono figlio dx
				else if (tbl.getThisNode().getId() > tbl.getParent().getId()) {
					if (dest.getID() <= tbl.getParent().getId())
						FileTransfer.send(tbl.getParent().getAddress(), dest);
					else if (dest.getID() > tbl.getParent().getId()
							&& dest.getID() < tbl.getThisNode().getId()
							&& tbl.hasLeftNode())
						FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
					else if (tbl.hasRightNode())
						FileTransfer
								.send(tbl.getRightNode().getAddress(), dest);
					else {
						Debug.output("ID " + dest.getID()
								+ " doesn't exist. File dropped");
						dest = null;
					}
				}
			}
		}
		// se sono root
		else {
			if (dest.getID() > tbl.getThisNode().getId())
				FileTransfer.send(tbl.getRightNode().getAddress(), dest);
			else
				FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
		}
	}

	private void routeNormal(OTRAFile dest, Node from) throws IOException {
		NodeTable tbl = NodeTable.getInstance();
		// if(my_ID == dest_ID)
		if (dest.getID() == tbl.getThisNode().getId()) {
			// *service*
			Debug.output("File is mine");
			File file = new File(dest.getName());

			// if file doesn't exists, then create it
			if (!file.exists()) {
				Debug.output("Creating new file");
				file.createNewFile();
			} else
				Debug.output("File already exists, creating a new one");

			FileOutputStream f = new FileOutputStream(file);
			f.write(dest.getData());
			Debug.output("File wrote");
			f.close();
			Debug.output("File closed");
		}
		// if(from == lchild_ID)
		else if (tbl.hasLeftNode() && from.getId() == tbl.getLeftNode().getId()) {
			if (tbl.isThisRoot()
					|| (dest.getID() < tbl.getParent().getId() && tbl
							.hasRightNode())) {
				// forward to rchild
				Debug.output("Forwarding to Right Child");
				FileTransfer.send(tbl.getRightNode().getAddress(), dest);
			} else if (dest.getID() > tbl.getParent().getId()) { // forward to
																	// parent
				Debug.output("Forwarding to Parent");
				FileTransfer.send(tbl.getParent().getAddress(), dest);
			} else {
				Debug.output("ID " + dest.getID()
						+ " doesn't exist. File dropped");
				dest = null;
			}
		}
		// if(from == rchild_ID)
		else if (tbl.hasRightNode()
				&& from.getId() == tbl.getRightNode().getId()) {
			if (tbl.isThisRoot()
					|| (dest.getID() < tbl.getParent().getId() && tbl
							.hasLeftNode())) {
				// forward to lchild
				Debug.output("Forwarding to Left Child");
				FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
			} else if (dest.getID() > tbl.getParent().getId()) { // forward to
																	// parent
				Debug.output("Forwarding to Parent");
				FileTransfer.send(tbl.getParent().getAddress(), dest);
			} else {
				Debug.output("ID " + dest.getID()
						+ " doesn't exist. File dropped");
				dest = null;
			}
		}
		// if(from == parent_ID)
		else if (!tbl.isThisRoot() && from.getId() == tbl.getParent().getId()) {
			if (dest.getID() < tbl.getThisNode().getId() && tbl.hasLeftNode()) {
				// forward to lchild
				Debug.output("Forwarding to Left Child");
				FileTransfer.send(tbl.getLeftNode().getAddress(), dest);
			} else if (tbl.hasRightNode()) { // forward to rchild
				Debug.output("Forwarding to Right Child");
				FileTransfer.send(tbl.getRightNode().getAddress(), dest);
			} else {
				Debug.output("ID " + dest.getID()
						+ " doesn't exist. File dropped");
				dest = null;
			}
		}
		// if nothing else
		else {
			System.out.println("ERROR: error in routing system");
			System.exit(1);
		}
	}

	private void routePrintingFile(OTRAFile f, Node from) {
		NodeTable tbl = NodeTable.getInstance();

		if (tbl.isLeftNode(from))
			leftTree = f;
		else
			rightTree = f;

		// Exit conditions. We have to wait for information from
		// both subtrees to generate a routing printing message.
		if (leftTree == null && tbl.hasLeftNode())
			return;
		if (rightTree == null && tbl.hasRightNode())
			return;

		if (leftTree == null) {
			leftTree = new OTRAFile(-1, "-1_print", Utility.intToByte(-1));
		} else if (rightTree == null) {
			rightTree = new OTRAFile(-1, "-1_print", Utility.intToByte(-1));
		}

		byte[] head = Utility.intToByte(tbl.getThisNode().getId());
		byte[] ndata;

		ndata = Utility.appendArray(head, rightTree.getData());
		ndata = Utility.appendArray(ndata, leftTree.getData());

		if (tbl.isThisRoot()) {
			// we are root, then we have to print collected information
			SerialTree st = new SerialTree(Utility.byteArrayToIntArray(ndata));
			// parse and print the tree
			Command.println(st.read());
		} else {
			// send the composed file to parent
			OTRAFile nfile = new OTRAFile(-1, "-1_print", ndata);
			FileTransfer.send(tbl.getParent().getAddress(), nfile);
		}

		// reset to default condition
		leftTree = null;
		rightTree = null;
	}
}
