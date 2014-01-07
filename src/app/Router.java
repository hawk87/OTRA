package app;

public class Router {

	private static Router INSTANCE = new Router();
	
	private OTRAFile leftTree;
	private OTRAFile rightTree;
			
	private Router() {
		
	}
	
	public static Router getInstance() {
		return INSTANCE;
	}
	
	private void routePrintingFile(OTRAFile f, Node from) {
		NodeTable tbl = NodeTable.getInstance();
		
		if(tbl.isLeftNode(from) && rightTree != null) {
			
			
			//set to default condition
			leftTree = null;
			rightTree = null;
		} else if (tbl.isRightNode(from) && leftTree != null) {
			
			
			//set to default condition
			leftTree = null;
			rightTree = null;
		}
		
		if(tbl.isLeftNode(from)) {
			//then we have to wait for information coming from right subtree
			leftTree = f;
		}
		
		if(tbl.isRightNode(from)) {
			// wait for information coming from left subtree
			rightTree = f;
		}
	}
	
	/**
	 * Utility to append an array to another.
	 * @param first
	 * @param second
	 * @return a (first.length + second.length) long array containing the two
	 * concatenated.
	 */
	private static byte[] appendArray(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}
	
}
