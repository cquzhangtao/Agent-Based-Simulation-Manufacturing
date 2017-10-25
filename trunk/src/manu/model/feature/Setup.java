package manu.model.feature;

import java.util.ArrayList;



public class Setup {
	public String setupType;
	public ArrayList<SubSetup> subSetups;
	public String typeofCurJob;
	public String stepofCurJob;

	public Setup(String setupType, String typeofCurJob, String stepofCurJob) {
		this.setupType = setupType;
		this.typeofCurJob = typeofCurJob;
		this.stepofCurJob = stepofCurJob;
		subSetups = new ArrayList<SubSetup>();
	}

	public void addforSingle(String typeofPreJob, String stepofPreJob,
			double setupTime) {
		SubSetup subSetup = new SubSetup();
		subSetup.typeofPreJob = typeofPreJob;
		subSetup.stepofPreJob = stepofPreJob;
		subSetup.setupTime = setupTime;
		subSetups.add(subSetup);

	}

	public void addforBatch(int preBatchID, int curBatchID, double setupTime) {
		SubSetup subSetup = new SubSetup();
		subSetup.preBatchID = preBatchID;
		subSetup.curBatchID = curBatchID;
		subSetup.setupTime = setupTime;
		subSetups.add(subSetup);

	}

	public double getSetupTime(String typeofPreJob, String stepofPreJob) {
		if (typeofPreJob.equals("") || stepofPreJob.equals(""))
			return 0;
		for (int i = 0; i < subSetups.size(); i++) {
			SubSetup sub = subSetups.get(i);
			if (sub.typeofPreJob.equals(typeofPreJob)
					&& sub.stepofPreJob.equals(stepofPreJob)) {
				return sub.setupTime;
			}
		}
		return 0;
	}

	public double getSetupTime() {
		if (subSetups.size() < 1)
			return 0;
		// System.out.println("Setup Time:"+subSetups.get(0).setupTime);
		return subSetups.get(0).setupTime;

	}

	public double getSetupTime(int preBatchID, int curBatchID) {
		if (preBatchID == 0 || curBatchID == 0)
			return 0;
		for (int i = 0; i < subSetups.size(); i++) {
			SubSetup sub = subSetups.get(i);
			if (sub.curBatchID == curBatchID && sub.preBatchID == preBatchID) {
				return sub.setupTime;
			}
		}
		return 0;
	}

	public double getSetupTime(int curBatchID) {
		if (curBatchID == 0)
			return 0;
		for (int i = 0; i < subSetups.size(); i++) {
			SubSetup sub = subSetups.get(i);
			if (sub.curBatchID == curBatchID) {
				return sub.setupTime;
			}
		}
		return 0;
	}

	public void add(SubSetup subSetup) {
		subSetups.add(subSetup);

	}

}
