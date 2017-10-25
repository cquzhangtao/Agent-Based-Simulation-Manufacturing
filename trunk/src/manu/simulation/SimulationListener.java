package manu.simulation;

public interface SimulationListener {
	

	public void onEnd();
	public void onTimeAdvance();
	public boolean onEndDecision();

}
