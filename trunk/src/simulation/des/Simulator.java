package simulation.des;

import java.util.LinkedList;
import java.util.List;



public class Simulator {
	
	private List<Event> events=new LinkedList<Event>();
	private List<ConditionalEvent> conditionalEvents=new LinkedList<ConditionalEvent>();
	private long time=0;
	
	private SimulationState state=SimulationState.stop;
	
	public void run(){
		if(state==SimulationState.run){
			return;
		}
		state=SimulationState.run;
		while(advance()){
			System.out.println(time);
		}		
		
	}
	public void stop(){
		state=SimulationState.stop;
	}


	
	private boolean advance(){
		
		if(events.isEmpty()&&conditionalEvents.isEmpty()){
			state=SimulationState.stop;		
			return false;	
		}
		if(state==SimulationState.stop){
			return false;
		}
		
		List<Event> firedEvents=new LinkedList<Event>();
	
		for(ConditionalEvent event:conditionalEvents){
			if(event.meetCondition()){
				fireEvent(event);
				firedEvents.add(event);
			}
		}
		conditionalEvents.removeAll(firedEvents);
		Event nextEvent=getNextEvent();
		if(nextEvent!=null){
			time=nextEvent.getTime();
			fireEvent(nextEvent);
			events.remove(nextEvent);
			
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void fireEvent(Event event){
		Object[] e=event.fire();
		
		if(e[0]!=null){
			insertEvents((List<Event>) e[0]);
		}
		if(e[1]!=null){
			conditionalEvents.addAll((List<ConditionalEvent>) e[1]);
		}
	}
	
	public void insertEvents(List<Event> es){
		for(Event e:es){
			insertEvent(e);
		}
	}
	
	public void insertEvent(Event e){
		for(int i=0;i<events.size();i++){
			Event event=events.get(i);
		
			if(e.getTime()<=event.getTime()){
				events.add(i,e);
				return;
			}
		}
		events.add(e);
	}
	
	protected Event getNextEvent(){
		if(events.isEmpty()){
			return null;
		}
		return events.get(0);
	}
	

}
