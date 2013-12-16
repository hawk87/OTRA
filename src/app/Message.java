package app;

import java.io.IOException;

public class Message {
	
	public static void touch(Node n) throws IOException {
		
		byte[] data = new byte[1];
		data[0] = MessageType.TOUCH.getFlag();
		Connection.send(n.getAddress(), data);
		
	}
	
	public static void size(Node n, int s) throws IOException {
		
		byte[] data = new byte[5];
		data[0] = MessageType.SIZE.getFlag();
		
		// tradurre int -> byte
		
		for(int i = 1; i < data.length; i++) {
			data[i] = (byte) s;
			s = s >> 8;
		}
		
		//int k = 0;
		//int temp = 0;
		
		//for(int j = 1; j < data.length; j++) {
		//	temp = (int) data[j];
		//	k = k + (temp << (8*j));
		//}
		
		Connection.send(n.getAddress(), data);
	}
	
	public static void translate(byte[] data) {
		MessageType flag = MessageType.convert(data[0]);
		switch(flag) {
		case TOUCH:
			//TODO
			break;
		case SIZE:
			//TODO
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
