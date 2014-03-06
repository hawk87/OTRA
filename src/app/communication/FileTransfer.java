package app.communication;

import java.io.IOException;
import java.net.InetAddress;

import app.Node;
import app.NodeTable;
import app.OTRAFile;
import app.Router;

public class FileTransfer {

	public static void send(InetAddress address, OTRAFile file) {
		/*
		 * byte[] ID = Utility.intToByte(file.getID());
		 * TCPConnection.send(address, Utility.appendArray(ID, file.getData()));
		 */

		TCPConnection.send(address, file);
	}

	public static void receive(InetAddress address, Object data) {
		Node node = NodeTable.getInstance().getNodeFromAddress(address);

		if (node == null) {
			System.err.println("Receive from unknow host");
			System.exit(1);
		}

		/*
		 * byte[] ID = new byte[4]; byte[] data = new byte[stream.length - 4];
		 * System.arraycopy(stream, 0, ID, 0, 4); System.arraycopy(stream, 4,
		 * data, 0, data.length);
		 * 
		 * String name = byteToString();
		 * 
		 * OTRAFile file = new OTRAFile(Utility.byteToInt(ID),, data);
		 */

		OTRAFile file = (OTRAFile) data;

		try {
			Router.getInstance().route(file, node);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}