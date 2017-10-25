package manu.simulation;

public interface SimulatorListener {
	
	public void onStart();
	public void onEnd();
	public void onReset();
	public void onStateChange(String state);
	public void onTimeAdvance(long currentTime,long beginTime);

}
