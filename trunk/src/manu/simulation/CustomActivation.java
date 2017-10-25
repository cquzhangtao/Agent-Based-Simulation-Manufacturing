package manu.simulation;

import simulation.framework.ActivationPriority;

public class CustomActivation implements ActivationPriority {

	public static String IsBlockYet = "Is block yet?";
	public static String TransportEnd = "Transport End";
	public static String ProessFinish = "Process Finish";
	public static String JobRelease = "Job Release";
	public static String SetupEnd = "Setup End";
	public static String DataAcqu = "Data Acquisition";
	// public static String BreakDown="Breakdown";
	// public static String Maintenance="Maintenacne";
	// public static String Repair="Repair";
	public static String Interrupt = "Interrupt Occur";
	public static String Recovery = "Recovery";

	@Override
	public int getPriority(String title) {
		// TODO Auto-generated method stub
		int priority = 0;
		if (title.equals(CustomActivation.ProessFinish))
			priority = 0;
		else if (title.equals(CustomActivation.SetupEnd))
			priority = 1;
		else if (title.equals(CustomActivation.Recovery))
			priority = 2;
		else if (title.equals(CustomActivation.TransportEnd))
			priority = 3;
		else if (title.equals(CustomActivation.IsBlockYet))
			priority = 4;
		else if (title.equals(CustomActivation.Interrupt))
			priority = 5;
		else if (title.equals(CustomActivation.JobRelease))
			priority = 6;
		else if (title.equals(CustomActivation.DataAcqu))
			priority = 7;
		else
			priority = 0;
		return priority;
	}
}
