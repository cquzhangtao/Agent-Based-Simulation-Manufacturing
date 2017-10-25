package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

import manu.model.component.Product;

public class ReleaseDataset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4127604839266068528L;
	private ArrayList<ReleaseData> releaseDataset = new ArrayList<ReleaseData>();
	public ReleaseData allReleaseDataset = new ReleaseData("All");

	public void init(Product[] products, int period) {
		allReleaseDataset.interval = period;
		for (int i = 0; i < products.length; i++) {
			ReleaseData rd = new ReleaseData(products[i].getName());
			rd.rawProcessTime = products[i].getRawProcessingTime().getAvg() / 3600.0;
			rd.interval = period;
			releaseDataset.add(rd);
		}
	}

	public void save() {
		for (ReleaseData r : releaseDataset)
			r.save();
		allReleaseDataset.save();
	}

	public void recover() {
		for (ReleaseData r : releaseDataset)
			r.recover();
		allReleaseDataset.recover();
	}

	public void add(ReleaseData releaseData) {
		releaseDataset.add(releaseData);
	}

	public void addWIP(int index, long time, int iwip, int allWip) {
		releaseDataset.get(index).addWIP(time, iwip);
		allReleaseDataset.addWIP(time, allWip);
	}

	public void addCycleTime(int index, long time, double cycleTime) {
		releaseDataset.get(index).addCycleTime(time, cycleTime);
		allReleaseDataset.addCycleTime(time, cycleTime);
	}

	public void addReleaseTime(int index, long time) {
		releaseDataset.get(index).addReleaseTime(time);
		allReleaseDataset.addReleaseTime(time);
	}

	public void stat() {
		for (int i = 0; i < releaseDataset.size(); i++) {
			releaseDataset.get(i).stat();
		}
		allReleaseDataset.stat();
	}

	public void reset() {
		for (int i = 0; i < releaseDataset.size(); i++) {
			releaseDataset.get(i).reset();
		}
		allReleaseDataset.reset();
	}

	public int size() {
		return releaseDataset.size();
	}

	public ReleaseData get(int index) {
		return releaseDataset.get(index);
	}

	public ReleaseData getAllReleaseDataset() {
		return allReleaseDataset;
	}

	public void setAllReleaseDataset(ReleaseData allReleaseDataset) {
		this.allReleaseDataset = allReleaseDataset;
	}

	public ArrayList<ReleaseData> getReleaseDataset() {
		return releaseDataset;
	}

	public void setReleaseDataset(ArrayList<ReleaseData> releaseDataset) {
		this.releaseDataset = releaseDataset;
	}
}
