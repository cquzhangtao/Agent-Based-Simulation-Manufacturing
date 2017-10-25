package manu.sbo.release.multiperiod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import manu.sbo.algorithm.antcolony.AntColonyAlgorithm;
import manu.sbo.algorithm.antcolony.DecisionVaraible;
import manu.sbo.algorithm.antcolony.DecisionVaraibles;
import manu.sbo.algorithm.antcolony.EvaluationInterface;
import manu.sbo.algorithm.antcolony.Problem;
import manu.sbo.algorithm.antcolony.Solution;
import manu.sbo.framework.iSimulation;
import manu.sbo.release.oneperiod.ReleaseSolution;
import manu.sbo.release.oneperiod.SimEvaluation;

public class MultiPeriodAlgorithm {
	Order order;
	long currentTime;
	long previousTime;
	long minTimeWindow=10*24*60*60*1000;
	
	public MultiPeriodAlgorithm(Order order){
		this.order=order;
		currentTime=getLatestDueDate();
	}
	
	public void run(){
		int index=1;
		while(getNextTimeLine()!=-1){
			index++;
			while(previousTime-currentTime<minTimeWindow){
				if(dealWithSmallSlice()==-1){
					repairTimeSlice();
					return;
				}
			}
			List<Product> includedProoducts = getProductsInSlice();
			addSlicetoProduct(includedProoducts);
			List<Product> productsForOpt = getProductForOptimization(includedProoducts);
			Map<Product, Double> intervals = getOptimalInterval1(productsForOpt);
			updateReleaseInterval(includedProoducts,intervals);
			updateStartTime(includedProoducts);
			
		}
		repairTimeSlice();
	}
	
	private void repairTimeSlice(){
		for(Product p:order.getProducts()){
			for(TimeSlice st:p.getTimeSlice()){
				int index=p.getTimeSlice().indexOf(st);
				long l=p.getTimeSliceIndex().get(index);
				if(st.getStart()>l&&index<p.getTimeSlice().size()-1){
					st.setStart(l);
				}
			}
		}
	}
	
	public Map<Product,Double> getOptimalInterval1(List<Product> productsForOpt){
		Random rnd=new Random();
		Map<Product,Double> result=new HashMap<Product,Double>();
		System.out.println("*******");
		for(Product p:productsForOpt){
			if(productsForOpt.size()==1){
				result.put(p,(double) p.getMinReleaseInterval()*1.5);
			}else{
			double l=p.getMinReleaseInterval()+(rnd.nextDouble()+0.5*productsForOpt.size()/order.getProducts().size())*(p.getMaxReleaseInterval(previousTime)*1.4-p.getMinReleaseInterval());
			result.put(p,l);
			}
			//result.put(p,(double) p.getMinReleaseInterval());
			//System.out.println(p.getId()+","+l+","+p.getMinReleaseInterval()+","+p.getMaxReleaseInterval(previousTime));
		}
		System.out.println("*******");
		return result;
	}
	
	public Map<Product,Double> getOptimalInterval(List<Product> productsForOpt){
		Problem problem = new Problem();
		for(Product p:productsForOpt){
			problem.addDecisionVaraible(p.getMinReleaseInterval(), p.getMaxReleaseInterval(currentTime));
		}

		problem.setGenerationNum(50);
		problem.setAntNum(10);
		problem.setDecimal(4);
		problem.setMaximization(true);
		final iSimulation sim=new iSimulation("E:\\project\\AgentBasedSim\\model\\Bottelneck.mod");
		final SimEvaluation simEva=new SimEvaluation();
		problem.setEvaluation(new EvaluationInterface() {

			@Override
			public double evaluate(DecisionVaraibles decisionVaraibles,
					int generationIndex) {
				ReleaseSolution solution=new ReleaseSolution();
				for(DecisionVaraible dec:decisionVaraibles){
					solution.addThroughput(dec.getValue());
				}
				solution.setPlannedTime((previousTime-currentTime)/1000.0/60/60/24);
				double result=simEva.evaluation(solution, sim);
				//System.out.println(result);
				return result;
			}
		});
		AntColonyAlgorithm alg = new AntColonyAlgorithm(problem);
		alg.run();
		Solution sol = alg.getBestSolution();
		Map<Product,Double> result=new HashMap<Product,Double>();
		int index=0;
		for(Product p:productsForOpt){
			result.put(p, sol.getDecisionVaraibles().get(index).getValue());
			index++;
		}
		return result;
	}
	
