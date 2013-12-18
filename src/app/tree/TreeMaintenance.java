package app.tree;

import app.Node;
import app.NodeTable;

public final class TreeMaintenance {
	// node/routing table
	private NodeTable table;
	
	// Singleton design pattern
	private static TreeMaintenance INSTANCE;

	private TreeMaintenance() {
		// we start maintenance from InitialState
		maintenanceState = InitialState.init();
		table = NodeTable.getInstance();
	}
	
	public static void startMaintenance() {
		if(INSTANCE != null) {
			System.out.println("maintenance already started..!!");
			System.exit(1);
		}
		INSTANCE = new TreeMaintenance();
	}
	
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

	// holds the current operational state
	// this can be NormalState, BalancingState ecc...
	private OperationalState maintenanceState;
	
	void changeState(OperationalState os) {
		maintenanceState = os;
	}
	
	//
	// INCOMING MESSAGES HANDLES
	//
	
	public void handleSize(Node n, int s) {
		maintenanceState.handleSize(n, s);
	}
	
	public void handleTouch(Node n) {
		maintenanceState.handleTouch(n);
	}
	
	public void handleJoinBroadcast(Node n) {
		maintenanceState.handleJoinBroadcast(n);
	}
	
	public void handleJoinSearch(Node n) {
		maintenanceState.handleJoinSearch(n);
	}

}
