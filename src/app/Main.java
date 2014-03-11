package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
//import java.util.Enumeration;

import app.communication.Connection;
import app.communication.MessageSystem;
import app.communication.TCPConnection;
import app.tree.TreeMaintenance;

public class Main {

	public static void main(String args[]) {

		String fileArg = "", idArg = "", interfaceArg = "";

		int id = 0;
		InterfaceAddress ourInterface = null;

		if (args.length == 0) {
			System.out.println("usage:");
			System.out
					.println("[-id <id_number>] set the ID number; the ID must be a positive integer number");
			System.out
					.println("[-interface <interface_name>] set the network interface");
			System.out
					.println("[-f <config_file_path>] set the configuration file path");
			System.exit(1);
		}

		for (int i = 0; i < args.length;) {
			String arg = args[i];
			switch (arg) {
			case "-f":
				if (!idArg.equals("")) {
					System.out.println("ERROR: invalid arguments");
					System.exit(1);
				} else {
					fileArg = args[i + 1];
					i += 2;
				}
				break;
			case "-id":
				if (!fileArg.equals("")) {
					System.out.println("ERROR: invalid arguments");
					System.exit(1);
				} else {
					idArg = args[i + 1];
					i += 2;
				}
				break;
			case "-interface":
				if (!fileArg.equals("")) {
					System.out.println("ERROR: invalid arguments");
					System.exit(1);
				} else {
					interfaceArg = args[i + 1];
					i += 2;
				}
				break;
			default:
				System.out.println("ERROR: invalid arguments");
				System.exit(1);
				break;
			}

			// TODO test argomenti
		}

		if (!fileArg.equals("")) {

			File configFile = new File(fileArg);

			if (configFile.exists()) {
				try {
					BufferedReader input = new BufferedReader(new FileReader(
							configFile));
					String line;
					while ((line = input.readLine()) != null) {
						String[] config = line.split("=");
						switch (config[0]) {
						case "id":
							id = Integer.parseInt(config[1]);
							Debug.output("host starting with id: " + id);
							break;
						case "interface":
							ourInterface = getNetworkInterface(config[1]);
							break;
						default:
							System.out
									.println("ERROR: error in configuration file");
							System.exit(1);
							break;
						}
					}
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (ourInterface == null || id == 0) {
					System.out.println("ERROR: error in configuration file");
					System.exit(1);
				}
			} else {
				System.out.println("ERROR: configuration file not found at: "
						+ configFile.getAbsolutePath());
				System.exit(1);
			}
		} else {
			id = Integer.parseInt(idArg);
			if (id <= 0) {
				System.out.println("the ID must be a positive integer number");
				System.exit(1);
			}
			Debug.output("host starting with id: " + id);
			ourInterface = getNetworkInterface(interfaceArg);
		}

		Node thisnode = new Node(id, ourInterface.getAddress());
		// allocating the node/routing table
		NodeTable.createInstance(thisnode);
		// set up Connection
		Connection.start(ourInterface);
		// set up TCPConnection
		TCPConnection.start();
		// start messaging system
		MessageSystem.getInstance().setName("MessageSystem listener thread");
		MessageSystem.getInstance().start();
		// entering in maintenance state
		TreeMaintenance.getInstance().setName("TreeMaintenance service thread");
		TreeMaintenance.getInstance().start();
		// run repl loop
		Command repl = new Command();
		repl.run();
	}

	private static InterfaceAddress getNetworkInterface(String interfaceName) {
		System.setProperty("java.net.preferIPv4Stack", "true");

		NetworkInterface netinterface = null;
		try {
			netinterface = NetworkInterface.getByName(interfaceName);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if (netinterface == null) {
			System.out.println("ERROR: wrong interface name");
			System.exit(1);
		}
		return netinterface.getInterfaceAddresses().get(0);
	}
}