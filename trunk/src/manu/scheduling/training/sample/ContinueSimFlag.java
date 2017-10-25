package manu.scheduling.training.sample;

public class ContinueSimFlag {
	boolean continueSim=false;
	public synchronized boolean isContinue(){
		return continueSim;
	}
	public synchronized void setContinue(boolean b){
		continueSim=b;
	}

}
