package manu.sbo.algorithm.antcolony;

import java.util.HashMap;
import java.util.Map;

public class Network {
	private Map<Integer,Node> nodes;
	private Map<String,Side> sides;
	private Node start;
	private Node end;
	
	public Network(){
		nodes=new HashMap<Integer,Node>();
		sides=new HashMap<String,Side>();
	}
	
	public void addNode(Node node){
		nodes.put(node.getId(), node);
	}
	
	public void addSide(Side side){
		sides.put(side.getStart().getId()+"-"+side.getEnd().getId(), side);
	}
	
	public void addSide(Node start,Node end){

		Side side=new Side();
		side.setEnd(end);
		side.setStart(start);
		addSide(side);
	}
	
	public Node getNode(int id){
		return nodes.get(id);
	}
	public Side getSide(Node start,Node end){
		return sides.get(start.getId()+"-"+end.getId());
	}

	public Node getStart() {
		return start;
	}

	public void setStart(Node start) {
		this.start = start;
	}

	public void setEnd(Node end) {
		this.end = end;
	}

	public Node getEnd() {
		return end;
	}

	public Map<String, Side> getSides() {
		return sides;
	}

	public void setSides(Map<String, Side> sides) {
		this.sides = sides;
	}

	public Map<Integer, Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<Integer, Node> nodes) {
		this.nodes = nodes;
	}

}
