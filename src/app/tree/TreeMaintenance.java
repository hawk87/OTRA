package app.tree;

import app.Node;
import app.NodeTable;

/**
 * This class holds the current operational state and route incoming messages
 * to the proper object methods.
 */
public final class TreeMaintenance implements Runnable {
	private final int WAITMSEC = 1000;
	/**
	 * reference to the node/routing table
	 */
	private NodeTable table;
	
	/**
	 * number of elements in the left subtree
	 */
	private int leftSize;
	/**
	 * number of elements in the right subtree
	 */
	private int rightSize;
	
	// Singleton design pattern
	private static TreeMaintenance INSTANCE;

	private TreeMaintenance() {
		table = NodeTable.getInstance();
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
	
	NodeTable getNodeTable() {
		return table;
	}
	
	int getLeftSize() {
		return leftSize;
	}
	
	int getRightSize() {
		return rightSize;
	}
	
	void setLeftSize(int s) {
		leftSize = s;
	}
	
	void setRightSize(int s) {
		rightSize = s;
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
	
	public void handleSize(Node n, int s) {
		maintenanceState.handleSize(n, s);
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

}
