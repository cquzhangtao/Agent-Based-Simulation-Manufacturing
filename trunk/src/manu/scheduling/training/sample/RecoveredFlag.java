package manu.scheduling.training.sample;

public class RecoveredFlag {
	private boolean recovered;
	public synchronized boolean isRecovered(){
		return recovered;
	}
	public synchronized void setRecovered(boolean b){
		recovered=b;
	}
}
