package app.tree;

import java.net.InetAddress;

import app.Node;

/**
 * This class holds the current operational state and route incoming messages
 * to the proper object methods.
 */
public final class TreeMaintenance extends Thread {
	private final int WAITMSEC = 1000;

	/**
	 * Holds the current operational state. This can be NormalState, Balancing ecc..
	 */
	private OperationalState maintenanceState;
	
	/**
	 * height of the left subtree
	 */
	int leftHeight;
	/**
	 * height of the right subtree
	 */
	int rightHeight;
	
	/**
	 * This enumerate the broadcast messages we sent in order to recognize
	 * multiple receptions of the same message.
	 */
	int broadcastNumber;
	
	// Singleton design pattern
	private static TreeMaintenance INSTANCE = new TreeMaintenance();

	private TreeMaintenance() {
		//we start with the joining phase
		maintenanceState = new JoiningState();
		broadcastNumber = (int) (Math.random() * Integer.MAX_VALUE);
	}
	
	public void run() {
		while (true) {
			maintenanceState.service();	
			try {
				Thread.sleep(WAITMSEC);
			} catch (InterruptedException e) {
				//we use InterruptException to wake up this thread
				//so, do nothing
			}
		}
	}
	
	void forceService() {
		//wake up this sleeping thread
		this.interrupt();
	}
	
	/**
	 * Retrieve the single instance of this class. If there was not previously
	 * any call to start() method this generate an abort.
	 */
	public static TreeMaintenance getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Compute the height of this node
	 */
	int computeHeight() {
		if(leftHeight >= rightHeight)
			return leftHeight + 1;
		else
			return rightHeight + 1;
	}
	
	boolean isInRecoveryState() {
		return RecoveryState.class.isInstance(maintenanceState);
	}
	
	/**
	 * Used when there is some operational-state change. From one state we change to
	 * another consequential new state. Called from OperationalState class.
	 */
	void changeState(OperationalState os) {
		maintenanceState = os;
	}
	
	//
	// INCOMING MESSAGES HANDLES
	//
	
	public void handlehHeight(Node n, int s) {
		maintenanceState.handleHeight(n, s);
	}
	
	public void handleJoinBroadcast(Node n) {
		maintenanceState.handleJoinBroadcast(n);
	}
	
	public void handleJoinResponse() {
		maintenanceState.handleJoinResponse();
	}
	
	public void handleJoinSearch(Node n) {
		maintenanceState.handleJoinSearch(n);
	}
	
	public void handlePrint() {
		maintenanceState.handlePrint();
	}
	
	public void handleBalance(Node from, Node anode) {
		maintenanceState.handleBalance(from, anode);
	}
	
	public void handleSetParent(Node from, Node x) {
		maintenanceState.handleSetParent(from, x);
	}

	public void handleSetLeft(Node from, Node x) {
		maintenanceState.handleSetLeft(from, x);
	}
	
	public void handleSetRight(Node from, Node x) {
		maintenanceState.handleSetRight(from, x);
	}
	
	public void handleDisconnected(Node disc, InetAddress from) {
		maintenanceState.handleDisconnected(disc, from);
	}
	
	public void handleDscnnResponse(Node sib) {
		maintenanceState.handleDscnnResponse(sib);
	}
	
	public void handleRecoveryFindMax(Node left, Node right) {
		maintenanceState.handleRecoveryFindMax(left, right);
	}
}
