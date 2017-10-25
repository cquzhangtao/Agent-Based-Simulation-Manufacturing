package manu.model.component;

import java.util.Random;

public class AllocationRule {

	public static int SQL = 0;// shortest queue length
	public static int STT = 1;// shortest transporting time
	public static int SPT = 2;// shortest processing time
	public static int STPT = 3;// shortest transporting and processing time
	public static int RANDOM = 4;
	public static int STSPT = 5;// shortes transporting,setup,processing time
	public static int STPT_M = 6;// //shortes transporting,processing time
	public static int MINUTI=7;//minimal utilization
	public static int SQT=8;//shortest queue time

	private static double[] ruleValue = new double[9];

	private static void updateValue(int currentRule, long currentTime, JobInfo jobInfo, ToolGroupInfo toolGroupInfo, BufferInfo bufferInfo) {
		switch (currentRule) {
		case 0:
			ruleValue[0] = 1.0 / bufferInfo.size();
			break;
		case 1:
			if (jobInfo.getCurrentProcessIndex() == 0) {
				ruleValue[1] = 0;
			} else {
				ruleValue[1] = 1.0 / jobInfo.getTransportTimePool().get(jobInfo.getPreToolGroupName() + toolGroupInfo.getToolGroupName());
			}
			break;
		case 2:
			ruleValue[2] = 1.0 / jobInfo.getProcessingTimePool().get(toolGroupInfo.getToolGroupName() + jobInfo.getCurrentProcessName());
			break;
		case 3:
			if (jobInfo.getCurrentProcessIndex() == 0) {
				ruleValue[3] = 1.0 / jobInfo.getProcessingTimePool().get(toolGroupInfo.getToolGroupName() + jobInfo.getCurrentProcessName());
			} else {
				ruleValue[3] = 1.0 / (jobInfo.getTransportTimePool().get(jobInfo.getPreToolGroupName() + toolGroupInfo.getToolGroupName()) + jobInfo
						.getProcessingTimePool().get(toolGroupInfo.getToolGroupName() + jobInfo.getCurrentProcessName()));

			}
			break;
		case 4:
			Random rnd = new Random();
			ruleValue[4] = rnd.nextDouble();
			break;
		case 5:
			// ruleValue[5]=toolGroupInfo.getSetups().get(key);
			break;
		case 6:
			double proc;

			if (bufferInfo.size() > 2) {
				ruleValue[6] = 0;
			} else {

				if (jobInfo.getCurrentProcessIndex() == 0) {
					proc = 1.0 / jobInfo.getProcessingTimePool().get(toolGroupInfo.getToolGroupName() + jobInfo.getCurrentProcessName());
				} else {
					proc = 1.0 / (jobInfo.getTransportTimePool().get(jobInfo.getPreToolGroupName() + toolGroupInfo.getToolGroupName()) + jobInfo
							.getProcessingTimePool().get(toolGroupInfo.getToolGroupName() + jobInfo.getCurrentProcessName()));

				}
				ruleValue[6] = proc;
			}
			break;
			
		case 7:
			ruleValue[7]=-1*toolGroupInfo.getUtilization();
			//toolGroupInfo.geta
			break;
		case 8:
			ruleValue[8]=1.0/toolGroupInfo.getBufferInfo().getBufferTime();
			//toolGroupInfo.geta
			break;

		default:
			System.out.println("rule errors");
			System.exit(0);
			break;
		}
	}

	public static double getPriorityValue(int currentRule, long currentTime, JobInfo jobInfo, ToolGroupInfo toolGroupInfo) {
		updateValue(currentRule, currentTime, jobInfo, toolGroupInfo, toolGroupInfo.getBufferInfo());
		return ruleValue[currentRule];
	}

}
