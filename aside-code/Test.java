import java.net.InetAddress;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection c = new Connection();
		
		InetAddress address = InetAddress.getByName(args[0]);
		
		for(int i =0; i<=100; i++ )
		{
			byte[] data = new byte[(int)(1 + Math.random() * 10)];
			
			for(int j = 0; j<data.length;j++)
			{
				//data[j]=(byte)(Math.random() * 100);
				data[j]=(byte)j;
			}
			c.send(address, data);
		}
	}
}
