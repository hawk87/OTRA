package app.tree;

import app.Node;

/**
 * This class holds the current operational state and route incoming messages
 * to the proper object methods.
 */
public final class TreeMaintenance implements Runnable {
	private final int WAITMSEC = 1000;
	
	/**
	 * height of the left subtree
	 */
	int leftHeight;
	/**
	 * height of the right subtree
	 */
	int rightHeight;
	
	// Singleton design pattern
	private static TreeMaintenance INSTANCE;

	private TreeMaintenance() {
	}
	
	/**
	 * Since this class implements the singleton design patter, this is the only
	 * way to create the single instance.
	 */
	public static void start() {
		if(INSTANCE != null) {
			System.out.println("maintenance already started..!!");
			System.exit(1);
		}
		INSTANCE = new TreeMaintenance();
		// we start maintenance from InitialState
		INSTANCE.maintenanceState = InitialState.init();
		Thread th = new Thread(INSTANCE);
		th.setName("TreeMaintenance service thread");
		th.start();
	}
	
	public void run() {
		while (true) {
			maintenanceState.service();
			try {
				Thread.sleep(WAITMSEC);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retrieve the single instance of this class. If there was not previously
	 * any call to start() method this generate an abort.
	 */
	public static TreeMaintenance getInstance() {
		if(INSTANCE == null) {
			System.out.println("maintenance not started...abort");
			System.exit(1);
		}
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

	/**
	 * Holds the current operational state. This can be NormalState, Balancing ecc..
	 */
	private OperationalState maintenanceState;
	
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
}
