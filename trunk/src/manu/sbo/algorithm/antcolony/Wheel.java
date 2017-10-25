package manu.sbo.algorithm.antcolony;

import java.util.Random;

public class Wheel {
	
	public static int getIndex(double[] data){
		double[] prob=new double[data.length];
		double sum=0;
		Random rnd=new Random();
		for(double d:data){
			sum+=d;
		}
		if(sum==0){
			return rnd.nextInt(data.length);
		}
		for(int i=0;i<data.length;i++){
			double subsum=0;
			for(int j=0;j<i+1;j++)
			{
				subsum+=data[j];
			}
			prob[i]=subsum/sum;
		}		
		
		double d=rnd.nextDouble();
		for(int i=0;i<prob.length;i++){
			if(d<prob[i]){
				return i;
			}
		}
		
		return 0;
		
	}
	public static int getIndex(int num){
		Random rnd=new Random();
		return rnd.nextInt(num);
	}

}
