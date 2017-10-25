package simulation.des;

public class Start {
	public static void main(String[] args){
		Simulator sim=new Simulator();		
		sim.insertEvent(new TestEvent());		
		sim.run();
	}

}
