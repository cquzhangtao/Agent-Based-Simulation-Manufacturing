package manu.sbo.algorithm.antcolony;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private int id;
	private List<Node> successors;
	private List<Node> predecessors;
	private int value;
	
	public Node(){
		successors=new ArrayList<Node>();
		predecessors=new ArrayList<Node>();
	}
	
	public void addPredecessor(Node node){
		predecessors.add(node);
	}
	public void addSuccessor(Node node){
		successors.add(node);
	}

	public List<Node> getSuccessors() {
		return successors;
	}

	public List<Node> getPredecessors() {
		return predecessors;
	}

	public void setSuccessors(List<Node> successors) {
		this.successors = successors;
	}

	public void setPredecessors(List<Node> predecessors) {
		this.predecessors = predecessors;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
