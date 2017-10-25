package manu.scheduling.training.data;

import java.util.List;

import manu.agent.component.Job;
import manu.agent.component.Release;
import manu.agent.component.ToolGroup;

public class EnvironmentState {
	private Release releaseAgent;
	private List<ToolGroup> toolGroupAgents;
	private List<Job> jobAgents;
	
	public EnvironmentState(Release release, List<ToolGroup> toolGroups,
			List<Job> jobs) {
		releaseAgent=release;
		toolGroupAgents=toolGroups;
		jobAgents=jobs;
	}
	public Release getReleaseAgent() {
		return releaseAgent;
	}
	public List<ToolGroup> getToolGroupAgents() {
		return toolGroupAgents;
	}
	public List<Job> getJobAgents() {
		return jobAgents;
	}
	public void setReleaseAgent(Release releaseAgent) {
		this.releaseAgent = releaseAgent;
	}
	public void setToolGroupAgents(List<ToolGroup> toolGroupAgents) {
		this.toolGroupAgents = toolGroupAgents;
	}
	public void setJobAgents(List<Job> jobAgents) {
		this.jobAgents = jobAgents;
	}
}
