import java.net.InetAddress;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection c = new Connection();
		c.start();
		
		InetAddress address = InetAddress.getByName(args[0]);
		byte[] data = {(byte) 0x3, (byte) 0x5};
		
		c.send(address, data);

	}

}
