package app;

public class Debug {
	
	private static String currentClass = "";
	/**
	 * Utility to print debugging information. 
	 * This method retrieves the caller's class name, method name and line.
	 */
	public static void output(String s) {
		Thread thisThread = Thread.currentThread();
		StackTraceElement[] stack = thisThread.getStackTrace();
		//stack[0] is the Thread class
		//stack[1] is this class
		//stack[2] is what we need to inspect
		String classname = stack[2].getClassName();
		String methodname = stack[2].getMethodName();
		int linenumber = stack[2].getLineNumber();
		
		if (!currentClass.equals(classname)) {
			System.out.println("DEBUG: " + classname);
			currentClass = classname;
		}
		System.out.println("  " + s + " (" + methodname + " " + linenumber + ")");
	}
	
}
