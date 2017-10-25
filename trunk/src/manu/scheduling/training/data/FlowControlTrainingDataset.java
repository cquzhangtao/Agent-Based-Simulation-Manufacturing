package manu.scheduling.training.data;

import java.io.Serializable;
import java.util.ArrayList;

import manu.agent.AgentBasedModel;
import manu.agent.component.ToolGroup;
import manu.agent.component.ToolGroupPool;
import manu.model.component.JobInfo;
import manu.model.component.Product;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolInfo;

public class FlowControlTrainingDataset implements Serializable{
	
	private ReleaseTrainingDatasetRaw releaseDataset;
	private RoutingTrainingDatasetRaw routingDataset;
	private SequencingTrainingDatasetRaw sequencingDataset;
	
	public FlowControlTrainingDataset(Product[] products,ArrayList<ToolGroupPool> skills,ArrayList<ToolGroup> toolGroups){
		
		
		releaseDataset=new ReleaseTrainingDatasetRaw();
		routingDataset=new RoutingTrainingDatasetRaw();
		sequencingDataset=new SequencingTrainingDatasetRaw();
		for(Product product:products){
			releaseDataset.put(product, new TrainingDatasetRaw<Product,Integer>());
		}
		for(ToolGroupPool skill:skills){
			routingDataset.put(skill.getAgentName(), new TrainingDatasetRaw<String,ToolGroupInfo>());
		}
		for(ToolGroup toolgroup:toolGroups){
			sequencingDataset.put(toolgroup.getAgentName(), new TrainingDatasetRaw<String,JobInfo>());
		}
	}
	public void addReleaseData(Product product, DataMeta<Integer> data){
		releaseDataset.get(product).add(data);
	}
	public void addRoutingData(String skill,DataMeta<ToolGroupInfo> data){
		routingDataset.get(skill).add(data);
	}
	
	public void addSequencingData(String toolGroup,DataMeta<JobInfo> data){
		sequencingDataset.get(toolGroup).add(data);
	}
	
	public ReleaseTrainingDatasetRaw getReleaseDataset() {
		return releaseDataset;
	}
	public RoutingTrainingDatasetRaw getRoutingDataset() {
		return routingDataset;
	}
	public SequencingTrainingDatasetRaw getSequencingDataset() {
		return sequencingDataset;
	}
	public void setReleaseDataset(ReleaseTrainingDatasetRaw releaseDataset) {
		this.releaseDataset = releaseDataset;
	}
	public void setRoutingDataset(RoutingTrainingDatasetRaw routingDataset) {
		this.routingDataset = routingDataset;
	}
	public void setSequencingDataset(SequencingTrainingDatasetRaw sequencingDataset) {
		this.sequencingDataset = sequencingDataset;
	}
	
