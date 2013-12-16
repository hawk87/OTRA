import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    private int countThread = 0;
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;

    public ServerThread() throws IOException {
	     this("ServerThread");
    }

    public ServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(7777);
        countThread++;
        System.out.println("Thread #"+countThread+ " started.");
    }

    public void run() {

        while (moreQuotes) {
            try {
                byte[] buf = new byte[1];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                byte[] msg = packet.getData();

                System.out.println("-: "+msg[0]);
                
		          // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);

                try {
                  Thread.sleep(50);
                }
                catch(InterruptedException e) {
                  e.printStackTrace();
                  System.exit(1);
                }
                socket.send(packet);

                System.out.println("*: "+msg[0]);
            } catch (IOException e) {
                e.printStackTrace();
		          moreQuotes = false;
              }
        }
        socket.close();
    }
}
