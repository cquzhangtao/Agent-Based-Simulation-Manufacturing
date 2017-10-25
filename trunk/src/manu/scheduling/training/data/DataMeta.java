package manu.scheduling.training.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import manu.agent.AgentBasedModel;

public class DataMeta<T> implements Serializable{
	private AgentBasedModel state;
	private List<ActionAndValue<T>> actionAndValues=new ArrayList<ActionAndValue<T>>();
	private Object location;
	public DataMeta(AgentBasedModel model){
		
		setState(model);
	
	}
	public void addActionAndValue(T action, double value){
		actionAndValues.add(new ActionAndValue<T>(action,value));
	}

	public AgentBasedModel getState() {
		return state;
	}

	public void setState(AgentBasedModel state) {
		this.state = state;
	}

	public List<ActionAndValue<T>> getActionAndValues() {
		return actionAndValues;
	}

	public void setActionAndValues(List<ActionAndValue<T>> actionAndValue) {
		this.actionAndValues = actionAndValue;
	}
	public Object getLocation() {
		return location;
	}
	public void setLocation(Object location) {
		this.location = location;
	}
	

}
