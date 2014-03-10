package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import app.communication.Connection;
import app.communication.MessageSystem;
import app.communication.TCPConnection;
import app.tree.TreeMaintenance;

public class Main {

	public static void main(String args[]) {

		String fileArg = "", idArg = "";

		int id = 0;
		InterfaceAddress ourInterface = null;

		if (args.length == 0) {
			//TODO help
			System.out.println("Missing arguments");
			System.exit(1);
		}

		for (int i = 0; i < args.length;) {
			String arg = args[i];
			switch (arg) {
			case "-f":
				if (!idArg.equals("")) {
					System.out.println("Invalid arguments");
					System.exit(1);
				} else {
					fileArg = args[i + 1];
					i += 2;
				}
				break;
			case "-id":
				if (!fileArg.equals("")) {
					System.out.println("Invalid arguments");
					System.exit(1);
				} else {
					idArg = args[i + 1];
					i += 2;
				}
				break;
			default:
				System.out.println("Invalid arguments default");
				System.exit(1);
				break;
			}
		}

		if (!fileArg.equals("")) {

			File configFile = new File(fileArg);

			if (configFile.exists()) {
				System.out.println("start with configuration file");
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
							System.setProperty("java.net.preferIPv4Stack",
									"true");
							NetworkInterface netinterface = null;
							try {
								netinterface = NetworkInterface
										.getByName(config[1]);
								Debug.output("host starting with interface: "
										+ config[1]);
							} catch (SocketException e) {
								e.printStackTrace();
							}
							if (netinterface == null) {
								System.out.println("wrong interface name");
								System.exit(1);
							}
							ourInterface = netinterface.getInterfaceAddresses()
									.get(0);
							break;
						default:
							System.out
									.println("there's an error in the configuration file");
							System.exit(1);
							break;
						}
					}
					input.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				//TODO missing cofig file
			}
		} else {
			/*BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			System.out.println("set the ID of this host");
			System.out.print(">>");
			String str = "";
			try {
				str = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			id = Integer.parseInt(str);*/
			
			id = Integer.parseInt(idArg);
			if (id <= 0) {
				System.out.println("the ID must be a positive integer number");
				System.exit(1);
			}
			Debug.output("host starting with id: " + id);

			ourInterface = getNetworkInterface();
		}

		Node thisnode = new Node(id, ourInterface.getAddress());
		// allocating the node/routing table
		NodeTable.createInstance(thisnode);
		// entering in maintenance state
		TreeMaintenance.getInstance().setName("TreeMaintenance service thread");
		TreeMaintenance.getInstance().start();
		// start messaging system
		MessageSystem.getInstance().setName("MessageSystem listener thread");
		MessageSystem.getInstance().start();
		// set up Connection
		Connection.start(ourInterface);
		// set up TCPConnection
		TCPConnection.start();
		// run repl loop
		Command repl = new Command();
		repl.run();
	}

	private static InterfaceAddress getNetworkInterface() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("available network interfaces");
		Enumeration<NetworkInterface> interfaces = null;
		try {
			for (interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
					.hasMoreElements();) {
				System.out.println(interfaces.nextElement());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.print(">>");
		String str = "";
		try {
			str = br.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		NetworkInterface netinterface = null;
		try {
			netinterface = NetworkInterface.getByName(str);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if (netinterface == null) {
			System.out.println("wrong name");
			System.exit(1);
		}
		return netinterface.getInterfaceAddresses().get(0);
	}
}