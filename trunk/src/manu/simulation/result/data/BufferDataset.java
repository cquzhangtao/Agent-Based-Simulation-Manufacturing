package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class BufferDataset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1815982809537937292L;
	private ArrayList<BufferData> bufferDataset=new ArrayList<BufferData>();
	public BufferData allBufferData=new BufferData("All");


	public void reset(){
		allBufferData.reset();
		for(int i=0;i<bufferDataset.size();i++)
		{
			bufferDataset.get(i).reset();
		}
	}
	
	public void stat(){
		double sum=0;
		int minSize=999999999;
		int maxBufferSize=0;
		for(int i=0;i<bufferDataset.size();i++)
		{
			bufferDataset.get(i).stat();
			sum+=bufferDataset.get(i).avgBufferSize;
			if(maxBufferSize<bufferDataset.get(i).maxBufferSize){
				maxBufferSize=bufferDataset.get(i).maxBufferSize;
			}
			if(bufferDataset.get(i).bufferSizebyDay.size()<minSize)
				minSize=bufferDataset.get(i).bufferSizebyDay.size();
		}
		allBufferData.avgBufferSize=1.0*sum/bufferDataset.size();
		allBufferData.maxBufferSize=maxBufferSize;
		
		for(int i=0;i<minSize;i++){
			sum=0;
			for(int j=0;j<bufferDataset.size();j++){
				sum+=bufferDataset.get(j).bufferSizebyDay.get(i);
			}
			allBufferData.bufferSizebyDay.add(sum);
		}
		
	}
	public void add(BufferData data)
	{
		bufferDataset.add(data);
	}
	public BufferData get(int index){
		return bufferDataset.get(index);
	}
	public int size(){
		return bufferDataset.size();
	}
	public BufferData getBufferData(String name){
		for(int i=0;i<bufferDataset.size();i++){
			if(bufferDataset.get(i).bufferName.equals(name))
				return bufferDataset.get(i);
		}
		return null;
	}
	public void save(){
		for(BufferData td:bufferDataset)
			td.save();
	}
	public void recover(){
		for(BufferData td:bufferDataset)
			td.recover();
	}
}
