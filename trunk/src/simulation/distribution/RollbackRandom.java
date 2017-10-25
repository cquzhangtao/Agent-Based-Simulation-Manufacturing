package simulation.distribution;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class RollbackRandom implements Serializable{
	private long seed=400;

	private Random rand=new Random(seed);
	private int counter=0;
	private ArrayList<Double> data=new ArrayList<Double>();
	
	public double nextDouble(){
		if(counter<data.size()){
			double d=data.get(counter);
			counter++;
			return d;
		}else{
			double d=rand.nextDouble();
			data.add(d);
			counter++;
			return d;
		}		
	}
	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
		rand=new Random(seed);
	}
	public void save(){
		for(int i=0;i<counter;i++)
			data.remove(0);
		counter=0;
	}
	public void recover(){

		counter=0;
		
	}
	public void reset(){
		rand=new Random(seed);
		counter=0;
		data.clear();
	}
	public RollbackRandom clone(){
		RollbackRandom rnd=new RollbackRandom();
		rnd.seed=seed;
		
		return rnd;
	}

}
