package app;

/**
 * This class read the serialized version of a binary tree. So the input is an
 * array of integers and the output is a string representing the tree.
 * This serves to maintain a state global to the recursion that inspect the 
 * tree, and, since this tree is binary so is the recursion and we have to
 * register the current reading position in a global variable to inspect the correct
 * subtree during the process.
 */
class SerialTree {
	
	/**
	 * Input of the procedure
	 */
	private int[] serialTree;
	/**
	 * Across all recursion calls, this field maintain the position of the
	 * current subtree in serialTree.
	 */
	private int position;
	
	SerialTree(int[] t) {
		serialTree = t;
		position = 0;
	}
	
	/**
	 * This method increase the pointer to which the call of read() interpret
	 * the beginning of the tree.
	 */
	private void increasePosition(int n) {
		if(serialTree.length - 1 - position < 0) {
			System.err.println("bad increasing of reading position");
			Thread.dumpStack();
			System.exit(1);
		}
		position += n;
	}
	
	public String read() {
		if(serialTree[position] == -1) {
			System.err.println("problems parsing the tree");
			Thread.dumpStack();
			System.exit(1);
		}
		String id = serialTree[position] + "";
		int spc = id.length() + 1;
		String sbtree;
		
		if(serialTree[position+1] != -1) {
			//then there is a left subtree
			increasePosition(1);
			sbtree = read();
			if(serialTree[position] != -1) {
				// right subtree
				return verticalSpace(spc, sbtree) + id + "\n" +
					verticalSpace(spc, read());
			} else {
				increasePosition(1);
				return verticalSpace(spc, sbtree) + id + "\n" +
					verticalSpace(spc, "-");
			}
		}
		
		if(serialTree[position+2] != -1) {
			//then there is a right subtree
			increasePosition(2);
			return verticalSpace(spc, "-") + id + "\n" +
					verticalSpace(spc, read());
		}
		
		//then this is a leaf node
		increasePosition(3);
		return id + "\n";
	}
	
	/**
	 * Insert n spaces at the beginning of each line in s
	 * @return modified string
	 */
	private String verticalSpace(int n, String s) {
		String spc = "";
		String printed = "";
		for(int k=0; k < n; k++)
			spc += " ";
		
		String[] lines = s.split("\n");
		for(int i=0; i < lines.length; i++) {
			printed += spc + lines[i] + "\n";
		}
		return printed;
	}
}
