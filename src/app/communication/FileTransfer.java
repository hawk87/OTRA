package app.communication;

import java.net.InetAddress;

import app.Node;
import app.NodeTable;
import app.OTRAFile;
import app.Router;
import app.Utility;

public class FileTransfer {

	public static void send(InetAddress address, OTRAFile file) {
		byte[] ID = Utility.intToByte(file.getID());
		TCPConnection.send(address, Utility.appendArray(ID, file.getData()));
	}

	public static void receive(InetAddress address, byte[] stream) {
		Node node = NodeTable.getInstance().getNodeFromAddress(address);

		if (node == null) {
			System.err.println("Receive from unknow host");
			System.exit(1);
		}

		byte[] ID = new byte[4];
		byte[] data = new byte[stream.length - 4];
		System.arraycopy(stream, 0, ID, 0, 4);
		System.arraycopy(stream, 4, data, 0, data.length);

		OTRAFile file = new OTRAFile(Utility.byteToInt(ID), data);

		Router.getInstance().route(file, node);
	}
}