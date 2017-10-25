package manu.sbo.algorithm.antcolony.example;

import manu.sbo.algorithm.antcolony.AntColonyAlgorithm;
import manu.sbo.algorithm.antcolony.DecisionVaraibles;
import manu.sbo.algorithm.antcolony.EvaluationInterface;
import manu.sbo.algorithm.antcolony.Problem;

public class AntColony {
	
	public static void main(String[]args){
		double[][] d={{1,2,2},{0,8,5}};
		
	System.out.print(run(d).toString());
		//System.out.print(sum(d));
		
	}
	public static double[] run(final double[][] d) {
		final int qNum=3;
		double[] q = new double[2];
		Problem problem = new Problem();
		for(int i=0;i<qNum;i++){
			problem.addDecisionVaraible(100, 0);
		}
		problem.setGenerationNum(100);
		problem.setAntNum(50);
		problem.setDecimal(5);
		problem.setMaximization(false);
		problem.setEvaluation(new EvaluationInterface() {

			public double evaluate(DecisionVaraibles decisionVaraibles,int geni) {
			
				double []q=new double[qNum];
				double sum=0;
				String  str="";
				for(int i=0;i<qNum;i++){
					q[i]=decisionVaraibles.get(0).getValue();
					sum+=q[i];
					str+=q[i]+" , ";
				}

				if(Math.abs(sum-1)>0.1){
					//System.out.println("Bad ants, discarded!");
					return Double.MAX_VALUE-100;
				}
				double r = function(q,d);
				//System.out.println("Ant "+str+", cost function: "+r);
				return r;
			}

			
		});
		AntColonyAlgorithm alg = new AntColonyAlgorithm(problem);
		alg.setEnchanceCoefficien(1);
		alg.run();
		q[0]=alg.getBestSolution().getDecisionVaraibles().get(0).getValue();
		q[1]=alg.getBestSolution().getDecisionVaraibles().get(1).getValue();
		System.out.print(alg.getBestSolution().toString());
		return q;
	}

	private static double function(double []q, double[][] d) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < d.length; i++) {
			double cost = costFunction(q, d[i]);
			if (min > cost) {
				min = cost;
			}
		}
		return min;
	}

	private static double costFunction(double []q, double[] d) {
		double v=q[0]*Math.abs(50-(d[0]-31.3489)/(62.8679-31.3489))+q[1]*Math.abs(0.0124-(d[1]-0.1303)/(0.1648-0.1303))+q[2]*Math.abs((d[2]+13.6913)/(38.5818+13.6913));
		return v;
	}
	
	public static int add(int a ,int b){
		return a+b;
	}
	
	public static double[] cal(int a ,int b){
		double[] r=new double[2];
		r[0]=a-b;
		r[1]=a+b;
		return r;
	}
	public static double[] calMatrix(int[] a ,int[] b){
		
		double[] r=new double[a.length];
		for(int i=0;i<a.length;i++){
			r[i]=a[i]+b[i];
		}

		return r;
	}
	public static double[] sum(double[][] a ){
		
		double[] sumA=new double[a.length];
		for(int i=0;i<a.length;i++){
			double sum=0;
			for(int j=0;j<a[i].length;j++){
			sum+=a[i][j];
			}
			sumA[i]=sum;
		}

		return sumA;
	}

}
