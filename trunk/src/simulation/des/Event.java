package simulation.des;

public abstract class Event {
	private long time=0;
	
	public abstract Object[] fire();
	
	public long getTime(){
		return time;
	}
	public void setTime(long t){
		time=t;
	}

}
