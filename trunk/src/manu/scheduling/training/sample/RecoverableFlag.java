package manu.scheduling.training.sample;

public class RecoverableFlag {
	private boolean recoverable=false;
	public synchronized boolean isRecoverable(){
		
		return recoverable;
	}
	public synchronized void setRecoverable(boolean b){
		recoverable=b;
	}

}
