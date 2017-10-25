package manu.sbo.release.oneperiod;

import manu.sbo.framework.EvaluationAbstract;
import manu.simulation.result.SimulationResult;

public class SimEvaluation extends EvaluationAbstract{
	
	@Override
	protected double getValue(SimulationResult result) {
		return result.getToolGroupDataset().getAllToolData().getProcessTime();
		///result.getReleaseDataset().getAllReleaseDataset().getAvgWip();
		
	}

}
