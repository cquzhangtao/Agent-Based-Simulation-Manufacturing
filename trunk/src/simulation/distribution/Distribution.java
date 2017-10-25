package simulation.distribution;

import java.io.Serializable;
import java.util.Map;

public class Distribution implements Serializable{

	private String name;

	public Distribution(String name) {
		this.name = name;
	}

	private double randExp(Map<String, Double> para, RollbackRandom rand)// mean
																			// //
																			// is
																			// 1/lambda
	{
		double p;
		double mean = para.get("mean");
		if (mean > 10000)
			mean = 10000;
		double randres;
		while (true) {
			p = rand.nextDouble();
			if (p < 1 / mean)
				break;
		}
		randres = -mean * Math.log(mean * p);
		return randres;
	}

	double v1;
	double v2 = 0;
	int phase = 0;
	double s = 0;
	double v1_s;
	double v2_s = 0;
	int phase_s = 0;
	double s_s = 0;
	private double randNorm(Map<String, Double> para, RollbackRandom rand) {
		
		double x;
		if (phase == 0) {
			do {
				double u1 = rand.nextDouble();
				double u2 = rand.nextDouble();
				v1 = 2 * u1 - 1;
				v2 = 2 * u2 - 1;
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			x = v1 * Math.sqrt(-2 * Math.log(s) / s);
		} else
			x = v2 * Math.sqrt(-2 * Math.log(s) / s);
		phase = 1 - phase;
		x= x * para.get("variance") + para.get("mean");
		if(x<0)
		{
			x=1;
		}
		return x;
	}
	public void save(){
		v1_s=v1;
		v2_s=v2;
		phase_s=phase;
		s_s=s;
		
	}
	public void recover(){
		v1=v1_s;
		v2=v2_s;
		phase=phase_s;
		s=s_s;
	}
	public void reset(){
		v1=0;
		v2=0;
		phase=0;
		s=0;
	}

	public double getRandom(Map<String, Double> para, RollbackRandom rand) {
		if (name.equalsIgnoreCase("Exponential")) {
			return randExp(para, rand);
		}
		if (name.equalsIgnoreCase("Normal")) {
			return randNorm(para, rand);
		} else if (name.equalsIgnoreCase("Constant")) {
			return para.get("mean");
		} else
			return 0;
	}

}
