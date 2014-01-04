import java.io.*;
import java.net.*;

public class TCPConnestion {
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
				int bytesRead = 0;
				try {
					Socket socket = serverSocket.accept();
					InputStream in = socket.getInputStream();
					DataInputStream data = new DataInputStream(in);

					String fileName = data.readUTF();
					OutputStream output = new FileOutputStream(fileName);
					long size = data.readLong();
					byte[] buffer = new byte[1024];

					while (size > 0
							&& (bytesRead = data.read(buffer, 0,
									(int) Math.min(buffer.length, size))) != -1) {
						output.write(buffer, 0, bytesRead);
						size -= bytesRead;
					}

					output.close();
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

	public static void send(InetAddress address, String fileName) {
		try {
			Socket socket = new Socket(address, SERVER_PORT);
			File file = new File(fileName);
			byte[] byteArray = new byte[(int) file.length() + 1];
			FileInputStream fis = new FileInputStream(file);
			// BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(fis);
			dis.readFully(byteArray, 0, byteArray.length);

			OutputStream os = socket.getOutputStream();

			// Sending file name, file size and data to the server
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF(file.getName());
			dos.writeLong(byteArray.length);
			dos.write(byteArray, 0, byteArray.length);
			dos.flush();

			dis.close();
			socket.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}