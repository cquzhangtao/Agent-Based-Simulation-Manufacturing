package simulation.distribution;

import java.io.Serializable;
import java.util.Map;

public class RandomVariable implements Serializable{
	
	private RollbackRandom rand=new RollbackRandom();
	private Map<String,Double> parameters;
	private Distribution distribution;
	public RandomVariable(String distri,Map<String,Double> parameter){
		distribution=new Distribution(distri) ;
		this.parameters=parameter;
	}
	
	public double getNext(){		
		return distribution.getRandom(parameters, rand);
	}
	
	public void save(){
		rand.save();
		distribution.save();
	}
	public void recover(){
		rand.recover();
		distribution.recover();
	}
	public void reset(){
		rand.reset();
		distribution.reset();
	}
	public double getParameter(String name){
		return parameters.get(name);
	}
	public void setParameter(String name,double value){
		parameters.put(name, value);
	}

}
