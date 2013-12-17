import java.io.IOException;
import java.net.*;

import app.CRC8;

public class Connection {

	private static final int RETRY_LIMIT = 3;
	private static final int DELAY_TOLLERANCE = 1000;
	private static final int PORT = 6666;
	private static final int ACK_PORT = 7777;

	private static final byte[] ACK = { (byte) 0x00 };
	private static final byte[] NACK = { (byte) 0xFF };
	
	Listener l;

	public Connection() {
		l = new Listener();
		l.start();
	}
	
	public void stop() {
		l=null;
	}
	
	private class Listener extends Thread {

		private DatagramSocket receiveSocket;

		public Listener() {
			try {
				receiveSocket = new DatagramSocket(PORT);
			} catch (Exception e) {
				System.err.print(e.getMessage());
			}
		}

		public void run() {
			while (true) {
				byte[] buf = new byte[16];
				DatagramPacket receivePacket = new DatagramPacket(buf,
						buf.length);
				try {
					receiveSocket.receive(receivePacket);
					byte[] message = receivePacket.getData();

					System.out.println("receive");

					byte[] data = new byte[receivePacket.getLength() - 1];
					System.arraycopy(message, 0, data, 0, data.length);

					if (CRC8.calculate(data) == message[receivePacket
							.getLength() - 1]) {
						DatagramPacket ack = new DatagramPacket(ACK,
								ACK.length, receivePacket.getAddress(),
								ACK_PORT);
						receiveSocket.send(ack);
					} else {
						DatagramPacket nack = new DatagramPacket(NACK,
								NACK.length, receivePacket.getAddress(),
								ACK_PORT);
						receiveSocket.send(nack);
					}
					for (int i = 0; i < data.length; i++) {
						System.out.print(data[i] + ";");
					}

				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public void send(InetAddress address, byte[] data) {

		int retry = 0;

		// message packet
		byte[] message = new byte[data.length + 1];
		System.arraycopy(data, 0, message, 0, data.length);
		message[data.length] = CRC8.calculate(data);
		DatagramPacket sendPacket = new DatagramPacket(message, message.length,
				address, PORT);

		// ack packet
		byte[] buf = new byte[8];
		DatagramPacket ackPacket = new DatagramPacket(buf, buf.length);

		try {
			DatagramSocket ackSocket = new DatagramSocket(ACK_PORT);

			for (;;) {
				// acknowledgment delay tolerance (in milliseconds)
				ackSocket.setSoTimeout(DELAY_TOLLERANCE);

				DatagramSocket sendSocket = new DatagramSocket();
				sendSocket.send(sendPacket);
				System.out.println("send");
				sendSocket.close();

				try {
					ackSocket.receive(ackPacket);
					ackSocket.close();

					// MIGLIORARE VERIFICA
					if (ackPacket.getData()[0] == ACK[0]) {
						System.out.print("ACK");
						break;
					} else {
						System.out.print("NACK");
						retry++;
					}

				} catch (SocketTimeoutException e) {
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