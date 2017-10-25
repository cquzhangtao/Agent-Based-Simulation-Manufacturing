package manu.sbo.algorithm.antcolony;

public class Problem {
	
	private DecisionVaraibles decisionVaraibles;
	private EvaluationInterface evaluation;
	private int decimal=4;
	private int generationNum=50;
	private int antNum=20;
	private boolean maximization=false;
	
	public Problem()
	{
		decisionVaraibles=new DecisionVaraibles();

	}
	public void addDecisionVaraible(double max,double min){
		decisionVaraibles.addDecisionVaraible(max,min);
	}

	public DecisionVaraibles getDecisionVaraibles() {
		return decisionVaraibles;
	}

	public EvaluationInterface getEvaluation() {
		return evaluation;
	}

	public void setDecisionVaraibles(DecisionVaraibles decisionVaraibles) {
		this.decisionVaraibles = decisionVaraibles;
	}

	public void setEvaluation(EvaluationInterface evaluation) {
		this.evaluation = evaluation;
	}

	public int getDecimal() {
		return decimal;
	}

	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}
	public int getColNum() {
		return decisionVaraibles.size()*decimal;
	}

	public int getGenerationNum() {
		return generationNum;
	}

	public int getAntNum() {
		return antNum;
	}

	public void setGenerationNum(int generationNum) {
		this.generationNum = generationNum;
	}

	public void setAntNum(int antNum) {
		this.antNum = antNum;
	}
	public void setMaximization(boolean maximization) {
		this.maximization = maximization;
	}
	public boolean isMaximization() {
		return maximization;
	}

}
