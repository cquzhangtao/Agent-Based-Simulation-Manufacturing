package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class BufferData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8809002582114769983L;
	public String bufferName;
	public ArrayList<Long> changeTime = new ArrayList<Long>();
	public ArrayList<Integer> bufferSize = new ArrayList<Integer>();
	public int maxBufferSize;
	public double avgBufferSize;
	public ArrayList<Double> bufferSizebyDay = new ArrayList<Double>();
	int changeTimeSize;
	int bufferSizeSize;
	public 	int interval = 7*24 * 3600;
	
	public BufferData(String name){
		bufferName=name;
	}
	
	public void save(){
		changeTimeSize=changeTime.size();
		bufferSizeSize=bufferSize.size();
	}
	public void recover(){
		while(changeTime.size()>changeTimeSize)
			changeTime.remove(changeTimeSize);
		while(bufferSize.size()>bufferSizeSize)
			bufferSize.remove(bufferSizeSize);
	}
	
	public void reset(){
		changeTime.clear();
		//changeTime=null;
		bufferSize.clear();
		bufferSizebyDay.clear();
		maxBufferSize=0;
		avgBufferSize=0;
	}
	
	public void add(long time, int size) {
		if(changeTime.size()==0)
		{
			changeTime.add((long) 0);
			bufferSize.add(0);
			changeTime.add(time);
			bufferSize.add(size);
			return;
		}
		
		if (changeTime.get(changeTime.size() - 1) == time) {
			changeTime.remove(changeTime.size() - 1);
			bufferSize.remove(bufferSize.size() - 1);
		}
		changeTime.add(time);
		bufferSize.add(size);
	}

	public void stat() {

		int index=0;
		int beginIndex=0;
		if(changeTime.size()>0){
		for(int i=interval;i<changeTime.get(changeTime.size()-1);i=i+interval)
		{
			long sum=0;
			while(changeTime.get(index)<i){
				sum+=bufferSize.get(index)*(changeTime.get(index+1)-changeTime.get(index));
				index++;
			}
			if(beginIndex!=0)
				sum+=(changeTime.get(beginIndex)-i+interval)*bufferSize.get(beginIndex-1);
			sum-=(changeTime.get(index)-i)*bufferSize.get(index-1);
			bufferSizebyDay.add(1.0*sum/interval);
			beginIndex=index;
		}
		
		long sum=0;
		for (int i = 1; i < bufferSize.size(); i++) {
			if (bufferSize.get(i) > maxBufferSize) {
				maxBufferSize = bufferSize.get(i);
			}
			sum += bufferSize.get(i - 1)
					* (changeTime.get(i) - changeTime.get(i - 1));
		
		}
		long period=changeTime.get(changeTime.size() - 1);
		if(period==0)
			avgBufferSize=0;
		else
			avgBufferSize = 1.0 * sum /period ;
		}
		changeTime.clear();
		//changeTime=null;
		bufferSize.clear();
		//bufferSize=null;
		
	}


}
