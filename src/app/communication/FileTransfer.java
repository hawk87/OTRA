package app.communication;

import java.io.IOException;
import java.net.InetAddress;

import app.Node;
import app.NodeTable;
import app.OTRAFile;
import app.Router;

public class FileTransfer {

	public static void send(InetAddress address, OTRAFile file) {
		TCPConnection.send(address, file.getName(), file.getData());
	}

	public static void receive(InetAddress address, String fileName, byte[] data) {
		Node node = NodeTable.getInstance().getNodeFromAddress(address);

		if (node == null) {
			System.err.println("Receive from unknown host");
			System.exit(1);
		}

		int ID = Integer.parseInt(fileName.split("_")[0]);

		OTRAFile file = new OTRAFile(ID, fileName, data);

		try {
			Router.getInstance().route(file, node);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}