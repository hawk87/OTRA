package app;

import java.net.InetAddress;

import app.tree.TreeMaintenance;

public class Message {
	
	public static void touch(Node n) {
		
		byte[] data = new byte[1];
		data[0] = MessageType.TOUCH.getFlag();
		Connection.send(n.getAddress(), data);
		
	}
	
	public static void size(Node n, int s) {
		
		byte[] data = new byte[5];
		data[0] = MessageType.SIZE.getFlag();
		
		// int -> byte
		
		for(int i = 1; i < data.length; i++) {
			data[i] = (byte) s;
			s = s >> 8;
		}
		
		Connection.send(n.getAddress(), data);
	}
	
	public static void sendJoinBroadcast(int id) {
		byte[] data = new byte[5];
		data[0] = MessageType.JOIN_BROADCAST.getFlag();
		
		//TODO
		//Connection.broadcast();
	}
	
	public static void joinSearch(Node n) {
		byte[] data = new byte[4];
		data[0] = MessageType.JOIN_SEARCH.getFlag();
		
		//TODO
		Connection.send(n.getAddress(), data);
	}
	
	public static void translate(InetAddress addr, byte[] data) {
		MessageType flag = MessageType.convert(data[0]);
		switch(flag) {
		case TOUCH:
			NodeTable tbl = NodeTable.getInstance();
			Node n = tbl.getNodeFromAddress(addr);
			if(n == null) {
				System.out.println("ERROR: touch message from unknown node");
				System.exit(1);
			}
			TreeMaintenance.getInstance().touchFromParent(n);
			break;
		case SIZE:
			int k = 0;
			int temp = 0;
			
			if (data.length != 5) {
				System.out.println("Parsing Error. Expected data size: 5.");
				System.exit(1);
			}
			
			// byte -> int
			for(int j = 1; j < data.length; j++) {
				temp = (int) data[j];
				k = k + (temp << (8*j));
			}
			
			//Node n = new Node(id, addr);
			break;
		case JOIN_BROADCAST:
			//TODO
			break;
		case JOIN_SEARCH:
			break;
		default:
			//if it doesn't match against any of our defined messages
			//throw an exception
		}
	}
}
