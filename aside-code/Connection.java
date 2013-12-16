import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

import app.CRC8;

public class Connection {

	class UDPSend extends Thread {
		private static final int ACK_PORT = 9999;
		private static final int RETRY_LIMIT = 3;
		private static final int DELAY_TOLLERANCE = 1000;

		private InetAddress address;
		private int port;
		private byte[] message;

		private int retry = 0;
		private byte[] ack = new byte[1];

		UDPSend(InetAddress address, int port, byte[] message) {
			this.address = address;
			this.port = port;
			this.message = new byte[message.length + 1];
			System.arraycopy(message, 0, this.message, 0, message.length);
			this.message[this.message.length] = CRC8.calculate(message);
		}

		public void run() {
			try {
				DatagramPacket packet = new DatagramPacket(message,
						message.length, address, port);
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
				DatagramSocket ackSocket = new DatagramSocket(ACK_PORT);

				for (;;) {
					// acknowledgment delay tolerance (in milliseconds)
					ackSocket.setSoTimeout(DELAY_TOLLERANCE);
					socket.send(packet);
					socket.close();

					try {
						ackSocket.receive(ackPacket);
						// da aggiungere verifica ACK/NACK
						ackSocket.close();
						break;
					} catch (InterruptedIOException e) {
						if (++retry >= RETRY_LIMIT) {
							ackSocket.close();
							System.err.println("Max retry limit reached.");
							System.exit(-1);// da modificare in futuro
						}

					}

				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		}
	}

	private static DatagramSocket socket;
	private static int port = 7777;

	public static void send(InetAddress address) throws IOException {

		socket = new DatagramSocket();

		byte[] message = new byte[2];
		message[0] = 0x01;// flag
		message[1] = CRC8.calculate(message, 1);
		DatagramPacket packet = new DatagramPacket(message, message.length,
				address, port);

		socket.send(packet);

		socket.setSoTimeout(1000);
		socket.receive(packet);

	}

	public static void size(int id, int size) {
	}
}
