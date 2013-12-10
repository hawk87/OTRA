import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class Client {
	
	private InetSocketAddress address;
	private DatagramSocket connection;
	
	public Client(String addr, int port) {
		address = new InetSocketAddress(addr, port);
		
		try {
			 connection = new DatagramSocket(port);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	public DatagramPacket createPacket(int data) {
		byte[] buf = new byte[1];
		buf[0] = (byte) data;
		DatagramPacket p = null;
		
		try {
		p = new DatagramPacket(
				buf, buf.length, address);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		return p;
	}
	
	public void run() {
		DatagramPacket p;
		byte[] d;
		int i = 0;
		while(true) {
			p = createPacket(i);
			i++;
			try {
				connection.send(p);
				d = p.getData();
				System.out.println("sent data: " + d[0]);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			receive();

			// wait a second before transmitting the next packet
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private void receive() {
		DatagramPacket p = createPacket(0);
		try {
			connection.receive(p);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		byte[] d = new byte[1];
		d = p.getData();
		System.out.println("received data: " + d[0]);
	}
	
	public static void main(String args[]) {
		System.out.println("connecting to host: " + args[0]);
		System.out.println("using port: " + args[1]);
		
		Client cl = new Client(args[0], Integer.parseInt(args[1]));
		cl.run();
	}
	
}
