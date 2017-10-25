package manu.model.component;


public class DispatchingRule {
	private static double[] ruleValue = new double[17];
	private static void updateValue(int currentRule, long currentTime, JobInfo jobInfo, ToolInfo toolInfo) {
		switch (currentRule) {
		case 0:
			//try{
			ruleValue[0] = currentTime-jobInfo.getStateStartTime();
//			}catch(NullPointerException e){
//				int a=0;
//				a=1;
//			}
			break;
		case 1:
			ruleValue[1] = jobInfo.getStateStartTime();
			break;
		case 2:
			ruleValue[2] = 1.0/jobInfo.getRemainTime().getAvg();
			break;
		case 3:
			ruleValue[3] =  jobInfo.getRemainTime().getAvg();
			break;
		case 4:
			ruleValue[4] = -jobInfo.getCurrentProcessTime();
			break;
		case 5:
			ruleValue[5] = jobInfo.getCurrentProcessTime();
			break;
		case 6:
			ruleValue[6] =currentTime- jobInfo.getReleaseTime();
			break;
		case 7:
			ruleValue[7] = jobInfo.getReleaseTime();
			break;
		case 8:
			ruleValue[8] = 1.0/jobInfo.getRemainStep();
			break;
		case 9:
			ruleValue[9] =  jobInfo.getRemainStep();
			break;
		case 10:
			ruleValue[10] = (currentTime - jobInfo.getReleaseTime()) + jobInfo.getRemainTime().getAvg();
			break;
		case 11:
			ruleValue[11] = 1.0/(jobInfo.getDueDate() - jobInfo.getRemainTime().getAvg() - currentTime);
			break;
		case 12:
			ruleValue[12] = 1.0/jobInfo.getDueDate();
			break;
		case 13:
			ruleValue[13] = 1.0/Math.max(jobInfo.getDueDate(), jobInfo.getRemainTime().getAvg() + currentTime);
			break;
		case 14:
			ruleValue[14] = 1.0* jobInfo.getRemainTime().getAvg()/(jobInfo.getDueDate() - currentTime) ;
			break;
		case 15:
			ruleValue[15] = 1.0*jobInfo.getRemainStep()/(jobInfo.getDueDate() - jobInfo.getRemainTime().getAvg() - currentTime);
			break;
		case 16:
			ruleValue[16]=1.0/toolInfo.getSetupTime(jobInfo);

			
			break;
//		case 17:
//			ruleValue[17]=requestTool.getSetupTime(this);
//			break;

		default:
			//ruleValue[0] = currentTime-beginWaitTime;
			System.out.println("rule errors");
			System.exit(0);
			break;
		}
	}

	public static double getPriorityValue(int currentRule, long currentTime,JobInfo jobInfo, ToolInfo toolInfo) {
		updateValue(currentRule, currentTime,jobInfo,toolInfo);
		return ruleValue[currentRule];
	}

}
