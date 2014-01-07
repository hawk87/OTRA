package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import app.tree.TreeMaintenance;

public class Main {
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		System.out.println("set the ID of this host");
		System.out.print(">>");
		String str = "";
		try {
			str = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int id = Integer.parseInt(str);
		if(id <= 0) {
			System.out.println("the ID must be a positive integer number");
			System.exit(1);
		}
		Debug.output("host starting with id: " + id);
		
		InterfaceAddress ourInterface = getNetworkInterface();
		
		Node thisnode = new Node(id, ourInterface.getAddress());
		//allocating the node/routing table
		NodeTable.createInstance(thisnode);
		//set up Connection
		Connection.start(ourInterface);
		//set up TCPConnection
		TCPConnection.start();
		//entering in maintenance state
		TreeMaintenance.start();
		//run repl loop
		Command repl = new Command();
		repl.run();
	}
	
	private static InterfaceAddress getNetworkInterface() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		System.out.println("available network interfaces");
		Enumeration<NetworkInterface> interfaces = null;
		try {
			for(interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
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
		if(netinterface == null) {
			System.out.println("wrong name");
			System.exit(1);
		}
		return netinterface.getInterfaceAddresses().get(0);
	}
}