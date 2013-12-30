package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
		case "print":
			print();
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
	
	private void print() {
		NodeTable tbl = NodeTable.getInstance();
		System.out.println(tbl);
	}

}
