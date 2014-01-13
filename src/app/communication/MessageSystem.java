package app.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import app.Node;
import app.NodeTable;
import app.Utility;
import app.tree.TreeMaintenance;

public class MessageSystem {
	
	public static boolean sendTouch(Node n) {
		byte[] data = new byte[1];
		data[0] = MessageType.TOUCH.getFlag();
		return Connection.send(n.getAddress(), data);
	}
	
	public static void sendSize(Node n, int s) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SIZE.getFlag();
		
		byte[] data = Utility.appendArray(flag, Utility.intToByte(s));
		//data[] now holds the first byte as flag and next four bytes
		//for the integer
		Connection.send(n.getAddress(), data);
	}
	
	public static void sendJoinBroadcast(int id) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.JOIN_BROADCAST.getFlag();
		
		byte[] data = Utility.appendArray(flag, Utility.intToByte(id));
		//data now holds the first byte as flag and next four bytes
		//for the integer
		Connection.sendBroadcast(data);
	}
	
	public static void sendJoinSearch(Node to, Node joining) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.JOIN_SEARCH.getFlag();
		//append 4 bytes for the id
		byte[] data = Utility.appendArray(flag, Utility.intToByte(joining.getId()));
		//append bytes for the InetAddress
		data = Utility.appendArray(data, joining.getAddress().getAddress());

		Connection.send(to.getAddress(), data);
	}
	
	public static void sendPrint() {
		byte[] flag = new byte[1];
		flag[0] = MessageType.PRINT.getFlag();
		Connection.sendBroadcast(flag);
	}
	
	public static void sendBalance(Node to) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.BALANCE.getFlag();
		Connection.send(to.getAddress(), flag);
	}
	
	public static void setParent(Node to, Node parent) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SET_PARENT.getFlag();		
		
		byte[] data = Utility.appendArray(flag, Utility.intToByte(parent.getId()));
		data = Utility.appendArray(data, parent.getAddress().getAddress());
		
		Connection.send(to.getAddress(), data);
	}
	
	public static void setLeftChild(Node to, Node leftChild) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SET_LEFT.getFlag();
		
		byte[] data = Utility.appendArray(flag, Utility.intToByte(leftChild.getId()));
		data = Utility.appendArray(data, leftChild.getAddress().getAddress());
		
		Connection.send(to.getAddress(), flag);
	}
	
	public static void setRightChild(Node to, Node rightChild) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SET_RIGHT.getFlag();
		
		byte[] data = Utility.appendArray(flag, Utility.intToByte(rightChild.getId()));
		data = Utility.appendArray(data, rightChild.getAddress().getAddress());
		
		Connection.send(to.getAddress(), flag);
	}
	
	public static void translate(InetAddress adr, byte[] data) {
		Node n;
		int k;
		TreeMaintenance maintainer = TreeMaintenance.getInstance();
		MessageType flag = MessageType.convert(data[0]);
		switch(flag) {
		case TOUCH:
			NodeTable tbl = NodeTable.getInstance();
			n = tbl.getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: TOUCH message from unknown node");
				System.exit(1);
			}
			break;
		case SIZE:
			k = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			
			n = NodeTable.getInstance().getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: SIZE message from unknown node");
				System.exit(1);
			}
			maintainer.handleSize(n, k);
			break;
		case JOIN_BROADCAST:
			k = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			if(k <= 0) {
				System.out.println("ERROR: received an invalid identifier: " + k);
				System.exit(1);
			}
			n = new Node(k, adr);
			maintainer.handleJoinBroadcast(n);
			break;
		case JOIN_SEARCH:
			InetAddress joinAdr = null;
			int id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			try {
				joinAdr = InetAddress.getByAddress(
						Arrays.copyOfRange(data, 5, 9));
			} catch(UnknownHostException uhe) {
				System.out.println("ERROR: Message: received wrong JOIN_SEARCH message");
				System.out.println("  UnknownHostException");
				System.exit(1);
			}
			
			n = new Node(id, joinAdr);
			maintainer.handleJoinSearch(n);
			break;
		case BALANCE:
			tbl = NodeTable.getInstance();
			n = tbl.getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: BALANCE message from unknown node");
				System.exit(1);
			}
			maintainer.handleBalance();
			break;
		case SET_PARENT:
			tbl = NodeTable.getInstance();
			n = tbl.getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: SET_PARENT message from unknown node");
				System.exit(1);
			}
			
			int idParent = Utility.byteToInt(Arrays.copyOfRange(data, 1, 4));
			InetAddress addrParent;
			try {
				addrParent = InetAddress.getByAddress(Arrays.copyOfRange(data, 5, 8));
				tbl.setParent(new Node(idParent, addrParent));
			} catch (UnknownHostException e) {
				System.out.println("IP Address illegal length");
				e.printStackTrace();
			}	
			break;
		case SET_LEFT:
			tbl = NodeTable.getInstance();
			n = tbl.getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: SET_PARENT message from unknown node");
				System.exit(1);
			}
			
			int idLeft = Utility.byteToInt(Arrays.copyOfRange(data, 1, 4));
			InetAddress addrLeft;
			try {
				addrLeft = InetAddress.getByAddress(Arrays.copyOfRange(data, 5, 8));
				tbl.setLeftNode(new Node(idLeft, addrLeft));
			} catch (UnknownHostException e) {
				System.out.println("IP Address illegal length");
				e.printStackTrace();
			}	
			break;
		case SET_RIGHT:
			tbl = NodeTable.getInstance();
			n = tbl.getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: SET_PARENT message from unknown node");
				System.exit(1);
			}
			
			int idRight = Utility.byteToInt(Arrays.copyOfRange(data, 1, 4));
			InetAddress addrRight;
			try {
				addrRight = InetAddress.getByAddress(Arrays.copyOfRange(data, 5, 8));
				tbl.setRightNode(new Node(idRight, addrRight));
			} catch (UnknownHostException e) {
				System.out.println("IP Address illegal length");
				e.printStackTrace();
			}	
			break;
		case PRINT:
			maintainer.handlePrint();
			break;
		default:
			//if it doesn't match against any of our defined messages
			//throw an exception
			System.out.println("ERROR: Unknown type of message");
			System.exit(1);
		}
	}
}
