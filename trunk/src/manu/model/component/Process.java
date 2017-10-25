package manu.model.component;

import java.util.List;

import manu.utli.Entity;

public class Process extends Entity{

	private String skill;
	
	private List<ToolGroupInfo> toolGroups;

	
	public Process(String name,String skill){
		super(name);
		this.setSkill(skill);
	}

	

	public List<ToolGroupInfo> getToolGroups() {
		return toolGroups;
	}

	public void setToolGroups(List<ToolGroupInfo> toolGroups) {
		this.toolGroups = toolGroups;
	}



	public String getSkill() {
		return skill;
	}



	public void setSkill(String skill) {
		this.skill = skill;
	}


	
}
