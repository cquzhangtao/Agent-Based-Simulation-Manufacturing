package manu.scheduling.training.data;


public interface GlobalData {
	
	
	public double getWip();
	public double getWipWeight();
	public double[] getWipD();
	public double getWipDWeight();
	public double getBufferSize();
	public double getBufferSizeWeight();
	public double getBufferTime();
	public double getBufferTimeWeight();
	public double[] getBufferSizeD();
	public double getBufferSizeDWeight();
	public double[] getBufferTimeD();
	public double getBufferTimeDWeight();
	public double getUnaTooRat();
	public double getUnaTooRatWeight();
	public double[] getUnaTooRatD();
	public double getUnaTooRatDWeight();
	public double[] getUnaTooRatbyP();
	public double getUnaTooRatbyPWeight();
	public double[] getTotWaiTimD();
	public double getTotWaiTimDWeight();
	public double[] getTotWaiTimbyP();
	public double getTotWaiTimbyPWeight();
	public void setWip(double wip);
	public void setWipWeight(double wipWeight);
	public void setWipD(double[] wipD);
	public void setWipDWeight(double wipDWeight);
	public void setBufferSize(double bufferSize);
	public void setBufferSizeWeight(double bufferSizeWeight);
	public void setBufferTime(double bufferTime);
	public void setBufferTimeWeight(double bufferTimeWeight);
	public void setBufferSizeD(double[] bufferSizeD);
	public void setBufferSizeDWeight(double bufferSizeDWeight);
	public void setBufferTimeD(double[] bufferTimeD);
	public void setBufferTimeDWeight(double bufferTimeDWeight);
	public void setUnaTooRat(double unaTooRat);
	public void setUnaTooRatWeight(double unaTooRatWeight);
	public void setUnaTooRatD(double[] unaTooRatD);
	public void setUnaTooRatDWeight(double unaTooRatDWeight);
	public void setUnaTooRatbyP(double[] unaTooRatbyP);
	public void setUnaTooRatbyPWeight(double unaTooRatbyPWeight);
	public void setTotWaiTimD(double[] totWaiTimD);
	public void setTotWaiTimDWeight(double totWaiTimDWeight);
	public void setTotWaiTimbyP(double[] totWaiTimbyP);
	public void setTotWaiTimbyPWeight(double totWaiTimbyPWeight);
	public double getObjective();
	public void setObjective(double objective);
	

	

}
