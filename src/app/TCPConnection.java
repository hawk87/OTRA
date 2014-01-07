package app;
import java.io.*;
import java.net.*;

public class TCPConnection {
	private static final int SERVER_PORT = 5555;

	private static class Server extends Thread {
		ServerSocket serverSocket;

		public Server() {
			try {
				serverSocket = new ServerSocket(SERVER_PORT);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		public void run() {
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					InputStream in = socket.getInputStream();
					DataInputStream dis = new DataInputStream(in);

					int size = dis.readInt();
					byte[] data = new byte[size];

					if (size > 0) {
						dis.readFully(data);
					}

					// CHIAMATA METODO, inoltro data

				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public static void start() {
		Server s = new Server();
		s.start();
	}

	public static void send(InetAddress address, byte[] data) {
		try {
			Socket socket = new Socket(address, SERVER_PORT);

			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeInt(data.length);
			dos.write(data, 0, data.length);
			dos.flush();

			socket.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}