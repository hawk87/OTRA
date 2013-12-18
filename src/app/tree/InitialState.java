package app.tree;

import app.Debug;
import app.Message;
import app.Node;
import app.NodeTable;

public class InitialState extends OperationalState implements Runnable {

	private final int waitmsec = 500;
	private int waitcount;
	private boolean isAttached;
	
	private InitialState() {
		waitcount = 0;
		isAttached = false;
	}
	
	static InitialState init() {
		Debug.output("Entering initial state...");
		InitialState is = new InitialState();
		//starting a new thread that repeat the broadcast signal
		Thread thread = new Thread(is);
		thread.start();
		return is;
	}
	
	public void run() {
		NodeTable tbl = supervisor.getNodeTable();
		while(true) {
			synchronized (this) {
				if(isAttached)
					//then stop this thread because we are now attached to
					//the network
					break;
			}
			if(waitcount > 2) {
				//we deduce there isn't any network up and running
				//we become the root
				Debug.output("There is no online network");
				Debug.output("becoming root");
				tbl.setParent(null);
				nextState(new NormalState());
				break;
			}
			waitcount++;
			Debug.output("sending broadcast signal");
			Message.sendJoinBroadcast(tbl.getThisNode().getId());
			try {
				Thread.sleep(waitmsec);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//in the initial state this message comes from the node who's going to
	//host us in the tree
	void handleJoinSearch(Node n) {
		//set as parent the node telling us that it has a free post to attach to.
		supervisor.getNodeTable().setParent(n);
		synchronized (this) {
			isAttached = true;
		}
		//go into normal operational state
		nextState(new NormalState());
	}
}
