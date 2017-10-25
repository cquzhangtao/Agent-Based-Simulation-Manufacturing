package manu.scheduling.training.sample;

public class SampleToolGroup {
	String name="";
	public synchronized boolean isMe(String str){
		if(name.equals(str))
			return true;
		return false;
	}
	public synchronized void set(String str){
		name=str;
	}
	public synchronized boolean isEmpty(){
		if(name.equals(""))
			return true;
		return false;
	}
	public synchronized void reset(){
		name="";
	}

}
