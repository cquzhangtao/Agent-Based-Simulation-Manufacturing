package manu.model.component.state;

public class ToolState {
	private static String[] toolStateStr = new String[] { "FREE", "SETUP",
			"BUSY", "BREAKDOWN", "BLOCK", "MAINTENANCE" };
	public static int FREE = 0;
	public static int SETUP = 1;
	public static int BUSY = 2;
	public static int BREAKDOWN = 3;
	public static int BLOCK = 4;
	public static int MAINTENANCE = 5;

	public static String int2String(int state) {
		return toolStateStr[state];
	}
}