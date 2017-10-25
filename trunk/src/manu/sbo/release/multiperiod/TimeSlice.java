package manu.sbo.release.multiperiod;

public class TimeSlice {
	long start;
	long end;
	double releaseInterval;//minutes
	int type;
	
	
	public TimeSlice(double releaseInterval,long start,long end, int type){
		this.start=start;
		this.end=end;
		this.type=type;
		this.releaseInterval=releaseInterval;
	}
	
	public long getDuration(){
		return (long) ((end-start)/1000.0/60.0);
	}
	public double getArrangedJobNum(){
		return getDuration()/releaseInterval;
	}
	public long getStart() {
		return start;
	}
	public long getEnd() {
		return end;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getReleaseInterval() {
		return releaseInterval;
	}

	public void setReleaseInterval(double releaseInterval) {
		this.releaseInterval = releaseInterval;
	}
}
