package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ToolData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String toolName = "";
	public String toolGroupName = "";
	public double freeTime;
	public double blockTime;
	public double processTime;
	public double breakdownTime;
	public double maintenanceTime;
	public double setupTime;
	public double totalSetupTime;
	public ArrayList<Double> beginFreeTime = new ArrayList<Double>();
	public ArrayList<Double> ifreeTime = new ArrayList<Double>();
	//public ArrayList<String> jobNames=new ArrayList<String>();
	public ArrayList<Double> utiliztionbyDay = new ArrayList<Double>();
	public ArrayList<StartEnd> setupTimeDataset = new ArrayList<StartEnd>();
	//public ArrayList<Double> isetupTime = new ArrayList<Double>();
	public ArrayList<StartEnd> blockTimeDataset = new ArrayList<StartEnd>();
	//public ArrayList<Double> iblockTime = new ArrayList<Double>();
	public ArrayList<Double> beginInterruptTime = new ArrayList<Double>();
	public ArrayList<Double> interruptTime = new ArrayList<Double>();
	public ArrayList<StartEnd> processTimeDataset = new ArrayList<StartEnd>();
	//public ArrayList<Double> iprocessTime = new ArrayList<Double>();
	//ToolData toolGroupData;
	public ArrayList<StartEnd> interruptTimeDataset = new ArrayList<StartEnd>();
	public int interruptNum;

	public int interval = 7 * 24 * 3600;
	
	private double freeTime_s;
	private double blockTime_s;
	private double processTime_s;
	private double breakdownTime_s;
	private double maintenanceTime_s;
	private double setupTime_s;
	int beginFreeTimeSize;
	int ifreeTimeSize;
	int beginInterruptTimeSize;
	int interruptTimeSize;
	
	int processTimeDatasetSize;
	int blockTimeDatasetSize;
	int setupTimeDataTimeSize;
	int interruptTimeDatasetSize;
	
	public void save(){
		freeTime_s=freeTime;
		blockTime_s=blockTime;
		processTime_s=processTime;
		breakdownTime_s=breakdownTime;
		maintenanceTime_s=maintenanceTime;
		setupTime_s=setupTime;

		beginFreeTimeSize=beginFreeTime.size();
		ifreeTimeSize=ifreeTime.size();
		beginInterruptTimeSize=beginInterruptTime.size();
		interruptTimeSize=interruptTime.size();
		
		 blockTimeDatasetSize= blockTimeDataset.size();
		 setupTimeDataTimeSize=setupTimeDataset.size();
		 processTimeDatasetSize=processTimeDataset.size();		
		 interruptTimeDatasetSize=interruptTimeDataset.size();
	}
	public void recover(){
		freeTime=freeTime_s;
		blockTime=blockTime_s;
		processTime=processTime_s;
		breakdownTime=breakdownTime_s;
		maintenanceTime=maintenanceTime_s;
		setupTime=setupTime_s;
		while(beginFreeTime.size()>beginFreeTimeSize)
			beginFreeTime.remove(beginFreeTimeSize);
		while(ifreeTime.size()>ifreeTimeSize)
			ifreeTime.remove(ifreeTimeSize);
		while(beginInterruptTime.size()>beginInterruptTimeSize)
			beginInterruptTime.remove(beginInterruptTimeSize);
		while(interruptTime.size()>interruptTimeSize)
			interruptTime.remove(interruptTimeSize);
		while(blockTimeDataset.size()>blockTimeDatasetSize)
			blockTimeDataset.remove(blockTimeDatasetSize);
		while(setupTimeDataset.size()>setupTimeDataTimeSize)
			setupTimeDataset.remove(setupTimeDataTimeSize);
		while(processTimeDataset.size()>processTimeDatasetSize)
			processTimeDataset.remove(processTimeDatasetSize);
		while(interruptTimeDataset.size()>interruptTimeDatasetSize)
			interruptTimeDataset.remove(interruptTimeDatasetSize);
	}

	public ToolData(String toolName, String toolGroupName) {
		this.toolName = toolName;
		this.toolGroupName = toolGroupName;
	}

	public ToolData() {
		toolName = "";
		toolGroupName = "";
	}
	public void addSetupTime(double setupBeginTime, double d){
		setupTimeDataset.add(new StartEnd(setupBeginTime,d));
	}
	public void addBlockTime(double blockBeginTime, double d){
		blockTimeDataset.add(new StartEnd(blockBeginTime,d));
	}

	public void addFreeTime(double freeBeginTime, double d) {
		beginFreeTime.add(freeBeginTime);
		ifreeTime.add(d);
		//jobNames.add(job);
//		if(toolGroupData!=null){
//			toolGroupData.addProcessTime(processBeginTime, d);
//		}

	}
	public void addProcessTime(double processBeginTime, double d,String job) {
		processTimeDataset.add(new StartEnd(processBeginTime,d,job));


	}

	public void addInterruptTime(double breakdownBeginTime, double d) {
		beginInterruptTime.add(breakdownBeginTime);
		interruptTime.add(d-breakdownBeginTime);
		interruptTimeDataset.add(new StartEnd(breakdownBeginTime,d));
//		if(toolGroupData!=null){
//			toolGroupData.addInterruptTime(breakdownBeginTime, d);
//		}

	}

	public void reset() {
		freeTime = 0;
		setupTime = 0;
		processTime = 0;
		blockTime = 0;
		breakdownTime = 0;
		maintenanceTime = 0;
		totalSetupTime=0;
		interruptNum=0;
		beginFreeTime.clear();
		ifreeTime.clear();
		utiliztionbyDay.clear();
		beginInterruptTime.clear();
		interruptTime.clear();
		this.blockTimeDataset.clear();
		this.setupTimeDataset.clear();
		this.processTimeDataset.clear();
		this.interruptTimeDataset.clear();
	}

	public String data2String() {
		double totalTime = freeTime + setupTime + processTime + blockTime
				+ breakdownTime + maintenanceTime;
		// System.out.println(totalTime/3600.0/24);
		return String.format("%s-%s-%f-%f-%f-%f-%f-%f", toolName,
				toolGroupName, freeTime / (1.0 * totalTime), setupTime
						/ (1.0 * totalTime), processTime / (1.0 * totalTime),
				blockTime / (1.0 * totalTime), breakdownTime
						/ (1.0 * totalTime), maintenanceTime
						/ (1.0 * totalTime));
	}

	public void stat() {
		totalSetupTime = 1.0*setupTime/3600;
		double totalTime = freeTime + setupTime + processTime + blockTime
				+ breakdownTime + maintenanceTime;
		if(totalTime>0){
		freeTime /= (1.0 * totalTime);
		setupTime /= (1.0 * totalTime);
		processTime /= (1.0 * totalTime);
		blockTime /= (1.0 * totalTime);
		breakdownTime /= (1.0 * totalTime);
		maintenanceTime /= (1.0 * totalTime);
		}
		
	
		if(beginFreeTime.size()==0)
			return;
		interruptNum=beginInterruptTime.size();
		int index = 0;
		for (int i = interval; i < beginFreeTime
				.get(beginFreeTime.size() - 1)
				+ ifreeTime.get(beginFreeTime.size() - 1); i += interval) {
			long sum = 0;
			if (index > 0) {
				if (beginFreeTime.get(index - 1)
						+ ifreeTime.get(index - 1) > i - interval) {
					sum += beginFreeTime.get(index - 1)
							+ ifreeTime.get(index - 1) - i+interval;
				}
			}
			while (beginFreeTime.get(index) < i) {
				sum += ifreeTime.get(index);
				index++;
				if (index == beginFreeTime.size())
					break;
			}
			if (index == beginFreeTime.size()) {
				if (beginFreeTime.get(index - 1)
						+ ifreeTime.get(index - 1) > i) {
					sum -= (beginFreeTime.get(index - 1)
							+ ifreeTime.get(index - 1) - i);
					utiliztionbyDay.add(1-1.0 * sum / interval);
				}
				break;
			}
			if (index > 0) {
				if (beginFreeTime.get(index - 1)
						+ ifreeTime.get(index - 1) > i) {
					sum -= (beginFreeTime.get(index - 1)
							+ ifreeTime.get(index - 1) - i);
				}
			}
			utiliztionbyDay.add(1-1.0 * sum / interval);
			

		}
		beginFreeTime.clear();
		ifreeTime.clear();
		//beginProcessTime=null;
		//iprocessTime=null;


	}

	public double totalTime() {
		return freeTime + setupTime + processTime + blockTime + breakdownTime
				+ maintenanceTime;
	}

	public void updateData(String s) {

		String[] temp = s.split("-");
		if (temp.length < 8)
			return;
		toolName = temp[0];
		toolGroupName = temp[1];
		freeTime = Double.valueOf(temp[2]);
		setupTime = Double.valueOf(temp[3]);
		processTime = Double.valueOf(temp[4]);
		blockTime = Double.valueOf(temp[5]);
		breakdownTime = Double.valueOf(temp[6]);
		maintenanceTime = Double.valueOf(temp[7]);
	}
	public double getBreakdownTime() {
		return breakdownTime;
	}
	public void setBreakdownTime(double breakdownTime) {
		this.breakdownTime = breakdownTime;
	}
	public double getProcessTime() {
		return processTime;
	}
	public void setProcessTime(double processTime) {
		this.processTime = processTime;
	}
	public double getUtilization(){
		if(totalTime()==0){
			return 0;
		}
		return processTime/totalTime();
	}


}
