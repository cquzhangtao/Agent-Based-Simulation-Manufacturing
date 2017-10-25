package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ToolGroupData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2746333220859078335L;
	public String toolGroupName;
	ArrayList<ToolData> toolDataset=new ArrayList<ToolData>();
	public ToolData toolGroupDataSum=new ToolData();
	public WaitDataset waitDataset;
	public BlockDataset blockDataset;
	public int toolNum;
	
	public ToolGroupData(String name,int period){
		toolGroupName=name;
//		for(int i=0;i<toolNum;i++){
//			toolDataset.add(new ToolData());
//		}
		toolGroupDataSum.toolName=name;
		toolGroupDataSum.toolGroupName=name;

		waitDataset=new WaitDataset(period);
		blockDataset=new BlockDataset(period);
		waitDataset.bufferName=name;
	}
	public void reset(){
		for(int i=0;i<toolDataset.size();i++){
			toolDataset.get(i).reset();
		}
		toolGroupDataSum.reset();
		waitDataset.reset();
		blockDataset.reset();
		
	}
	public void addTool(ToolData data){
		//data.toolGroupData=toolGroupData;
		toolDataset.add(data);
	}
	
	public void addFreeTime(int index,long time){
		toolDataset.get(index).freeTime+=time;
	}
	public void addSetupTime(int index,long time){
		toolDataset.get(index).setupTime+=time;
	}
	public void addProcessTime(int index,long time){
		toolDataset.get(index).processTime+=time;
	}
	public void addBlockTime(int index,long time){
		toolDataset.get(index).blockTime+=time;
	}
	public void addBreakdownTime(int index,long time){
		toolDataset.get(index).breakdownTime+=time;
	}
	public void addMaintenanceTime(int index,long time){
		toolDataset.get(index).maintenanceTime+=time;
	}
	public void stat()
	{
		int minSize=999999999;
		int minIndex=-1;
		int interruptNum=0;
		long totalSetupTime=0;
		toolNum=toolDataset.size();
		for(int i=0;i<toolDataset.size();i++){
			toolDataset.get(i).stat();
			toolGroupDataSum.freeTime+=toolDataset.get(i).freeTime;
			toolGroupDataSum.setupTime+=toolDataset.get(i).setupTime;
			toolGroupDataSum.processTime+=toolDataset.get(i).processTime;
			toolGroupDataSum.blockTime+=toolDataset.get(i).blockTime;
			toolGroupDataSum.breakdownTime+=toolDataset.get(i).breakdownTime;
			toolGroupDataSum.maintenanceTime+=toolDataset.get(i).maintenanceTime;
			if(toolDataset.get(i).utiliztionbyDay.size()<minSize){
				minSize=toolDataset.get(i).utiliztionbyDay.size();
				minIndex=i;
			}
			interruptNum+=toolDataset.get(i).interruptNum;
			totalSetupTime+=toolDataset.get(i).totalSetupTime;
			
		}
		toolGroupDataSum.stat();
		toolGroupDataSum.interruptNum=interruptNum;
		toolGroupDataSum.totalSetupTime=totalSetupTime;
		if(minIndex!=-1){
			for(int i=0;i<toolDataset.get(minIndex).utiliztionbyDay.size();i++){
				
				double sum=0;
				for(int j=0;j<toolDataset.size();j++){
					sum+=(toolDataset.get(j).utiliztionbyDay.get(i));
				}
				toolGroupDataSum.utiliztionbyDay.add(sum/toolDataset.size());
			}
		}
		waitDataset.stat();
		blockDataset.stat();
		
		
	}
	public int size() {
		// TODO Auto-generated method stub
		return toolDataset.size();
	}
	public ToolData get(int j) {
		// TODO Auto-generated method stub
		return toolDataset.get(j);
	}
	public ToolData get(String toolName) {
		// TODO Auto-generated method stub
		for(int i=0;i<toolDataset.size();i++){
			if(toolDataset.get(i).toolName.equals(toolName))
			{
				return toolDataset.get(i);
			}
		}
		return null;
	}
	
	public void save(){
		for(int i=0;i<toolDataset.size();i++){
			toolDataset.get(i).save();
		}
		waitDataset.save();
		blockDataset.save();
	}
	public void recover(){
		for(int i=0;i<toolDataset.size();i++){
			toolDataset.get(i).recover();
		}
		waitDataset.recover();
		blockDataset.recover();
	}
	public ArrayList<ToolData>  getToolData() {
		// TODO Auto-generated method stub
		return toolDataset;
	}

}