	private void updateStartTime(List<Product> includedProducts){
		for(Product p:includedProducts){
			p.updateStartTime(currentTime,previousTime);
		}
	}
	
	private void updateReleaseInterval(List<Product> includedProducts,Map<Product,Double> interval){
		for(Product p:includedProducts){
			TimeSlice ts=p.getTimeSlice(currentTime);
			TimeSlice pre =p.getTimeSlice(previousTime);
			if(ts.type==1){
				ts.setReleaseInterval(interval.get(p));
				if(pre!=null&&pre.getType()==3){
					pre.setReleaseInterval(interval.get(p));
				}
				
			}else if(ts.type==2){
				if(pre==null){
					System.out.println("Sothing wrong");
				}else if(pre.getType()==3){
					System.out.println("Sothing wrong");
					pre.setReleaseInterval(p.getMinReleaseInterval());
					ts.setReleaseInterval(p.getMinReleaseInterval());
				}else{
					ts.setReleaseInterval(pre.getReleaseInterval());
				}
			}else if(ts.type==3){
				
			}else{
				System.out.println("Sothing wrong");
			}
		}
		
	}
	private List<Product> getProductForOptimization(List<Product> includedProducts){
		List<Product> includedProductsopt=new ArrayList<Product>();
		for(Product p:includedProducts){
			if(p.getTimeSlice(currentTime).getType()==1){
				includedProductsopt.add(p);
			}
		}
		return includedProductsopt;
	}
	
	private void addSlicetoProduct(List<Product> includedProducts){
		for(Product p:includedProducts){
			long start=p.getStartDate();
			long end=p.getDueDate();
			if(start>=currentTime&&start<previousTime){
				if(end<=previousTime){
					p.addSlice(start, end,1,currentTime);
					
				}else{
					if(previousTime-start>(previousTime-currentTime)/2.0){
						p.addSlice(start, previousTime,1,currentTime);
					}else{
						p.addSlice(start, previousTime,2,currentTime);
					}					
				}
			}
				
			else if(end>currentTime&&end<=previousTime)
			{
				if(start>=currentTime){
					p.addSlice(start, end,1,currentTime);
				}else{
					if(end-currentTime>(previousTime-currentTime)/2.0){
						p.addSlice(currentTime, end,1,currentTime);
					}else{
						p.addSlice(currentTime, end,3,currentTime);
					}
				}
				
				
			}else if(start<currentTime&&end>previousTime){
				p.addSlice(currentTime, previousTime,1,currentTime);
			}
			else
			{
				System.out.println("Sothing wrong");
			}
		}
		
	}
	
	private List<Product> getProductsInSlice(){
		List<Product> includedProducts=new ArrayList<Product>();
		for(Product p:order.getProducts()){
			long start=p.getStartDate();
			long end=p.getDueDate();
			if(start>=currentTime&&start<previousTime
					||end>currentTime&&end<=previousTime
					||start<currentTime&&end>previousTime){
				includedProducts.add(p);
			}else if(end<=currentTime||start>=previousTime){
				//do nothing
			}
			else
			{
				System.out.println("Something wrong");
			}
		}
		return includedProducts;
	}
	
	private int dealWithSmallSlice(){
		long preTime=previousTime;
		if(getNextTimeLine()==-1){
			return -1;
		}
		previousTime=preTime;
		return 1;
	}
	
	private int getNextTimeLine(){
		long min=Long.MAX_VALUE;
		long nextTime=currentTime;
		for(Product p:order.getProducts()){
			if(min>currentTime-p.getStartDate()&&currentTime>p.getStartDate()){
				min=currentTime-p.getStartDate();
				nextTime=p.getStartDate();
			}
			if(min>currentTime-p.getDueDate()&&currentTime>p.getDueDate()){
				min=currentTime-p.getDueDate();
				nextTime=p.getDueDate();
			}			
		}
		if(nextTime==currentTime)
			return -1;
		previousTime=currentTime;
		currentTime=nextTime;
		return 1;
	}
	private long getLatestDueDate(){
		long latest=Long.MIN_VALUE;
		for(Product p:order.getProducts()){
			if(latest<p.getDueDate()){
				latest=p.getDueDate();
			}
		}
		return latest;
		
	}

}
