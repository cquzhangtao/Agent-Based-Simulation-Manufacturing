package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class WaitDataset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1566768495213338359L;
	public String bufferName;
	public double maxWaitTime = 0;
	public double avgWaitTime = 0;
	public int interval = 7 * 24 * 3600;
	ArrayList<WaitData> waitDataset = new ArrayList<WaitData>();
	public ArrayList<Double> waitDatasetbyDay = new ArrayList<Double>();

	private int waitDatasetSize;

	public WaitDataset(int period) {
		// TODO Auto-generated constructor stub
		interval = period;
	}

	public void save() {
		waitDatasetSize = waitDataset.size();
	}

	public void recover() {
		while (waitDataset.size() > waitDatasetSize)
			waitDataset.remove(waitDatasetSize);
	}

	public void add(String jobName, long endWaitTime, long waitTime) {
		waitDataset.add(new WaitData(jobName, endWaitTime, waitTime));
	}

	public void reset() {
		maxWaitTime = 0;
		avgWaitTime = 0;
		waitDataset.clear();
		waitDatasetbyDay.clear();
	}

	public void stat() {
		int count = 0;
		int time = interval;
		long sum = 0;
		for (int i = 0; i < waitDataset.size(); i++) {
			if (waitDataset.get(i).endWaitTime < time) {
				count++;
				sum += waitDataset.get(i).waitTime;
			} else {
				if (count == 0)
					waitDatasetbyDay.add(0.0);
				else {
					waitDatasetbyDay.add(1.0 * sum / count / 3600);
					count = 0;
				}
				sum = 0;

				time += interval;
				i = i - 1;
			}
		}
		sum = 0;
		for (int i = 0; i < waitDataset.size(); i++) {
			if (waitDataset.get(i).waitTime > maxWaitTime) {
				maxWaitTime = waitDataset.get(i).waitTime;
			}
			sum += waitDataset.get(i).waitTime;
		}
		if (waitDataset.size() > 0)
			avgWaitTime = 1.0 * sum / waitDataset.size() / 3600;
		else
			avgWaitTime = 0;
		maxWaitTime = 1.0 * maxWaitTime / 3600;
		waitDataset.clear();
		// waitDataset=null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return waitDatasetbyDay.size();
	}

}
