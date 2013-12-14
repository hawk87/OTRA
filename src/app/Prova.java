package app;

import app.tree.Responder;

public class Prova {
	
	public static void main(String args[]) {
		Thread t = new Thread(new Responder());
		t.start();
	}
}
