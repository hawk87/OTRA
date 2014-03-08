package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.communication.MessageSystem;

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
	
	private void read(String cmd) throws IOException {
		String[] tks = cmd.split("[ |\t]+");
		switch (tks[0]) {
		case "table":
			table();
			break;
		case "net":
			printNetwork();
			break;
		case "send": // send <file path>
			Path path = Paths.get(tks[1]);
			byte[] data = Files.readAllBytes(path);
			Debug.output("Data read");

			String filename = path.getFileName().toString();
			int ID = Integer.parseInt(filename.split("_")[0]);
			Debug.output("Got ID");
			
			if(ID > 0) {
				OTRAFile file = new OTRAFile(ID, filename, data);
				Debug.output("OTRAFile created");
				Debug.output("Ready to send");
				Router.getInstance().forward(file);
				//Router.getInstance().route(file, NodeTable.getInstance().getThisNode());
				Debug.output("File sent");
			}
			else Debug.output("Unknown ID");
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
		MessageSystem.sendPrint();
	}
	
	public static void println(String s) {
		System.out.println("");
		System.out.println(s);
		System.out.print(">> ");
	}
}