	public TrainingDatasetMap<SequencingTrainingData> createSequencingTrainingDataset(){
		TrainingDatasetMap<SequencingTrainingData> trainingDatasetMap=new TrainingDatasetMap<SequencingTrainingData> ();
		
		for(String groupName:sequencingDataset.keySet()){
			TrainingDataset<SequencingTrainingData> traningDataset=new TrainingDataset<SequencingTrainingData>(groupName);
			trainingDatasetMap.putDataset(groupName, traningDataset);
			TrainingDatasetRaw<String, JobInfo> dataset = sequencingDataset.get(groupName);
			for(DataMeta<JobInfo> data:dataset){
				AgentBasedModel agentModel = data.getState();
				//ToolGroupInfo toolGroupInfo=agentModel.getToolGroup(groupName).getToolGroupInfo();
				ToolInfo toolInfo=(ToolInfo) data.getLocation();
				
				for(ActionAndValue<JobInfo> actionAndValue:data.getActionAndValues()){
					JobInfo jobInfo=actionAndValue.getAction();
					SequencingTrainingData trainingData=new SequencingTrainingData();
					traningDataset.add(trainingData);
					//objective
					trainingData.setObjective(actionAndValue.getValue());
					//global
					setGlobalData(trainingData,agentModel,agentModel.getToolGroupAgent(toolInfo.getToolGroupName()).currentTime());
					
					//local
					trainingData.setLocalData(jobInfo, agentModel.getToolGroupAgent(groupName).getBufferInfo(), toolInfo,agentModel, agentModel.getCurrentTime());					
					
				}
			}
		}
		
		return trainingDatasetMap;
	
	}
	public TrainingDataset<SequencingTrainingData> createSequencingTrainingDataset(String groupName){
		//TrainingDatasetMap<SequencingTrainingData> trainingDatasetMap=new TrainingDatasetMap<SequencingTrainingData> ();
		
		//for(String groupName:sequencingDataset.keySet()){
			TrainingDataset<SequencingTrainingData> traningDataset=new TrainingDataset<SequencingTrainingData>(groupName);
			//trainingDatasetMap.putDataset(groupName, traningDataset);
			TrainingDatasetRaw<String, JobInfo> dataset = sequencingDataset.get(groupName);
			for(DataMeta<JobInfo> data:dataset){
				AgentBasedModel agentModel = data.getState();
				//ToolGroupInfo toolGroupInfo=agentModel.getToolGroup(groupName).getToolGroupInfo();
				ToolInfo toolInfo=(ToolInfo) data.getLocation();
				
				for(ActionAndValue<JobInfo> actionAndValue:data.getActionAndValues()){
					JobInfo jobInfo=actionAndValue.getAction();
					SequencingTrainingData trainingData=new SequencingTrainingData();
					traningDataset.add(trainingData);
					//objective
					trainingData.setObjective(actionAndValue.getValue());
					//global
					setGlobalData(trainingData,agentModel,agentModel.getToolGroupAgent(toolInfo.getToolGroupName()).currentTime());
					
					//local
					trainingData.setLocalData(jobInfo, agentModel.getToolGroupAgent(groupName).getBufferInfo(), toolInfo,agentModel, agentModel.getCurrentTime());					
					
				}
			}
		//}
		
		return traningDataset;
	
	}
	
	public TrainingDatasetMap<RoutingTrainingData> geRoutingTrainingDataset(){
		TrainingDatasetMap<RoutingTrainingData> trainingDatasetMap=new TrainingDatasetMap<RoutingTrainingData> ();
		
		for(String groupName:routingDataset.keySet()){
			TrainingDataset<RoutingTrainingData> traningDataset=new TrainingDataset<RoutingTrainingData>(groupName);
			trainingDatasetMap.putDataset(groupName, traningDataset);
			TrainingDatasetRaw<String, ToolGroupInfo> dataset = routingDataset.get(groupName);
			for(DataMeta<ToolGroupInfo> data:dataset){
				AgentBasedModel agentModel = data.getState();
				//ToolGroupInfo toolGroupInfo=agentModel.getToolGroup(groupName).getToolGroupInfo();
				JobInfo jobInfo=(JobInfo) data.getLocation();
				
				for(ActionAndValue<ToolGroupInfo> actionAndValue:data.getActionAndValues()){
					ToolGroupInfo toolGroupInfo=actionAndValue.getAction();
					RoutingTrainingData trainingData=new RoutingTrainingData();
					traningDataset.add(trainingData);
					//objective
					trainingData.setObjective(actionAndValue.getValue());
					//global
					setGlobalData(trainingData,agentModel,agentModel.getCurrentTime());
//					
//					//local
					
//					trainingData.setProcessingTime(jobInfo.getCurrentProcessTime());
//					trainingData.setSetupTime(toolInfo.getSetupTime(jobInfo));
					
					
				}
			}
		}
		
		return trainingDatasetMap;
	
	}
	
	private void setGlobalData(GlobalData globalData,AgentBasedModel agentModel, long currentTime){
		
		new GlobalDataCompute(agentModel).getGlobalData(globalData, currentTime);
		
	}
	

}
