package app.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import app.Node;
import app.NodeTable;
import app.Utility;
import app.tree.TreeMaintenance;

public class MessageSystem extends Thread {
	
	private ArrayBlockingQueue<Message> messageQueue;
	
	private static MessageSystem INSTANCE = new MessageSystem();
	
	private MessageSystem() {
		messageQueue = new ArrayBlockingQueue<>(10);
	}
	
	public static MessageSystem getInstance() {
		return INSTANCE;
	}
	
	public void enqueue(InetAddress address, byte[] data){
		boolean r;
		r = messageQueue.offer(new Message(address, data));
		if(!r) {
			System.out.println("ERROR: MessageSystem.enqueue(): no space available");
			System.exit(1);
		}
	}
	
	public void run(){
		while(true) {
			try {
				synchronized (TreeMaintenance.getInstance()) {
					translate(messageQueue.take());
				}
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	
	public static boolean sendTouch(Node n) {
		byte[] data = new byte[1];
		data[0] = MessageType.TOUCH.getFlag();
		return Connection.send(n.getAddress(), data);
	}
	
	public static void sendHeight(Node n, int s) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.HEIGHT.getFlag();
		
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
	
	public static void sendJoinResponse(Node to) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.JOIN_RESPONSE.getFlag();
		
		Connection.send(to.getAddress(), flag);
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
	
	public static void sendBalance(Node to, Node anode) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.BALANCE.getFlag();
		
		byte[] data = Utility.appendArray(flag, Utility.intToByte(anode.getId()));
		data = Utility.appendArray(data, anode.getAddress().getAddress());
		
		Connection.send(to.getAddress(), data);
	}
	
	public static void sendSetParent(Node to, Node parent) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SET_PARENT.getFlag();
		byte[] data;
		
		int thisid = NodeTable.getInstance().getThisNode().getId();
		data = Utility.appendArray(flag, Utility.intToByte(thisid));
		
		if(parent == null) {
			//we send ID=-1 to signal this situation
			data = Utility.appendArray(data, Utility.intToByte(-1));
		} else {
			data = Utility.appendArray(data, Utility.intToByte(parent.getId()));
			data = Utility.appendArray(data, parent.getAddress().getAddress());
		}
		Connection.send(to.getAddress(), data);
	}
	
	public static void sendSetLeft(Node to, Node leftChild) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SET_LEFT.getFlag();
		byte[] data;
		
		int thisid = NodeTable.getInstance().getThisNode().getId();
		data = Utility.appendArray(flag, Utility.intToByte(thisid));
		
		if(leftChild == null) {
			//we send ID=-1 to signal this situation
			data = Utility.appendArray(data, Utility.intToByte(-1));
		} else {
			data = Utility.appendArray(data, Utility.intToByte(leftChild.getId()));
			data = Utility.appendArray(data, leftChild.getAddress().getAddress());
		}
		
		Connection.send(to.getAddress(), data);
	}
	
	public static void sendSetRight(Node to, Node rightChild) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.SET_RIGHT.getFlag();
		byte[] data;
		
		int thisid = NodeTable.getInstance().getThisNode().getId();
		data = Utility.appendArray(flag, Utility.intToByte(thisid));

