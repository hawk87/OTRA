package app.tree;

import app.Node;
import app.NodeTable;

public final class TreeMaintenance {
	// node/routing table
	private NodeTable table;
	
	// Singleton design pattern
	private static TreeMaintenance INSTANCE;

	private TreeMaintenance(NodeTable nt) {
		// INSTANCE have to contain a pointer to the newly created instance
		INSTANCE = this;
		// we start maintenance from NormalState
		maintenanceState = new NormalState();
		table = nt;
	}
	
	public static TreeMaintenance getInstance() {
		if (INSTANCE == null)
			throw new IllegalStateException("TreeMaintenance not initialized");
		return INSTANCE;
	}

	public static TreeMaintenance getInstance(NodeTable nt) {
		if(INSTANCE == null) {
			INSTANCE = new TreeMaintenance(nt);
		} else
			throw new IllegalStateException("TreeMaint. already initialized");
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
	
	public void sizeFromChild(Node n, int s) {
		maintenanceState.sizeFromChild(n, s);
	}
	
	public void touchFromParent(Node n) {
		maintenanceState.touchFromParent(n);
	}
	
	public void joinBroadcast(Node n) {
		maintenanceState.joinBroadcast(n);
	}
	
	public void joinSearch(Node n) {
		maintenanceState.joinSearch(n);
	}

}
