package manu.model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import manu.utli.Entity;
import manu.utli.StatValue;

public class JobInfo extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -468329519836476603L;
	private List<ActualProcess> actualProcessFlow = new ArrayList<ActualProcess>();
	private ProcessingTimePool processingTimePool;
	private TransportTimePool transportTimePool;
	private Product product;
	
	private int priority;
	private long dueDate;
	private int currentProcessIndex;
	private Long releaseTime;
	private boolean blocked = false;	
	private String currentTool;
	private String preTool;
	private String currentToolIndex;
	private int state;
	private long stateStartTime;
	

	public JobInfo(String name,Product product) {
		super(name);
		priority = 1;
		currentProcessIndex = 0;
		this.product = product;
		this.processingTimePool=product.getProcessingTimePool();
		this.transportTimePool=product.getTransportTimePool();
		for (Process process : product.getProcessFlow()) {
			actualProcessFlow.add(new ActualProcess(process.getName()));
		}
	}
	
	public String getPath(){
		String path="Flow:";
		for(Process process:product.getProcessFlow()){
			path+=process.getSkill()+",";
		}
		path+="Tools:";
		for(ActualProcess actualProcess:actualProcessFlow){
			path+=actualProcess.getToolGroup()+",";
		}
		return path;
	}
	
	public String getProductName(){
		return product.getName();
	}



	public StatValue getRemainTime() {
		return getRemainTime(currentProcessIndex);
	}
	public StatValue getTotalTime() {
		return getRemainTime(0);
	}

	public int getRemainStep() {
		return product.getProcessFlow().size() - currentProcessIndex - 1;
	}

	public StatValue getRemainTime(int curIndex) {
		StatValue totalTime = new StatValue();

		Map<String,Long> totalTimes = new HashMap<String,Long>();
		for (ToolGroupInfo curTool : product.getProcessFlow().get(curIndex).getToolGroups()) {
			long procTime=processingTimePool.get(curTool.getToolGroupName()+product.getProcessFlow().get(curIndex).getName());
			totalTimes.put(curTool.getToolGroupName(), procTime);
		}

		for (int i=curIndex+1;i<product.getProcessFlow().size();i++) {
			Process process = product.getProcessFlow().get(i);
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

	public long getCurrentTransTime() {
		if (currentProcessIndex == 0)
			return 0;
		return transportTimePool.get(actualProcessFlow.get(currentProcessIndex - 1)
				.getToolGroup()
				+ actualProcessFlow.get(currentProcessIndex).getToolGroup());
	}

	public long getCurrentProcessTime() {
		return processingTimePool.get(actualProcessFlow.get(currentProcessIndex)
				.getToolGroup()
				+ actualProcessFlow.get(currentProcessIndex).getName());
	}

	public int getProcessNum() {
		return actualProcessFlow.size();
	}

	public boolean isFirstProcess() {
		return (0 == currentProcessIndex);

	}

	public boolean isLastProcess() {
		return (actualProcessFlow.size() == currentProcessIndex + 1);

	}

	public boolean isFinished() {
		return (currentProcessIndex == getProcessNum() - 1);

	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
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

	public int getPriority() {
		return priority;
	}

	public long getDueDate() {
		return dueDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}



	public Long getReleaseTime() {
		return releaseTime;
	}



	public void setReleaseTime(Long releaseTime) {
		this.releaseTime = releaseTime;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public String getCurrentTool() {
		return currentTool;
	}

	public String getPreTool() {
		return preTool;
	}

	public String getCurrentToolIndex() {
		return currentToolIndex;
	}

	public void setCurrentTool(String currentTool) {
		this.currentTool = currentTool;
	}

	public void setPreTool(String preTool) {
		this.preTool = preTool;
	}

	public void setCurrentToolIndex(String currentToolIndex) {
		this.currentToolIndex = currentToolIndex;
	}

	public String getCurrentToolGroupName() {
		return actualProcessFlow.get(currentProcessIndex).getToolGroup();
	}
	
	public void setCurrentToolGroupName(String name) {
		actualProcessFlow.get(currentProcessIndex).setToolGroup(name);
	}
	
	public String getPreToolGroupName() {
		
		if(currentProcessIndex==0)
			return null;
		
		return actualProcessFlow.get(currentProcessIndex-1).getToolGroup();
	}
	public String getPreProcessName(){
		if(currentProcessIndex==0)
			return null;
		return product.getProcessFlow().get(currentProcessIndex-1).getName();
	}

	public void goNextProcess() {
		currentProcessIndex++;
		
	}

	public int getCurrentProcessIndex() {
		return currentProcessIndex;
	}

	public void setCurrentProcessIndex(int currentProcessIndex) {
		this.currentProcessIndex = currentProcessIndex;
	}

	public int getProductIndex() {
		// TODO Auto-generated method stub
		return product.getIndex();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getStateStartTime() {
		return stateStartTime;
	}

	public void setStateStartTime(long stateStartTime) {
		this.stateStartTime = stateStartTime;
	}
	
	@Override
	public JobInfo clone(){
		JobInfo jobinfo=new JobInfo(getName(),product);
		jobinfo.currentProcessIndex=currentProcessIndex;
		jobinfo.releaseTime=releaseTime;
		jobinfo.state=state;
		jobinfo.currentTool=currentTool;
		jobinfo.blocked=blocked;
		jobinfo.currentToolIndex=currentToolIndex;
		jobinfo.preTool=preTool;
		
		
		jobinfo.processingTimePool=processingTimePool;
		jobinfo.transportTimePool=transportTimePool;
		jobinfo.product=product;
		
		jobinfo.priority=priority;
		jobinfo.dueDate=dueDate;
		jobinfo.stateStartTime=stateStartTime;
		jobinfo.actualProcessFlow = new ArrayList<ActualProcess>();
		
		for(ActualProcess process:actualProcessFlow){
			jobinfo.actualProcessFlow.add((ActualProcess) process.clone());
		}
		
		return jobinfo;
	}

	public String getCurrentProcessName() {
		// TODO Auto-generated method stub
		return actualProcessFlow.get(currentProcessIndex).getName();
	}

	public Process getCurrentProcess() {
		// TODO Auto-generated method stub
		return product.getProcessFlow().get(currentProcessIndex);
	}

}
