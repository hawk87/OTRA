import java.net.*;

import app.CRC8;

public class Connection {

	private static final int RETRY_LIMIT = 3;
	private static final int DELAY_TOLLERANCE = 1000;
	private static final int PORT = 7777;

	public void send(InetAddress address, byte[] data) {

		int retry = 0;
		// message packet
		byte[] message = new byte[data.length + 1];
		System.arraycopy(data, 0, message, 0, data.length);
		message[data.length] = CRC8.calculate(data);
		DatagramPacket packet = new DatagramPacket(message, message.length,
				address, PORT);
		// ack packet
		byte[] ack = new byte[1];
		DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);

		try {
			DatagramSocket socket = new DatagramSocket();

			for (;;) {
				socket.send(packet);

				// acknowledgment delay tolerance (in milliseconds)
				socket.setSoTimeout(DELAY_TOLLERANCE);

				try {
					socket.receive(ackPacket);
					// da aggiungere verifica ACK/NACK
					socket.close();
					break;
				} catch (SocketTimeoutException e) {
					if (++retry >= RETRY_LIMIT) {
						socket.close();
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
