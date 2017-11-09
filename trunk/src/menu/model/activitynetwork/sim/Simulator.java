package menu.model.activitynetwork.sim;

import java.util.ArrayList;
import java.util.List;

import menu.model.activitynetwork.Activity;

public class Simulator {
	
	private List<Activity> activityPool;
	private long clock;
	
	public void run(){
		
		while(!activityPool.isEmpty()){
			clock=advance();
		}
		
	}
	private long advance(){
		List<Activity> executedActivities=new ArrayList<Activity>();
		List<Activity> newActivities=new ArrayList<Activity>();;
		long maxTime=0;
		for(Activity act:activityPool){
			if(act.isReady()){
				executedActivities.add(act);
				newActivities.addAll(act.execute());
				long time=act.getTime();
				if(time>maxTime){
					maxTime=time;
				}
			}
		}
		activityPool.removeAll(executedActivities);
		activityPool.addAll(newActivities);
		return clock+maxTime;
	}
	public long getClock() {
		return clock;
	}

}
