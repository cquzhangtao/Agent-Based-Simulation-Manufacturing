package manu.scheduling.training.data;

import java.util.HashMap;
import java.util.List;

import manu.agent.AgentBasedModel;
import manu.agent.component.Job;
import manu.agent.component.Release;
import manu.agent.component.ToolGroup;
import manu.model.component.JobInfo;
import manu.model.component.Product;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolInfo;
import manu.model.component.state.ToolState;

public class GlobalDataCompute {
	
	private AgentBasedModel agentModel;
	
	
	public GlobalDataCompute(AgentBasedModel agentModel){
		this.agentModel=agentModel;
	}
	


	private List<Job> getJobAgents() {
		// TODO Auto-generated method stub
		return agentModel.getJobAgents();
	}
	private Release getReleaseAgent() {
		// TODO Auto-generated method stub
		return agentModel.getReleaseAgent();
	}
	private List<ToolGroup> getToolGroupAgents() {
		// TODO Auto-generated method stub
		return agentModel.getToolGroupAgents();
	}
	
	public double getCurrentWip() {
		return getJobAgents().size();
	}

	public double getCurrentWip(int index) {
		return getReleaseAgent().getWipDetail()[index];
	}

	public double[] getCurrentWipD() {
		double[] d=new double[getReleaseAgent().getWipDetail().length];
		for(int i=0;i<getReleaseAgent().getWipDetail().length;i++){
			d[i]=getReleaseAgent().getWipDetail()[i];
		}
		return d;
	}

	public int[] getJobNum(int jobTypeIndex, int curStepIndex) {
		int sum[] = new int[3];
		for (Job job : getJobAgents()) {
			JobInfo jobinfo = job.getJobInfo();
			if (jobinfo.getProductIndex() == jobTypeIndex) {
				if (jobinfo.getCurrentProcessIndex() < curStepIndex)
					sum[0]++;
				else if (jobinfo.getCurrentProcessIndex() == curStepIndex)
					sum[1]++;
				else
					sum[2]++;

			}
		}

		return sum;
	}

	public double[] getBufferSizeD() {
		double[] size = new double[getToolGroupAgents().size() ];
		int index = 0;
		for (ToolGroup tg : getToolGroupAgents()) {
			//if (!tg.getAgentName().equals(toolGroupName)) {
				size[index] = tg.getBufferInfo().size();
				index++;
			//}
		}

		return size;
	}

	public double[] getBufferTimeD() {
		double[] size = new double[getToolGroupAgents().size() ];
		int index = 0;
		for (ToolGroup tg : getToolGroupAgents()) {
			//if (!tg.getAgentName().equals(toolGroupName)) {
				size[index] = tg.getBufferInfo().getBufferTime();
				index++;
			//}
		}
		return size;
	}

	public double[] getTotalWaitingTimeD(long currentTime) {
		double[] size = new double[getToolGroupAgents().size()];
		int index = 0;
		for (ToolGroup tg : getToolGroupAgents()) {
			size[index] = tg.getBufferInfo().getTotalWaitingTime(currentTime);
			index++;
		}

		return size;
	}

	public double[] getTotalWaitingTimebyP(long currentTime) {
		double[] time = new double[getReleaseAgent().getProducts().length];
		for (ToolGroup tg : getToolGroupAgents()) {
			for (JobInfo job : tg.getBufferInfo().getJobsinBuffer()) {
				time[job.getProductIndex()] += currentTime
						- job.getStateStartTime();
			}
		}
		return time;
	}
	
	public double getUnavailableToolRatio(){
		double sum=0;
		for(ToolGroup tg:getToolGroupAgents()){
			int sum1=0;
			for(ToolInfo t:tg.getToolGroupInfo().getToolInfos()){
				if(t.state==ToolState.BREAKDOWN||t.state==ToolState.MAINTENANCE)
				sum1++;
			}
			sum+=1.0*sum1/tg.getToolGroupInfo().getToolNuminGroup();
		}
		return sum;
	}
	public double[] getUnavailableToolRatioD(){
		double [] ratio=new double[getToolGroupAgents().size()];
		int index=0;
		for(ToolGroup tg:getToolGroupAgents()){
			int sum1=0;
			for(ToolInfo t:tg.getToolGroupInfo().getToolInfos()){
				if(t.state==ToolState.BREAKDOWN||t.state==ToolState.MAINTENANCE)
				sum1++;
			}
			ratio[index]=1.0*sum1/tg.getToolGroupInfo().getToolNuminGroup();
			index++;
		}
		return ratio;
	}
	public double[] getUnavailableToolRatiobyP(){
		Product[] products=getReleaseAgent().getProducts();
		double[] ratioP=new double[products.length];
		HashMap<String,Double> ratio=new HashMap<String,Double>();
		for(ToolGroup tg:getToolGroupAgents()){
			int sum1=0;
			for(ToolInfo t:tg.getToolGroupInfo().getToolInfos()){
				if(t.state==ToolState.BREAKDOWN||t.state==ToolState.MAINTENANCE)
				sum1++;
			}
			ratio.put(tg.getAgentName(), 1.0*sum1/tg.getToolGroupInfo().getToolNuminGroup());
		}
		for(int i=0;i<products.length;i++){
			for(int j=0;j<products[i].getProcessFlow().size();j++){
				double sum=0;
				for(ToolGroupInfo tg:products[i].getProcessFlow().get(j).getToolGroups()){
					sum+=ratio.get(tg.getToolGroupName());
				}
				ratioP[i]+=sum/products[i].getProcessFlow().get(j).getToolGroups().size();
			}
		}
		return ratioP;
	}
	public int[] getToolState(String name){
		int [] a=new int[2];
		for(ToolGroup tg:getToolGroupAgents()){
			if(tg.getToolGroupInfo().getToolGroupName().equals(name)){
				for(ToolInfo t:tg.getToolGroupInfo().getToolInfos()){
					if(t.state==ToolState.BREAKDOWN||t.state==ToolState.MAINTENANCE)
						a[0]++;
				}
				a[1]=tg.getToolGroupInfo().getToolNuminGroup();
				return a;
			}
		}
		return null;
	}
	
	public void getGlobalData(GlobalData globalData,long currentTime){
		
		globalData.setWip(this.getCurrentWip());
		globalData.setWipD(this.getCurrentWipD());
		globalData.setBufferSizeD(this.getBufferSizeD());
		globalData.setBufferTimeD(this.getBufferTimeD());
		globalData.setTotWaiTimD(this.getTotalWaitingTimeD(currentTime));
		globalData.setTotWaiTimbyP(this.getTotalWaitingTimebyP(currentTime));
		globalData.setUnaTooRat(this.getUnavailableToolRatio());
		globalData.setUnaTooRatbyP(getUnavailableToolRatiobyP());
		globalData.setUnaTooRatD(getUnavailableToolRatioD());
		
		
	}

}
