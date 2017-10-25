package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ToolGroupDataset implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762331968679254274L;
	public ArrayList<ToolGroupData> toolGroupDataset=new ArrayList<ToolGroupData>();
	public ToolData allToolData=new ToolData("All","All");
	public WaitDataset allWaitDataset;
	public BlockDataset allBlockDataset;
	public int toolNum=0;
	public void add(ToolGroupData data,int priod)
	{
		toolGroupDataset.add(data);

		allWaitDataset=new WaitDataset(priod);
		allBlockDataset=new BlockDataset(priod);
		allWaitDataset.bufferName="All";
		allBlockDataset.bufferName="All";
	}
	public void reset(){
		allToolData.reset();
		allWaitDataset.reset();
		allBlockDataset.reset();
		toolNum=0;
		for(int i=0;i<toolGroupDataset.size();i++){
			toolGroupDataset.get(i).reset();}
	}
	
	public void stat(){
		int minIndex=-1;
		long minSize=999999999;
		int interruptNum=0;
		long totalSetupTime=0;
		for(int i=0;i<toolGroupDataset.size();i++){
			toolGroupDataset.get(i).stat();
			toolNum+=toolGroupDataset.get(i).toolNum;
			allToolData.freeTime+=toolGroupDataset.get(i).toolGroupDataSum.freeTime;
			allToolData.setupTime+=toolGroupDataset.get(i).toolGroupDataSum.setupTime;
			allToolData.blockTime+=toolGroupDataset.get(i).toolGroupDataSum.blockTime;
			allToolData.processTime+=toolGroupDataset.get(i).toolGroupDataSum.processTime;
			allToolData.breakdownTime+=toolGroupDataset.get(i).toolGroupDataSum.breakdownTime;
			allToolData.maintenanceTime+=toolGroupDataset.get(i).toolGroupDataSum.maintenanceTime;
			
			if(toolGroupDataset.get(i).toolGroupDataSum.utiliztionbyDay.size()<minSize){
				minSize=toolGroupDataset.get(i).toolGroupDataSum.utiliztionbyDay.size();
				minIndex=i;
			}
			interruptNum+=toolGroupDataset.get(i).toolGroupDataSum.interruptNum;
			totalSetupTime+=toolGroupDataset.get(i).toolGroupDataSum.totalSetupTime;
			
		}
		allToolData.stat();
		//muse below here
		allToolData.interruptNum=interruptNum;
		allToolData.totalSetupTime=totalSetupTime;
		
		if(minIndex!=-1){
			for(int i=0;i<toolGroupDataset.get(minIndex).toolGroupDataSum.utiliztionbyDay.size();i++){
				
				double sum=0;
				for(int j=0;j<toolGroupDataset.size();j++){
					sum+=(toolGroupDataset.get(j).toolGroupDataSum.utiliztionbyDay.get(i));
				}
				allToolData.utiliztionbyDay.add(sum/toolGroupDataset.size());
			}
		}
		double sum=0;
		double maxWaitTime=0;
		minSize=999999999;
		double sum1=0;
		double maxBlockTime=0;
		int minSize1=999999999;
		for(int i=0;i<toolGroupDataset.size();i++){
			//toolGroupDataset.get(i).waitDataset.stat();
			sum+=toolGroupDataset.get(i).waitDataset.avgWaitTime;
			
			if(toolGroupDataset.get(i).waitDataset.maxWaitTime>maxWaitTime){
				maxWaitTime=toolGroupDataset.get(i).waitDataset.maxWaitTime;
			}
			if(toolGroupDataset.get(i).waitDataset.size()<minSize){
				minSize=toolGroupDataset.get(i).waitDataset.size();
			}
			
			sum1+=toolGroupDataset.get(i).blockDataset.avgBlockTime;
			
			if(toolGroupDataset.get(i).blockDataset.maxBlockTime>maxBlockTime){
				maxBlockTime=toolGroupDataset.get(i).blockDataset.maxBlockTime;
			}
			if(toolGroupDataset.get(i).blockDataset.size()<minSize1){
				minSize1=toolGroupDataset.get(i).blockDataset.size();
			}
		}
		allWaitDataset.avgWaitTime=sum/toolGroupDataset.size();
		allWaitDataset.maxWaitTime=maxWaitTime;
		allBlockDataset.avgBlockTime=sum1/toolGroupDataset.size();
		allBlockDataset.maxBlockTime=maxBlockTime;

		for(int i=0;i<minSize;i++){
			sum=0;
			for(int j=0;j<toolGroupDataset.size();j++){
				sum+=toolGroupDataset.get(j).waitDataset.waitDatasetbyDay.get(i);
			}
			allWaitDataset.waitDatasetbyDay.add(1.0*sum/toolGroupDataset.size());
		}
		for(int i=0;i<minSize1;i++){
			sum1=0;
			for(int j=0;j<toolGroupDataset.size();j++){
				sum1+=toolGroupDataset.get(j).blockDataset.blockDatasetbyDay.get(i);
			}
			allBlockDataset.blockDatasetbyDay.add(1.0*sum1/toolGroupDataset.size());
		}
		
	}

	public int size() {
		// TODO Auto-generated method stub
		return toolGroupDataset.size();
	}

	public ToolGroupData get(int i) {
		// TODO Auto-generated method stub
		return toolGroupDataset.get(i);
	}
	public ToolData get(String toolGroupName) {
		// TODO Auto-generated method stub
		for(int i=0;i<toolGroupDataset.size();i++){
			if(toolGroupDataset.get(i).toolGroupName.equals(toolGroupName))
			{
				return toolGroupDataset.get(i).toolGroupDataSum;
			}
		}
		return null;
	}
	public ToolGroupData getToolGroupData(String toolGroupName) {
		// TODO Auto-generated method stub
		for(int i=0;i<toolGroupDataset.size();i++){
			if(toolGroupDataset.get(i).toolGroupName.equals(toolGroupName))
			{
				return toolGroupDataset.get(i);
			}
		}
		return null;
	}
	
	public void save(){
		for(ToolGroupData td:toolGroupDataset)
			td.save();
	}
	public void recover(){
		for(ToolGroupData td:toolGroupDataset)
			td.recover();
	}
	public ToolData getAllToolData() {
		return allToolData;
	}
	public void setAllToolData(ToolData allToolData) {
		this.allToolData = allToolData;
	}

}
