package app;

public class Debug {
	
	private static String currentClass = "";
	/*
	 * Utility to print debugging information. 
	 * This method retrieve the caller class name.
	 */
	public static void output(String s) {
		Thread thisThread = Thread.currentThread();
		StackTraceElement[] stack = thisThread.getStackTrace();
		String classname = stack[2].getClassName();
		int linenumber = stack[2].getLineNumber();
		
		if (!currentClass.equals(classname)) {
			System.out.println("DEBUG: " + classname);
			currentClass = classname;
		}
		System.out.println("  " + s + " (line " + linenumber + ")");
	}
	
}
