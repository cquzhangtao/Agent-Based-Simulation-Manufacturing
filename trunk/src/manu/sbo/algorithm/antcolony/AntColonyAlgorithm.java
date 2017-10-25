package manu.sbo.algorithm.antcolony;

public class AntColonyAlgorithm extends AntColonyAlgorithmAbstract {
	private Solution solution;
	private EvaluationInterface evaluation;

	public AntColonyAlgorithm(Problem problem){
		super(NetworkUtli.createNetwork(problem.getColNum()),problem.getGenerationNum(),problem.getAntNum());
		this.solution = new Solution(problem.getDecisionVaraibles(),problem.getDecimal());
		this.evaluation=problem.getEvaluation();
		this.setMaximization(problem.isMaximization());
	}

	@Override
	protected double evaluateAnt(Ant ant,int generationIndex) {
		solution.ant2solution(ant);
		return evaluation.evaluate(solution.getDecisionVaraibles(),generationIndex);

	}

	public Solution getBestSolution() {
		solution.ant2solution(getBestAnt());
		return solution;
	}

}
