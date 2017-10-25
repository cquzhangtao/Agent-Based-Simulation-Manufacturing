package manu.sbo.algorithm.antcolony;

public class AntColony {
	public static Double[] run(final Double[][] d) {
		Double[] q = new Double[2];
		Problem problem = new Problem();
		problem.addDecisionVaraible(100, 0);
		problem.addDecisionVaraible(100, 0);
		problem.setGenerationNum(100);
		problem.setAntNum(100);
		problem.setDecimal(5);
		problem.setMaximization(false);
		problem.setEvaluation(new EvaluationInterface() {

			@Override
			public double evaluate(DecisionVaraibles decisionVaraibles,int generationIndex) {
				double q1 = decisionVaraibles.get(0).getValue();
				double q2 = decisionVaraibles.get(1).getValue();
				if(q1+q2>1){
					return Double.MAX_VALUE;
				}
				double r = function(q1,q2,d);
				return r;
			}
		});
		AntColonyAlgorithm alg = new AntColonyAlgorithm(problem);
		alg.setEnchanceCoefficien(1);
		alg.run();
		q[0]=alg.getBestSolution().getDecisionVaraibles().get(0).getValue();
		q[1]=alg.getBestSolution().getDecisionVaraibles().get(1).getValue();
		return q;
	}

	private static double function(double q1, double q2, Double[][] d) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < d.length; i++) {
			double cost = costFunction(q1, q2, d[i]);
			if (min > cost) {
				min = cost;
			}
		}
		return min;
	}

	private static double costFunction(double q1, double q2, Double[] d) {

		return 0;
	}
	
	public  int add(int a ,int b){
		return a+b;
	}

}
