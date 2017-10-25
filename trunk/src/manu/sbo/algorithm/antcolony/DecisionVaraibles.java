package manu.sbo.algorithm.antcolony;

import java.util.ArrayList;
public class DecisionVaraibles extends ArrayList<DecisionVaraible>{

	private static final long serialVersionUID = 1L;

	public void addDecisionVaraible(double max,double min){
		this.add(new DecisionVaraible(max,min));
	}
	
	public String toString(){
		String str="";
		for(DecisionVaraible dec:this){
			str+=dec.getValue()+",";
		}
		return str;
	}
	
	public void reset()
	{
		for(DecisionVaraible dec:this){
			dec.reset();
		}
		
	}
}
