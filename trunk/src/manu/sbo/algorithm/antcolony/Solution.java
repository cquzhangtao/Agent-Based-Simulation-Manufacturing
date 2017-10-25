package manu.sbo.algorithm.antcolony;

import java.util.List;

public class Solution {

	
	private DecisionVaraibles decisionVaraibles;
	private int decimal=4;
	
	public Solution(DecisionVaraibles decisionVaraibles,int decimal)
	{
		this.decisionVaraibles=decisionVaraibles;
		this.decimal=decimal;
	}
	
	public boolean ant2solution(Ant ant) {
		// TODO Auto-generated method stub
		int index=0;
		List<Node> path = ant.getPath();
		for(int i=1;i<path.size()-1;i=i+decimal)
		{
			DecisionVaraible decisionVaraible=decisionVaraibles.get(index);
			double temp=0;
			for(int j=0;j<decimal;j++){
				temp+=path.get(i+j).getValue()*Math.pow(10,-1*(j+1));
			
			}
			decisionVaraible.setNormalizedValue(temp);
			index++;
		}
		return true;
		
	}
	
	public String toString(){
		return decisionVaraibles.toString();
	}


	public DecisionVaraibles getDecisionVaraibles() {
		return decisionVaraibles;
	}


	public void setDecisionVaraibles(DecisionVaraibles decisionVaraibles) {
		this.decisionVaraibles = decisionVaraibles;
	}

}
