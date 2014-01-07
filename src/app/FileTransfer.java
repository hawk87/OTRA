package app;

import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;

public class FileTransfer {

	public static void send(InetAddress address, OTRAFile file) {
		byte[] ID = IntegerUtility.intToByte(file.getID());
		TCPConnection.send(address, appendArray(ID, file.getData()));
	}

	public static void receive(InetAddress address, byte[] stream) {
		byte[] ID = new byte[4];
		byte[] data = new byte[stream.length - 4];

		System.arraycopy(stream, 0, ID, 0, 4);
		System.arraycopy(stream, 4, data, 0, data.length);

		OTRAFile file = new OTRAFile(IntegerUtility.byteToInt(ID), data);

		Node node = NodeTable.getInstance().getNodeFromAddress(address);

		if (node == null) {
			System.err.println("Receive from unknow host");
			System.exit(1);
		}

		// TODO chiamata router
	}

	/**
	 * Utility to append an array to another.
	 * 
	 * @param first
	 * @param second
	 * @return a (first.length + second.length) long array containing the two
	 *         concatenated.
	 */
	private static byte[] appendArray(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];

		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}
}