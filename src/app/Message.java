package app;

import java.io.IOException;

public class Message {
	
	public static void touch(Node n) throws IOException {
		
		byte[] data = new byte[1];
		data[0] = 0x01; // flag number
		Connection.send(n.getAddress(), data);
		
	}
	
	public static void size(Node n, int s) throws IOException {
		
		byte[] data = new byte[5];
		data[0] = 0x02; // flag number
		
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
}
