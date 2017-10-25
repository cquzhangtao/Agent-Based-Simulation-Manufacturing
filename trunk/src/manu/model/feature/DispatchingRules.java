package manu.model.feature;

public class DispatchingRules {
	public static int FIFO = 0;// first in first out
	public static int LIFO = 1;// last in first out
	public static int SPRT = 2;// shortest remain process time
	public static int LPRT = 3;// longest remain process time
	public static int SPT = 4;// shortest process time
	public static int LPT = 5;// longest process time
	public static int EAT = 6;// earliest arrive time
	public static int LAT = 7;// latest arrive time
	public static int SRPS = 8;// smallest remain process steps
	public static int LRPS = 9;// largest remain process steps
	public static int AT_RPT = 10;// Arrival Time-total Remaining Process Time
	public static int MST = 11;// minimum slack time
	public static int EDD = 12;// earliest due date
	public static int MDD = 13;// modified due date
	public static int CR = 14;// critical ratio
	public static int SPRO = 15;// slack per remaining operation
	public static int SetupAvoid = 16;// look ahead
	public static int LookAhead = 17;// look ahead
	

	public static int String2Int(String ruleName) {
		if (ruleName.equals("FIFO"))
			return 0;
		else if (ruleName.equals("LIFO"))
			return 1;
		else if (ruleName.equals("SPRT"))
			return 2;
		else if (ruleName.equals("LPRT"))
			return 3;
		else if (ruleName.equals("SPT"))
			return 4;
		else if (ruleName.equals("LPT"))
			return 5;
		else if (ruleName.equals("EAT"))
			return 6;
		else if (ruleName.equals("LAT"))
			return 7;
		else if (ruleName.equals("SRPS"))
			return 8;
		else if (ruleName.equals("LRPS"))
			return 9;
		else if (ruleName.equals("AT_RPT"))
			return 10;
		else if (ruleName.equals("MST"))
			return 11;
		else if (ruleName.equals("EDD"))
			return 12;
		else if (ruleName.equals("MDD"))
			return 13;
		else if (ruleName.equals("CR"))
			return 14;
		else if (ruleName.equals("SPRO"))
			return 15;
		else if (ruleName.equals("SetupAvoid"))
			return 16;
		System.out.println("rule errors");
		System.exit(0);
		return 0;
	}
}