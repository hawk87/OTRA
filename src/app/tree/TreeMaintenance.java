package app.tree;

import app.Node;
import app.NodeTable;

public final class TreeMaintenance {
	// node/routing table
	private NodeTable table;
	
	// Singleton design pattern
	private static TreeMaintenance INSTANCE = new TreeMaintenance();

	private TreeMaintenance() {
		// INSTANCE have to contain a pointer to the newly created instance
		INSTANCE = this;
		// we start maintenance from NormalState
		maintenanceState = new NormalState();
		table = NodeTable.getInstance();
	}
	
	public static TreeMaintenance getInstance() {
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
