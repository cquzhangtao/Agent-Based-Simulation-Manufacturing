package manu.model.component;

import java.io.Serializable;

public class ReleasePolicy implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -420943308930176226L;
	public static int ConstantTime = 0;
	public static int ConstantWIP = 1;
	public static int VoidStarve = 2;
	public static int ConstantTimeDetail = 3;
	public static int ConstantWIPDetail = 4;
	public static int PlanLotAndDueDate = 5;
	public static int Bottleneck = 6;
	public static int IntelligentControl = 7;
	
	public int releasePolicy=0;
	public double intervalTime=0;
	public int costWIP;
	public int beginningReleasePolicy;
	public int avgProcessStepNum;
	
	public boolean isPolicy(int policy){
		return this.releasePolicy==policy;
	}
	private static String[] policyStr=new String[]{
			"CONINT","CONWIP","VOIDST","CONINTD","CONWIPD","PLANDUEDATE","BOTTLENECK","INTELLIGENT"
	};
	@Override
	public String toString(){
		return policyStr[releasePolicy];
	}
	
	public static String getString(int index){
		return policyStr[index];
	}
	
	
}