		if(rightChild == null) {
			// we send ID=-1 to signal this situation
			data = Utility.appendArray(data, Utility.intToByte(-1));
		} else {
			data = Utility.appendArray(data, Utility.intToByte(rightChild.getId()));
			data = Utility.appendArray(data, rightChild.getAddress().getAddress());
		}
		Connection.send(to.getAddress(), data);
	}
	
	public static void sendDisconnected(Node disc) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.DISCONNECTED.getFlag();
		byte[] data;
		
		data = Utility.appendArray(flag, Utility.intToByte(disc.getId()));
		data = Utility.appendArray(data, disc.getAddress().getAddress());
		
		Connection.sendBroadcast(data);
	}
	
	public static void sendDscnnResponse(InetAddress to, Node x) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.DSCNN_RESPONSE.getFlag();
		byte[] data;
		
		data = Utility.appendArray(flag, Utility.intToByte(x.getId()));
		data = Utility.appendArray(data, x.getAddress().getAddress());
		
		Connection.send(to, data);
	}
	
	public static void sendRecoveryFindMax(Node to, Node leftChild, Node rightChild) {
		byte[] flag = new byte[1];
		flag[0] = MessageType.RECOVERY_FIND_MAX.getFlag();
		byte[] data;
		
		data = Utility.appendArray(flag, serializeNodeObj(leftChild));
		data = Utility.appendArray(data, serializeNodeObj(rightChild));
		
		Connection.send(to.getAddress(), data);
	}
	
	/**
	 * Serializing the Node object. Also, null case is handled.
	 * @param Node x
	 * @return 8 bytes vector with the serialization of x
	 */
	private static byte[] serializeNodeObj(Node x) {
		byte[] data;
		if(x == null) {
			data = Utility.intToByte(-1);
			data = Utility.appendArray(data, Utility.intToByte(0));
		} else {
			data = Utility.intToByte(x.getId());
			data = Utility.appendArray(data, x.getAddress().getAddress());
		}
		return data;
	}
	
	/**
	 * Read the serial version of the vector Node
	 * @param vector containing the serial version of the Node
	 * @return the Node obj
	 */
	private static Node readNodeObj(byte[] vector) {
		if(vector.length != 8) {
			System.out.println("Wrong size in reading a Node object");
			System.exit(1);
		}
		Node x;
		int id = Utility.byteToInt(Arrays.copyOfRange(vector, 0, 4));
		if(id == -1) {
			x = null;
		} else {
			InetAddress adr = null;
			try {
				adr = InetAddress.getByAddress(
						Arrays.copyOfRange(vector, 4, 8));
			} catch(UnknownHostException uhe) {
				System.out.println("ERROR: Problems parsing a Node object");
				System.out.println("  UnknownHostException");
				System.exit(1);
			}
			x = new Node(id, adr);
		}
		return x;
	}
	
	private void translate(Message msg) {
		Node from, x;
		int k, id;
		InetAddress adr = msg.getAddress();
		byte[] data = msg.getData();
		TreeMaintenance maintainer = TreeMaintenance.getInstance();
		NodeTable tbl = NodeTable.getInstance();
		from = tbl.getNodeFromAddress(adr);
		
		MessageType flag = MessageType.convert(data[0]);
		switch(flag) {
		case TOUCH:
			break;
		case HEIGHT:
			k = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			maintainer.handlehHeight(from, k);
			break;
		case JOIN_BROADCAST:
			k = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			if(k <= 0) {
				System.out.println("ERROR: received an invalid identifier: " + k);
				System.exit(1);
			}
			from = new Node(k, adr);
			maintainer.handleJoinBroadcast(from);
			break;
		case JOIN_RESPONSE:
			maintainer.handleJoinResponse();
			break;
		case JOIN_SEARCH:
			InetAddress joinAdr = null;
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			try {
				joinAdr = InetAddress.getByAddress(
						Arrays.copyOfRange(data, 5, 9));
			} catch(UnknownHostException uhe) {
				System.out.println("ERROR: Message: received wrong JOIN_SEARCH message");
				System.out.println("  UnknownHostException");
				System.exit(1);
			}
			
			x = new Node(id, joinAdr);
			maintainer.handleJoinSearch(x);
			break;
		case BALANCE:
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			InetAddress anodeAdr = null;
			try {
				anodeAdr = InetAddress.getByAddress(
						Arrays.copyOfRange(data, 5, 9));
			} catch(UnknownHostException uhe) {
				System.out.println("ERROR: Message: received wrong BALANCE message");
				System.out.println("  UnknownHostException");
				System.exit(1);
			}
			Node anode = new Node(id, anodeAdr);
			maintainer.handleBalance(from, anode);
			break;
		case SET_PARENT:
			id = Utility.byteToInt(Arrays.copyOfRange(data, 5, 9));
			if(id == -1) {
				x = null;
			} else {
				InetAddress badr = null;
				try {
					badr = InetAddress.getByAddress(
							Arrays.copyOfRange(data, 9, 13));
				} catch (UnknownHostException e) {
					System.out.println("ERROR: Message: received wrong SET_PARENT message");
					System.out.println("  UnknownHostException");
					System.exit(1);
				}
				x = new Node(id, badr);
			}
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			from = new Node(id, adr);
			maintainer.handleSetParent(from, x);
			break;
		case SET_LEFT:
			id = Utility.byteToInt(Arrays.copyOfRange(data, 5, 9));
			if(id == -1) {
				x = null;
			} else {
				InetAddress badr = null;
				try {
					badr = InetAddress.getByAddress(
							Arrays.copyOfRange(data, 9, 13));
				} catch (UnknownHostException e) {
					System.out.println("ERROR: Message: received wrong SET_PARENT message");
					System.out.println("  UnknownHostException");
					System.exit(1);
				}
				x = new Node(id, badr);
			}
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			from = new Node(id, adr);
			maintainer.handleSetLeft(from, x);
			break;
		case SET_RIGHT:
			id = Utility.byteToInt(Arrays.copyOfRange(data, 5, 9));
			if(id == -1) {
				x = null;
			} else {
				InetAddress badr = null;
				try {
					badr = InetAddress.getByAddress(
							Arrays.copyOfRange(data, 9, 13));
				} catch (UnknownHostException e) {
					System.out.println("ERROR: Message: received wrong SET_PARENT message");
					System.out.println("  UnknownHostException");
					System.exit(1);
				}
				x = new Node(id, badr);
			}
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			from = new Node(id, adr);
			maintainer.handleSetRight(from, x);
			break;
		case PRINT:
			maintainer.handlePrint();
			break;
		case DISCONNECTED:
			InetAddress discAdr = null;
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			try {
				discAdr = InetAddress.getByAddress(
						Arrays.copyOfRange(data, 5, 9));
			} catch(UnknownHostException uhe) {
				System.out.println("  UnknownHostException");
				System.exit(1);
			}
			maintainer.handleDisconnected(new Node(id, discAdr), adr);
			break;
		case DSCNN_RESPONSE:
			id = Utility.byteToInt(Arrays.copyOfRange(data, 1, 5));
			anodeAdr = null;
			try {
				anodeAdr = InetAddress.getByAddress(
						Arrays.copyOfRange(data, 5, 9));
			} catch(UnknownHostException uhe) {
				System.out.println("  UnknownHostException");
				System.exit(1);
			}
			maintainer.handleDscnnResponse(new Node(id, anodeAdr));
			break;
		case RECOVERY_FIND_MAX:
			Node a = readNodeObj(Arrays.copyOfRange(data, 1, 9));
			Node b = readNodeObj(Arrays.copyOfRange(data, 9, 17));
			maintainer.handleRecoveryFindMax(a, b);
			break;
		default:
			//if it doesn't match against any of our defined messages
			//throw an exception
			System.out.println("ERROR: Unknown type of message");
			System.exit(1);
		}
	}
}
