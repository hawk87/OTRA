package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import app.communication.Message;

public class Command {
	
	/**
	 * Read-Commands infinite loop
	 */
	public void run() {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		String input;
		
		while(true) {
			try {
				System.out.print(">> ");
				input = br.readLine();
				read(input);
				
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private void read(String cmd) {
		String[] tks = cmd.split("[ |\t]+");
		switch (tks[0]) {
		case "table":
			table();
			break;
		case "net":
			printNetwork();
			break;
		case "send":
			//TODO
			break;
		case "exit":
			System.exit(0);
			break;
		default:
			System.out.println("invalid command");
			break;
		}
	}
	
	private void table() {
		NodeTable tbl = NodeTable.getInstance();
		System.out.println(tbl);
	}
	
	private void printNetwork() {
		Message.sendPrint();
	}
	
	public static void println(String s) {
		System.out.println("");
		System.out.println(s);
		System.out.print(">> ");
	}
}
