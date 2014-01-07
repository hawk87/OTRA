package app;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import app.tree.TreeMaintenance;

public class Message {
	
	public static void sendTouch(Node n) {
		byte[] data = new byte[1];
		data[0] = MessageType.TOUCH.getFlag();
		Connection.send(n.getAddress(), data);
	}
	
	public static void sendSize(Node n, int s) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SIZE.getFlag();
		
		byte[] data = appendArray(flag, IntegerUtility.intToByte(s));
		//data[] now holds the first byte as flag and next four bytes
		//for the integer
		Connection.send(n.getAddress(), data);
	}
	
	public static void sendJoinBroadcast(int id) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.JOIN_BROADCAST.getFlag();
		
		byte[] data = appendArray(flag, IntegerUtility.intToByte(id));
		//data now holds the first byte as flag and next four bytes
		//for the integer
		Connection.sendBroadcast(data);
	}
	
	public static void sendJoinSearch(Node to, Node joining) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.JOIN_SEARCH.getFlag();
		//append 4 bytes for the id
		byte[] data = appendArray(flag, IntegerUtility.intToByte(joining.getId()));
		//append bytes for the InetAddress
		data = appendArray(data, joining.getAddress().getAddress());

		Connection.send(to.getAddress(), data);
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
			maintainer.handleTouch(n);
			break;
		case SIZE:
			k = IntegerUtility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			
			n = NodeTable.getInstance().getNodeFromAddress(adr);
			if(n == null) {
				System.out.println("ERROR: SIZE message from unknown node");
				System.exit(1);
			}
			maintainer.handleSize(n, k);
			break;
		case JOIN_BROADCAST:
			k = IntegerUtility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			if(k <= 0) {
				System.out.println("ERROR: received an invalid identifier: " + k);
				System.exit(1);
			}
			n = new Node(k, adr);
			maintainer.handleJoinBroadcast(n);
			break;
		case JOIN_SEARCH:
			InetAddress joinAdr = null;
			int id = IntegerUtility.byteToInt(Arrays.copyOfRange(data, 1, 5));
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
		default:
			//if it doesn't match against any of our defined messages
			//throw an exception
			System.out.println("ERROR: Unknown type of message");
			System.exit(1);
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
