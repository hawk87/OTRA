package app;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

import app.tree.TreeMaintenance;

public class Main {
	public static void main(String args[]) {
		System.out.println("set the ID of this host");
		Scanner sc = new Scanner(System.in);
		System.out.print(">>");
		String str = sc.next();
		sc.close();
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
		//entering in maintenance state
		TreeMaintenance.start();
	}
	
	private static InterfaceAddress getNetworkInterface() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		System.out.println("available network interfaces");
		Enumeration<NetworkInterface> interfaces = null;
		try {
			for(interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
				System.out.println(interfaces.nextElement());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(">>");
		Scanner sc = new Scanner(System.in);
		String str = sc.next();
		sc.close();
		NetworkInterface netinterface = null;
		try {
			netinterface = NetworkInterface.getByName(str);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(netinterface == null) {
			System.out.println("wrong name");
			System.exit(1);
		}
		return netinterface.getInterfaceAddresses().get(0);
	}
}