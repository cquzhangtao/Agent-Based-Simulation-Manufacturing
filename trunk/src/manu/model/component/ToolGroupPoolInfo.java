package manu.model.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import manu.scheduling.DecisionMaker;
import manu.scheduling.training.data.ReleaseTrainingData;
import manu.scheduling.training.data.RoutingTrainingData;
import manu.utli.Entity;

public class ToolGroupPoolInfo extends Entity{
	
	private Skill skill;
	private List<ToolGroupInfo> toolGroups=new ArrayList<ToolGroupInfo>();
	private DecisionMaker<RoutingTrainingData> decisionMaker;
	
	public List<ToolGroupInfo> getToolGroups() {
		return toolGroups;
	}

	public void setToolGroups(List<ToolGroupInfo> toolGroups) {
		this.toolGroups = toolGroups;
	}
	
	public String getToolGroupNames(){
		String tgs="";
		for(ToolGroupInfo tg:toolGroups){
			tgs+=tg.getToolGroupName()+",";
		}
		
		return tgs;
	}
	
	public ToolGroupInfo getBestToolGroup(JobInfo jobInfo,long currentTime){
		
		return getBestToolGroup(toolGroups,jobInfo,currentTime,AllocationRule.SQL);
		
	}
	public ToolGroupInfo getBestToolGroup(JobInfo jobInfo,long currentTime,int rule){
		
		return getBestToolGroup(toolGroups,jobInfo,currentTime,rule);
		
	}
	public ToolGroupInfo getBestToolGroup(List<ToolGroupInfo>toolGroups,JobInfo jobInfo,long currentTime){
		return getBestToolGroup(toolGroups,jobInfo,currentTime,AllocationRule.SQL);
		
	}
	public ToolGroupInfo getBestToolGroup(List<ToolGroupInfo>toolGroups,JobInfo jobInfo,long currentTime,int rule){
		double max=-1*Double.MAX_VALUE;
		
		List<ToolGroupInfo> selectedToolGroups=new ArrayList<ToolGroupInfo>();
		for(ToolGroupInfo tg:toolGroups){
			double v=AllocationRule.getPriorityValue(rule, currentTime, jobInfo, tg);
			if(max<v){
				max=v;
				selectedToolGroups.clear();
				selectedToolGroups.add(tg);			
			}
			else if(max==v){
				selectedToolGroups.add(tg);
			}
		}
//		
		Random rnd=new Random(100);		
		return selectedToolGroups.get(rnd.nextInt(selectedToolGroups.size()));
		
//		List<ToolGroupInfo> selectedToolGroupsNew=new ArrayList<ToolGroupInfo>();
//		max=Double.MIN_VALUE;
//		for(ToolGroupInfo tg:selectedToolGroups){
//			double v=AllocationRule.getPriorityValue(AllocationRule.STPT, currentTime, jobInfo, tg);
//			if(max<v){
//				max=v;
//				selectedToolGroupsNew.clear();
//				selectedToolGroupsNew.add(tg);			
//			}
//			else if(max==v){
//				selectedToolGroupsNew.add(tg);
//			}
//		}
//		
//		Random rnd=new Random(100);		
//		return selectedToolGroupsNew.get(rnd.nextInt(selectedToolGroupsNew.size()));
		
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public DecisionMaker<RoutingTrainingData> getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(DecisionMaker<RoutingTrainingData> decisionMaker) {
		this.decisionMaker = decisionMaker;
	}
	
	
	
	

}
