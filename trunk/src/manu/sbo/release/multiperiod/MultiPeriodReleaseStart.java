package manu.sbo.release.multiperiod;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MultiPeriodReleaseStart {
	
	public static void main(String []args){
		Order order=new Order();
		order.addProduct(string2long("2014-02-01"),string2long("2014-04-25") , 267, 150);
		order.addProduct(string2long("2014-02-10"),string2long("2014-04-15") , 450, 120);
		order.addProduct(string2long("2014-03-20"),string2long("2014-05-25") , 333, 150);
		order.addProduct(string2long("2014-02-05"),string2long("2014-03-25") , 364, 110);
		order.addProduct(string2long("2014-02-20"),string2long("2014-05-05") , 267, 180);
		GraphicShow g1 = new GraphicShow (order);
		GraphicShow g = new GraphicShow (order);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MultiPeriodAlgorithm alg=new MultiPeriodAlgorithm(order);
		alg.run();
		g.update();
		
		
	}
	
	public static long string2long(String str){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date result = null;
		try {
			result = df.parse(str);
			return result.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
        

	}

}
