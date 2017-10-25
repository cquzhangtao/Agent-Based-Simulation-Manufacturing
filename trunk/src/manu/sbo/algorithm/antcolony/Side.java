package manu.sbo.algorithm.antcolony;

public class Side {
	private int id;
	private Node start;
	private Node end;
	private double wieght=0.1;
	public Node getStart() {
		return start;
	}
	public Node getEnd() {
		return end;
	}
	public double getWieght() {
		return wieght;
	}
	public void setStart(Node start) {
		this.start = start;
	}
	public void setEnd(Node end) {
		this.end = end;
	}
	public void setWieght(double wieght) {
		this.wieght = wieght;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
