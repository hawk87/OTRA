package app;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Connection {
	
	private static DatagramSocket socket;
	private static final int port = 7777;
	
	public static void send(InetAddress address, byte[] data) {

		socket = new DatagramSocket();

		byte[] message = new byte[data.length+1];
		message[0] = CRC8.calculate(data);
		
		for(int i = 1; i < message.length; i++) {
			message[i] = data [i-1];
		}
		
		DatagramPacket packet = new DatagramPacket(message, message.length, address, port);

		socket.send(packet);

		socket.setSoTimeout(1000);
		socket.receive(packet);

	}

}
