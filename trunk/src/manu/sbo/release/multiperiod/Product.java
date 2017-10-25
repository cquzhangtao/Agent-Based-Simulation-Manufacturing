package manu.sbo.release.multiperiod;

import java.util.ArrayList;
import java.util.List;

public class Product {
	private List<TimeSlice> timeSlice;
	private List<Long> timeSliceIndex;
	private long dueDate;
	private long earliesStartDate;
	private long minReleaseInterval;
	private int jobNum;
	private long startDate;
	private int id;
	public Product(long dueDate,long earliestStartDate,long minReleaseInterval,int jobNum, int i){
		timeSlice=new ArrayList<TimeSlice>();
		timeSliceIndex=new ArrayList<Long>();
		this.dueDate=dueDate;
		this.earliesStartDate=earliestStartDate;
		this.setMinReleaseInterval(minReleaseInterval);
		this.jobNum=jobNum;
		startDate=dueDate-jobNum*minReleaseInterval*60*1000;;
		setId(i);
	}
	
	public int getArrangedJobNumNoLastSlice(){
		int arrangedJobNum=0;
		for(TimeSlice ts:timeSlice){
			if(timeSlice.indexOf(ts)!=timeSlice.size()-1)
				arrangedJobNum+=ts.getArrangedJobNum();
		}
		return arrangedJobNum;
	}
	
	public int getArrangedJobNum(){
		int arrangedJobNum=0;
		for(TimeSlice ts:timeSlice){
			
				arrangedJobNum+=ts.getArrangedJobNum();
		}
		return arrangedJobNum;
	}
	public void updateStartTime(long currentTime,long preTime){
		TimeSlice ts = getTimeSlice(currentTime);
		TimeSlice pre = getTimeSlice(preTime);
		if(ts.getType()==1||ts.getType()==2){
			startDate-=(1.0*ts.getDuration()/minReleaseInterval-ts.getArrangedJobNum())*minReleaseInterval*60*1000;
		}
		if(pre!=null&&pre.getType()==3){
			startDate-=(1.0*pre.getDuration()/minReleaseInterval-pre.getArrangedJobNum())*minReleaseInterval*60*1000;
		}
	}
	
	public double getMaxReleaseInterval(long preTime){
		int an=getArrangedJobNumNoLastSlice();
		if(an<jobNum)
			return (preTime-earliesStartDate)/1000.0/60.0/(jobNum-an);
		else
			return minReleaseInterval*10;
	}
	
	public void addSlice(long start,long end,int type,long currentTime){
		timeSlice.add(new TimeSlice(minReleaseInterval,start,end,type));
		timeSliceIndex.add(currentTime);
	}
	public TimeSlice getTimeSlice(long curTime){
		int index= timeSliceIndex.indexOf(curTime);
		if(index==-1)
			return null;
		return timeSlice.get(index);
		
	}
	public List<TimeSlice> getTimeSlice() {
		return timeSlice;
	}
	public long getDueDate() {
		return dueDate;
	}
	public long getEarliesStartDate() {
		return earliesStartDate;
	}

	public int getJobNum() {
		return jobNum;
	}
	public void setTimeSlice(List<TimeSlice> timeSlice) {
		this.timeSlice = timeSlice;
	}
	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}
	public void setEarliesStartDate(long earliesStartDate) {
		this.earliesStartDate = earliesStartDate;
	}

	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getStartDate() {
		return startDate;
	}

	public void setMinReleaseInterval(long minReleaseInterval) {
		this.minReleaseInterval = minReleaseInterval;
	}

	public long getMinReleaseInterval() {
		return minReleaseInterval;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public List<Long> getTimeSliceIndex() {
		return timeSliceIndex;
	}

	public void setTimeSliceIndex(List<Long> timeSliceIndex) {
		this.timeSliceIndex = timeSliceIndex;
	}
	
}
