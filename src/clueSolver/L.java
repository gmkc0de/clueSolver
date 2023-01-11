package clueSolver;

public class L {

	public static String CURRENT_LEVEL = "info";
	public static String INFO = "info";
	public static String WARN = "warn";
	public static String ERROR = "error";
	public static String NONE = "none";

	public static void i(Object message) {
		if (CURRENT_LEVEL.equals(INFO)) {
			System.out.println(message);
		}
	}

	public static void i(int message) {
		if (CURRENT_LEVEL.equals(INFO)) {
			System.out.println(message);
		}
	}
	
	public static void w(Object message) {
		if (CURRENT_LEVEL.equals(WARN) || CURRENT_LEVEL.equals(INFO)) {
			System.out.println(message);
		}
	}

	public static void e(Object message) {
		if (CURRENT_LEVEL.equals(ERROR) || CURRENT_LEVEL.equals(WARN) || CURRENT_LEVEL.equals(INFO)) {
			System.out.println(message);
		}
	}

}
