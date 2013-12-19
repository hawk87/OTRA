import java.io.IOException;
import java.net.*;

import app.CRC8;
import app.Cache;

public class Connection {

	private static final int RETRY_LIMIT = 5;
	private static final int DELAY_TOLLERANCE = 100;
	private static final int PORT = 6666;
	private static final int ACK_PORT = 7777;

	private static final byte[] ACK = { (byte) 0x00 };
	private static final byte[] NACK = { (byte) 0xFF };

	Listener l;

	private byte sn = 00000000;

	public Connection() {
		l = new Listener();
		l.start();
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

					byte[] data = new byte[receivePacket.getLength() - 2];
					System.arraycopy(message, 0, data, 0, data.length);

					byte crc = message[receivePacket.getLength() - 1];
					byte sn = message[receivePacket.getLength() - 2];

					System.out.println("receive " + (int) sn+" crc: "+crc);

					InetAddress address = receivePacket.getAddress();

					// PORCATA
					if (Cache.isThere(address, sn)) {
						System.out.println("DUPLICATO");
					}
					
					Cache.print();

					if (CRC8.calculate(message, receivePacket.getLength()-1) == crc) {
						DatagramPacket ack = new DatagramPacket(ACK,
								ACK.length, address, ACK_PORT);
						receiveSocket.send(ack);
					} else {
						DatagramPacket nack = new DatagramPacket(NACK,
								NACK.length, address, ACK_PORT);
						receiveSocket.send(nack);
					}
					for (int i = 0; i < data.length; i++) {
						System.out.println("-> " + data[i]);
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
		byte[] message = new byte[data.length + 2];
		System.arraycopy(data, 0, message, 0, data.length);
		message[data.length] = sn; // add SN
		message[data.length+1] = CRC8.calculate(message, message.length - 1); // add
																			// CRC
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
				System.out.println("send " + (int) sn);
				sendSocket.close();

				try {
					ackSocket.receive(ackPacket);
					

					// MIGLIORARE VERIFICA
					if (ackPacket.getData()[0] == ACK[0]) {
						System.out.println("<- ACK " + (int) sn);
						sn++;
						ackSocket.close();
						break;
					} else {
						System.out.println("<- NACK " + (int) sn);
						//retry++;
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

	public void sendBroadcast(byte[] data){
		try {
			byte[] address = InetAddress.getLocalHost().getAddress();
			address[3]=(byte)0xFF;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}