package manu.model.feature;

import java.util.ArrayList;


public class Setups {
	public ArrayList<Setup> setups = null;

	public Setups() {

		setups = new ArrayList<Setup>();
	}

	public void addSetup(Setup setup) {
		setups.add(setup);
	}

	public Setup getSetup(String typeofCurJob, String stepofCurJob) {
		for (int i = 0; i < setups.size(); i++) {
			if (setups.get(i).typeofCurJob.equals(typeofCurJob)
					&& setups.get(i).stepofCurJob.equals(stepofCurJob)) {
				return setups.get(i);
			}
		}
		return null;
	}

	public String toString(String toolGroupName, String isBatchTool) {
		String s = "************************************************************************\n";
		s += "************************************************************************\n";
		s += "Tool group " + toolGroupName + " setup time\n";
		for (int i = 0; i < setups.size(); i++) {
			Setup setup = setups.get(i);
			s += "************************************\n";
			s += String
					.format("Type of current job: %s, step of current job: %s, setup type:%s\n",
							setup.typeofCurJob, setup.stepofCurJob,
							setup.setupType);
			s += "************************************\n";

			for (int j = 0; j < setup.subSetups.size(); j++) {
				if (setup.setupType.equals("sequence-dependent")) {
					if (isBatchTool.equals("True")) {
						s += String
								.format(j
										+ "  Current batchID: %d, previous batchID: %d, setup time: %.2f\n",
										setup.subSetups.get(j).preBatchID,
										setup.subSetups.get(j).curBatchID,
										setup.subSetups.get(j).setupTime);
					} else {
						s += String
								.format(j
										+ "  Type of previous job: %s, Step of previous job: %s, setup time: %.2f\n",
										setup.subSetups.get(j).typeofPreJob,
										setup.subSetups.get(j).stepofPreJob,
										setup.subSetups.get(j).setupTime);
					}
				} else if (setup.setupType.equals("sequence-independent")) {
					if (isBatchTool.equals("True")) {
						s += String.format(j
								+ "  Current batchID: %d, setup time: %.2f\n",
								setup.subSetups.get(j).curBatchID,
								setup.subSetups.get(j).setupTime);
					} else {
						s += String.format(j + "  Setup time: %.2f\n",
								setup.subSetups.get(j).setupTime);
					}
				}
			}

		}
		return s;
	}
}
