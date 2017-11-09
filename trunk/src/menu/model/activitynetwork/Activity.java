package menu.model.activitynetwork;

import java.util.List;

public class Activity {
	
	private List<Activity> predecessors;
	private List<Activity> successors;
	
	private MaterialRequirements matRequ;
	private ResourceRequirements resRequ;
	private OutputMatierals outMats;
	
	private List<MaterialAndQuantity> materials;
	private List<ResourceAndQuantity> resources;
	
	private Duration duration;
	
	private ActivityState state;
	
	public MaterialRequirements getMatRequ() {
		return matRequ;
	}
	public void setMatRequ(MaterialRequirements matRequ) {
		this.matRequ = matRequ;
	}
	public ResourceRequirements getResRequ() {
		return resRequ;
	}
	public void setResRequ(ResourceRequirements resRequ) {
		this.resRequ = resRequ;
	}
	public OutputMatierals getOutMats() {
		return outMats;
	}
	public void setOutMats(OutputMatierals outMats) {
		this.outMats = outMats;
	}
	public List<Activity> getPredecessors() {
		return predecessors;
	}
	public void setPredecessors(List<Activity> predecessors) {
		this.predecessors = predecessors;
	}
	public List<Activity> getSuccessors() {
		return successors;
	}
	public void setSuccessors(List<Activity> successors) {
		this.successors = successors;
	}
	public ActivityState getState() {
		return state;
	}
	public void setState(ActivityState state) {
		this.state = state;
	}
	
	public long getTime(){
		return (long) duration.getTime(materials, resources).toUnit(Unit.millisecond);
	}
	
	private boolean arePredecessorsDone(){
		for(Activity act:predecessors){
			if(act.getState()!=ActivityState.done){
				return false;
			}
		}
		return true;
	}
	
	private boolean areMaterialsReady(){
		return true;
	}
	
	private boolean areResourcesReady(){
		return true;
	}
	
	
	
	
	public boolean isReady(){
		return arePredecessorsDone()&&areMaterialsReady()&&areResourcesReady();
	}
	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	public List<Activity> execute(){
		
		return successors;
	}


}
