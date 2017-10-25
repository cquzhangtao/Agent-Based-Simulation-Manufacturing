package manu.model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import manu.scheduling.DecisionMaker;
import manu.scheduling.training.data.ReleaseTrainingData;
import manu.utli.Entity;
import manu.utli.StatValue;
import simulation.distribution.RandomVariable;

public class Product extends Entity implements Comparable<Product>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Process> processFlow = new ArrayList<Process>();
	private ProcessingTimePool processingTimePool;
	private TransportTimePool transportTimePool;
	private int index;
	private int priority;
	private long dueDate;
	private double releaseProb;
	private int sortType = 0;
	private double productCap = 0;
	private double releaseIntervalTime;
	private int releaseConstantWIP;
	private int maxWIP;
	private RandomVariable releaseIntervalTimeR;
	private DecisionMaker<ReleaseTrainingData> decisionMaker;

	
	public ArrayList<Process> getProcessFlow() {
		return processFlow;
	}
	
	public StatValue getRawProcessingTime() {
		StatValue totalTime = new StatValue();

		Map<String,Long> totalTimes = new HashMap<String,Long>();
		int curIndex=0;
		for (ToolGroupInfo curTool : this.getProcessFlow().get(curIndex).getToolGroups()) {
			long procTime=processingTimePool.get(curTool.getToolGroupName()+this.getProcessFlow().get(curIndex).getName());
			totalTimes.put(curTool.getToolGroupName(), procTime);
		}

		for (int i=curIndex+1;i<this.getProcessFlow().size();i++) {
			Process process = this.getProcessFlow().get(i);
			Map<String,Long> totalTimeT = new HashMap<String,Long>();
			for (String preTool : totalTimes.keySet()) {
				for (ToolGroupInfo curTool : process.getToolGroups()) {
					long transTime=transportTimePool.get(preTool+curTool.getToolGroupName());
					long procTime=processingTimePool.get(curTool.getToolGroupName()+process.getName());
					totalTimeT.put(curTool.getToolGroupName(), totalTimes.get(preTool)+transTime+procTime);
				}
			}
			totalTimes=totalTimeT;
		}
		
		long sum=0;
		for(Long time:totalTimes.values()){
			sum+=time;
			if(time>totalTime.getMax()){
				totalTime.setMax(time);
			}
			
			if(time<totalTime.getMin()){
				totalTime.setMin(time);
			}
		}
		totalTime.setAvg(sum/totalTimes.size());

		return totalTime;
	}
	
public void addProcess(String name,String skill){
	
	processFlow.add(new Process(name,skill));
}
	public void setProcessFlow(ArrayList<Process> processFlow) {
		this.processFlow = processFlow;
	}
	
	public void reset() {
		if (releaseIntervalTimeR != null)
			releaseIntervalTimeR.reset();
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public int getPriority() {
		return priority;
	}


	public long getDueDate() {
		return dueDate;
	}


	public double getReleaseProb() {
		return releaseProb;
	}


	public int getSortType() {
		return sortType;
	}


	public double getProductCap() {
		return productCap;
	}


	public double getReleaseIntervalTime() {
		return releaseIntervalTime;
	}


	public int getReleaseConstantWIP() {
		return releaseConstantWIP;
	}


	public RandomVariable getReleaseIntervalTimeR() {
		return releaseIntervalTimeR;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}


	public void setReleaseProb(double releaseProb) {
		this.releaseProb = releaseProb;
	}


	public void setSortType(int sortType) {
		this.sortType = sortType;
	}


	public void setProductCap(double productCap) {
		this.productCap = productCap;
	}


	public void setReleaseIntervalTime(double releaseIntervalTime) {
		this.releaseIntervalTime = releaseIntervalTime;
	}


	public void setReleaseConstantWIP(int releaseConstantWIP) {
		this.releaseConstantWIP = releaseConstantWIP;
	}


	public void setReleaseIntervalTimeR(RandomVariable releaseIntervalTimeR) {
		this.releaseIntervalTimeR = releaseIntervalTimeR;
	}

	@Override
	public int compareTo(Product o) {
		if (sortType == 0) {
			if (dueDate < o.dueDate)
				return -1;
			else if (dueDate == o.dueDate)
				return 0;
			else
				return 1;
		} else {
			if (releaseProb < o.releaseProb)
				return -1;
			else if (releaseProb == o.releaseProb)
				return 0;
			else
				return 1;
		}
	}

	public ProcessingTimePool getProcessingTimePool() {
		return processingTimePool;
	}

	public void setProcessingTimePool(ProcessingTimePool processingTimePool) {
		this.processingTimePool = processingTimePool;
	}

	public TransportTimePool getTransportTimePool() {
		return transportTimePool;
	}

	public void setTransportTimePool(TransportTimePool transportTimePool) {
		this.transportTimePool = transportTimePool;
	}

	public int getMaxWIP() {
		return maxWIP;
	}

	public void setMaxWIP(int maxWIP) {
		this.maxWIP = maxWIP;
	}

	public DecisionMaker<ReleaseTrainingData> getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(DecisionMaker<ReleaseTrainingData> decisionMaker) {
		this.decisionMaker = decisionMaker;
	}




	
	
	
	
}
