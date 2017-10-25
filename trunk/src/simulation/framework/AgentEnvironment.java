package simulation.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import manu.agent.component.Job;

public class AgentEnvironment implements Serializable{
	private HashMap<String, Queue> agentList = new HashMap<String, Queue>();
	private AgentStateCount count = new AgentStateCount();
	private ModelUpdateFlag modelUpdateFlag=new ModelUpdateFlag();
	
	public void reset(){
		getCount().reset();
		modelUpdateFlag.resetUpdateFlag();
	}
	public void reset(ArrayList<Job>jobs){
		getCount().reset();
		modelUpdateFlag.resetUpdateFlag();
		for(Job job:jobs){
			getMessageQueues().remove(job.getAgentName());
		}
	}
	public void clear(){
		getMessageQueues().clear();
		reset();
	}
	public Queue getQueue(String name){
		return getMessageQueues().get(name);
	}
	public byte[] getLock(String name){
		return getMessageQueues().get(name).getLock();
	}
	
	public void addMessage(Message msg){
		getMessageQueues().get(msg.getReceiver()).offer(msg);
	}
	public void notifyReceiver(String receiver){
		getMessageQueues().get(receiver).getLock().notifyAll();
	}
	public void notifySimulator(){
		getMessageQueues().get("Simulator").getLock().notifyAll();
	}
	public Queue register(String agentName){
		Queue messageQueue = new Queue(agentName);
		synchronized (getMessageQueues()){
			getMessageQueues().put(agentName, messageQueue);
		}
		return messageQueue;
	}
	public void register(Agent agent){
		Queue messageQueue = new Queue(agent.getAgentName());
		synchronized (getMessageQueues()){
			getMessageQueues().put(agent.getAgentName(), messageQueue);
		}
		agent.setMessageQueue(messageQueue);
		agent.setEnvironment(this);
	}
	public void register(SimulationBase agent){
		Queue messageQueue = new Queue(agent.getName());
		synchronized (getMessageQueues()){
			getMessageQueues().put(agent.getName(), messageQueue);
		}
		agent.setMessageQueue(messageQueue);
		agent.setEnvironment(this);
	}
	public boolean updated(){
		return modelUpdateFlag.isUpdated();
	}
	public void resetUpdatedState(){
		modelUpdateFlag.resetUpdateFlag();
	}
	public void update(){
		modelUpdateFlag.update();
	}
	public void clearAgent(){
		getMessageQueues().clear();
	}
	public Queue addSimulator(){
		return register("Simulator");
	}
	public void deRegister(String name){
		synchronized(getMessageQueues()){
			getMessageQueues().remove(name);
		}
	}
	@Override
	public AgentEnvironment clone(){
		AgentEnvironment a=new AgentEnvironment();
		a.getCount().set(0);
		a.modelUpdateFlag.set(modelUpdateFlag.get());
		synchronized(getMessageQueues()){
			for (Queue q : getMessageQueues().values()) {
				a.getMessageQueues().put(q.getName(), new Queue(q.getName()));
			}
		}
//		ArrayList<Queue> queueList=new ArrayList<Queue>(getAgentList().values());
//		for(int i=0;i<queueList.size();i++){
//			
//			a.getAgentList().put(queueList.get(i).getName(), queueList.get(i).clone());
//		}
		return a;
	}
	public HashMap<String, Queue> getMessageQueues() {
		return agentList;
	}
	public void setAgentList(HashMap<String, Queue> agentList) {
		this.agentList = agentList;
	}
	public AgentStateCount getCount() {
		return count;
	}
	public void setCount(AgentStateCount count) {
		this.count = count;
	}
	
	public String getAgentNames(){
		String str="";
		for (Queue q : getMessageQueues().values()) {
			str+=q.getName()+",";
			
		}
		return str;
	}
	

}
