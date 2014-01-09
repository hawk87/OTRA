package app.communication;
import java.io.IOException;
import java.net.*;

import app.communication.CRC8;

public class Connection {

	private static final int RETRY_LIMIT = 10;
	private static final int DELAY_TOLLERANCE = 25;
	private static final int PORT = 6666;
	private static final int ACK_PORT = 7777;

	private static final byte[] ACK = { (byte) 0x00 };
	private static final byte[] NACK = { (byte) 0xFF };

	private static byte sn = 00000000;
	
	private static InterfaceAddress interfaceAddress = null;

	private static class Listener extends Thread {

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

					byte[] data = new byte[receivePacket.getLength() - 3];
					System.arraycopy(message, 2, data, 0, data.length);

					byte flag = message[0];
					byte sn = message[1];
					byte crc = message[receivePacket.getLength() - 1];

					//System.out.println("receive " + (int) sn + " crc: " + crc);

					InetAddress address = receivePacket.getAddress();

					if (CRC8.calculate(message, receivePacket.getLength() - 1) == crc) {
						if (flag == (byte) 0x00) {
							DatagramPacket ack = new DatagramPacket(ACK,
									ACK.length, address, ACK_PORT);
							receiveSocket.send(ack);
						}
						if (!Cache.isThere(address, sn)) {
							Message.translate(receivePacket.getAddress(), data);
						}
					} else {
						if (flag == (byte) 0x00) {
							DatagramPacket nack = new DatagramPacket(NACK,
									NACK.length, address, ACK_PORT);
							receiveSocket.send(nack);
						}
					}
					/*for (int i = 0; i < data.length; i++) {
						System.out.println("-> " + data[i]);
					}*/

				} catch (IOException e) {
					e.printStackTrace();
					//System.err.println(e.getMessage());
					//System.err.println(e.getStackTrace());
				}
			}
		}
	}

	public static void start(InterfaceAddress intAdr){
		Listener l  = new Listener();
		l.setName("Connection thread");
		l.start();
		interfaceAddress = intAdr;
	}

	public static boolean send(InetAddress address, byte[] data) {

		int retry = 0;

		// message packet
		byte[] message = new byte[data.length + 3];
		System.arraycopy(data, 0, message, 2, data.length);
		message[0] = (byte) 0x00;
		message[1] = sn; // add SN
		message[message.length - 1] = CRC8.calculate(message,
				message.length - 1); // add CRC
		DatagramPacket sendPacket = new DatagramPacket(message, message.length,
				address, PORT);

		// ack packet
		byte[] buf = new byte[8];
		DatagramPacket ackPacket = new DatagramPacket(buf, buf.length);

		try {
			DatagramSocket ackSocket = new DatagramSocket(ACK_PORT);

			while(true) {
				// acknowledgment delay tolerance (in milliseconds)
				ackSocket.setSoTimeout(DELAY_TOLLERANCE*(int)Math.pow(1.5, retry));

				DatagramSocket sendSocket = new DatagramSocket();
				sendSocket.send(sendPacket);
				//System.out.println("send " + (int) sn);
				sendSocket.close();

				try {
					ackSocket.receive(ackPacket);

					// MIGLIORARE VERIFICA
					if (ackPacket.getData()[0] == ACK[0]) {
						//System.out.println("<- ACK " + (int) sn);
						sn++;
						ackSocket.close();
						return true;
					} else {
						System.out.println("<- NACK " + (int) sn);
						// retry++;
					}

				} catch (SocketTimeoutException e) {
					if (++retry >= RETRY_LIMIT) {
						ackSocket.close();
						System.err.println("Max retry limit reached.");
						//System.exit(-1);
						return false;
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//System.err.println(e.getMessage());
			//System.err.println(e.getStackTrace());
			return false;
		}
	}

	public static void sendBroadcast(byte[] data) {
		try {

			InetAddress address = interfaceAddress.getBroadcast();

			DatagramSocket sendSocket = new DatagramSocket();
			sendSocket.setBroadcast(true);

			byte[] message = new byte[data.length + 3];
			System.arraycopy(data, 0, message, 2, data.length);
			message[0] = (byte) 0x80;
			message[1] = sn; // add SN
			message[message.length - 1] = CRC8.calculate(message,
					message.length - 1); // add CRC
			DatagramPacket sendPacket = new DatagramPacket(message,
					message.length, address, PORT);

			sendSocket.send(sendPacket);
			//System.out.println("send broadcast " + (int) sn);
			sendSocket.close();
			sn++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}