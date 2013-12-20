package app.tree;

import app.Debug;
import app.Message;
import app.Node;
import app.NodeTable;

/**
 * Initial operational state. We send broadcast signal to inspect if there is
 * some OTRA network already up and running. Otherwise we set ourself as root
 * and listen for other joining nodes.
 */
class InitialState extends OperationalState implements Runnable {

	private final int waitmsec = 500;
	/**
	 * Number of times that a broadcast message do not receive any response.
	 * If this counter exceeds 3 then we assume there is no OTRA network running.
	 */
	private int waitcount;
	/**
	 * Boolean to see if we have received any response and if we are attached to
	 * some OTRA network.
	 */
	private boolean isAttached;
	
	private InitialState() {
		waitcount = 0;
		isAttached = false;
	}
	
	/**
	 * To set up the InitialState instance: a new instance and the start of a 
	 * new thread that executes the broadcast repetitions.
	 */
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
	
	/*
	 * This handle in this operational state serves to set the InetAddress of 
	 * this host itself. We capture the broadcast signal we generated and use the 
	 * ip address contained in it.
	 */
	void handleJoinBroadcast(Node n) {
		Node thisnode = supervisor.getNodeTable().getThisNode();
		//we check if the ip address of thisnode is null
		if(thisnode.getAddress() == null) {
			if(n.getId() == thisnode.getId()) {
				//we are receiving the broadcast signal we generated
				//we set the InetAddress
				thisnode.setAddress(n.getAddress());
			}
		}
	}
	
	/* 
	 * In the initial state this message comes from the node who's going to
	 * host us in the tree.
	 */
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
