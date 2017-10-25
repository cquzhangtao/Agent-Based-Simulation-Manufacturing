package manu.model.component;

import manu.utli.Entity;

public class ActualProcess extends Entity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long processingTime;
	private long transTime;
	private String toolGroup;
	
	public ActualProcess(String name){
		super(name);
	}
	
	public ActualProcess clone(){
		ActualProcess p=new ActualProcess(this.getName());
		super.clone(p);
		p.processingTime=processingTime;
		p.transTime=transTime;
		p.toolGroup=toolGroup;	
		return p;
		
	}

	public long getProcessingTime() {
		return processingTime;
	}

	public long getTransTime() {
		return transTime;
	}

	public String getToolGroup() {
		return toolGroup;
	}

	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}

	public void setTransTime(long transTime) {
		this.transTime = transTime;
	}

	public void setToolGroup(String toolGroup) {
		this.toolGroup = toolGroup;
	}

}
