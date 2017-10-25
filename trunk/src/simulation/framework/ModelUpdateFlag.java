package simulation.framework;

import java.io.Serializable;

public class ModelUpdateFlag implements Serializable{
	private boolean updated=false;
	public synchronized void update(){
		if(!updated)
			updated=true;
	}
	public synchronized boolean isUpdated(){
		return updated;
	}
	public synchronized void resetUpdateFlag(){
		updated=false;
	}
	public synchronized void set(boolean b){
		updated=b;
	}
	public synchronized boolean  get(){
		return updated;
	}
	
}
