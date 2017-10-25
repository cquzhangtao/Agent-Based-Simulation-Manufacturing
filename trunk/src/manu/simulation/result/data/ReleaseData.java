package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ReleaseData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4565579778253675246L;
	public String product;
	ArrayList<Integer> wip = new ArrayList<Integer>();
	ArrayList<Long> wipChangeTime = new ArrayList<Long>();
	ArrayList<Double> cycleTime = new ArrayList<Double>();
	ArrayList<Long> receivedTime = new ArrayList<Long>();
	ArrayList<Long> releaseTime = new ArrayList<Long>();
	public 	int interval = 7 * 24 * 3600;
	
	int wipSize;
	int wipChangeTimeSize;
	int cycleTimeSize;
	int receivedTimeSize;
	int releaseTimeSize;
	
	public ArrayList<Double> wipbyDay = new ArrayList<Double>();
	public ArrayList<Double> cycleTimebyDay = new ArrayList<Double>();
	public ArrayList<Integer> finishedLotsbyDay = new ArrayList<Integer>();
	public ArrayList<Integer> releaseLotsbyDay = new ArrayList<Integer>();
	public int finishedLotNum = 0;
	public int releasedLotNum = 0;
	public int maxWip = 0;
	public double avgWip;
	public double minCycleTime = 99999999;
	public double maxCycleTime = 0;
	public double avgCycleTime;
	public double releaseRatio = 0;
	public double productivity = 0;
	public double rawProcessTime = 0;
	
	public void save(){
		wipSize=wip.size();
		wipChangeTimeSize=wipChangeTime.size();
		cycleTimeSize=cycleTime.size();
		receivedTimeSize=receivedTime.size();
		releaseTimeSize=releaseTime.size();
	}
	public void recover(){
		while(wip.size()>wipSize)
			wip.remove(wipSize);
		while (wipChangeTime.size()>wipChangeTimeSize)
			wipChangeTime.remove(wipChangeTimeSize);
		while(cycleTime.size()>cycleTimeSize)
			cycleTime.remove(cycleTimeSize);
		while(receivedTime.size()>receivedTimeSize)
			receivedTime.remove(receivedTimeSize);
		while(releaseTime.size()>releaseTimeSize)
			releaseTime.remove(releaseTimeSize);
			
	}

	public ReleaseData(String product) {
		this.product = product;
	}
	public void reset(){
		wipbyDay.clear();
		cycleTimebyDay.clear();
		finishedLotsbyDay.clear();
		releaseLotsbyDay.clear();
		wip.clear();
		//wip = null;
		wipChangeTime.clear();
		//wipChangeTime = null;
		cycleTime.clear();
		//cycleTime = null;
		receivedTime.clear();
		//receivedTime = null;
		releaseTime.clear();
		//releaseTime = null;
		finishedLotNum = 0;
		releasedLotNum = 0;
		maxWip = 0;
		avgWip=0;
		minCycleTime = 99999999;
		maxCycleTime = 0;
		avgCycleTime=0;
		releaseRatio = 0;
		 productivity = 0;
		//rawProcessTime = 0;
	}

	public void addCycleTime(long time, double cycleTime) {
		this.cycleTime.add(cycleTime);
		receivedTime.add(time);

	}

	public void addReleaseTime(long time) {
		releaseTime.add(time);
	}

	public void addWIP(long time, int iwip) {
		if (wipChangeTime.size() == 0) {
			wipChangeTime.add((long) 0);
			wip.add(0);
			wipChangeTime.add(time);
			wip.add(iwip);
			return;
		}

		if (wipChangeTime.get(wipChangeTime.size() - 1) == time) {
			wipChangeTime.remove(wipChangeTime.size() - 1);
			wip.remove(wip.size() - 1);
		}
		wipChangeTime.add(time);
		wip.add(iwip);
	}

	public void stat() {

		int index = 0;
		int beginIndex = 0;
		for (int i = interval; i < wipChangeTime.get(wipChangeTime.size() - 1); i = i
				+ interval) {
			long sum = 0;
			while (wipChangeTime.get(index) < i) {
				sum += wip.get(index)
						* (wipChangeTime.get(index + 1) - wipChangeTime
								.get(index));
				index++;
			}
			if (beginIndex != 0)
				sum += (wipChangeTime.get(beginIndex) - i + interval)
						* wip.get(beginIndex - 1);
			sum -= (wipChangeTime.get(index) - i) * wip.get(index - 1);
			wipbyDay.add(1.0 * sum / interval);
			beginIndex = index;
		}

		long sum = 0;
		for (int i = 1; i < wip.size(); i++) {
			if (wip.get(i) > maxWip) {
				maxWip = wip.get(i);
			}
			sum += wip.get(i - 1)
					* (wipChangeTime.get(i) - wipChangeTime.get(i - 1));
			// System.out.println(wip.get(i - 1)+","+wipChangeTime.get(i -
			// 1)+","+(wipChangeTime.get(i) - wipChangeTime.get(i - 1)));

		}
		long period=wipChangeTime.get(wipChangeTime.size() - 1);
		if(period==0)
			avgWip=0;
		else
			avgWip = 1.0 * sum /period ;

		index = 0;
		if (receivedTime.size() > 1) {
			for (int i = interval; i < receivedTime
					.get(receivedTime.size() - 1); i = i + interval) {
				int sum1 = 0;
				int count = 0;
				while (receivedTime.get(index) < i) {
					sum1 += cycleTime.get(index);
					count++;
					index++;
					if (index == receivedTime.size())
						break;
				}
				if (index == receivedTime.size())
					break;
				finishedLotsbyDay.add(count);
				cycleTimebyDay.add(1.0 * sum1 / count/3600);

			}

			sum = 0;
			for (int i = 1; i < cycleTime.size(); i++) {
				if (cycleTime.get(i) > maxCycleTime) {
					maxCycleTime = cycleTime.get(i);
				}
				if (cycleTime.get(i) < minCycleTime) {
					minCycleTime = cycleTime.get(i);
				}

				sum += cycleTime.get(i);

			}
			if(cycleTime.size()==0)
				avgCycleTime=0;
			else
				avgCycleTime = 1.0 * sum / cycleTime.size()/3600;
			minCycleTime=1.0*minCycleTime/3600;
			maxCycleTime=1.0*maxCycleTime/3600;
			finishedLotNum = cycleTime.size();
			period=(receivedTime.get(receivedTime.size() - 1)-receivedTime.get(0));
			if(period==0)
				productivity=0;
			else
				productivity = 1.0 * finishedLotNum
					/ period * interval;

		}
		index = 0;
		if (releaseTime.size() > 1) {
			for (int i = interval; i < releaseTime.get(releaseTime.size() - 1); i = i
					+ interval) {
				int count = 0;
				while (releaseTime.get(index) < i) {
					count++;
					index++;
					if (index == releaseTime.size())
						break;
				}
				if (index == releaseTime.size())
					break;
				releaseLotsbyDay.add(count);

			}
			releasedLotNum = releaseTime.size();
			period=releaseTime.get(releasedLotNum - 1)-releaseTime.get(0);
			if(period==0)
				releaseRatio=0;
			else
				releaseRatio = 1.0 * releasedLotNum
					/ period * interval;
		}

		wip.clear();
		//wip = null;
		wipChangeTime.clear();
		//wipChangeTime = null;
		cycleTime.clear();
		//cycleTime = null;
		receivedTime.clear();
		//receivedTime = null;
		releaseTime.clear();
		//releaseTime = null;

	}
	public double getAvgWip() {
		return avgWip;
	}
	public void setAvgWip(double avgWip) {
		this.avgWip = avgWip;
	}


}
