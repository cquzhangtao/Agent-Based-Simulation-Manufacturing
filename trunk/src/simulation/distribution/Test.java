package simulation.distribution;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RollbackRandom r=new RollbackRandom();
		for(int i=0;i<10;i++){
			System.out.println(r.nextDouble());
		}
		System.out.println("*******saved**********");
		r.save();
		for(int i=0;i<5;i++){
			System.out.println(r.nextDouble());
		}
		r.recover();
		System.out.println("*******recovered**********");
		for(int i=0;i<8;i++){
			System.out.println(r.nextDouble());
		}
		r.recover();
		System.out.println("*****recovered************");
		for(int i=0;i<4;i++){
			System.out.println(r.nextDouble());
		}
		r.save();
		System.out.println("******saved***********");
		for(int i=0;i<4;i++){
			System.out.println(r.nextDouble());
		}
		r.recover();
		System.out.println("******recovered***********");
		for(int i=0;i<5;i++){
			System.out.println(r.nextDouble());
		}
	}

}
