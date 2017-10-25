package manu.sbo.algorithm.antcolony;

import java.util.ArrayList;
import java.util.List;


public class Ant {
	private List<Node> path;
	private double value;
	public Ant(){
		path=new ArrayList<Node>();
	}
	public void run(Network network){
		Node curNode=network.getStart();
		path.add(curNode);
		while(curNode!=network.getEnd()){
			Node preNode=curNode;
			curNode=route(network,curNode);
			path.add(curNode);
			updateSideWeight(network.getSide(preNode,curNode));
		}

	}
	
	private Node route(Network network,Node curNode){
		double[] weight=new double[curNode.getSuccessors().size()];
		for(Node nextNode:curNode.getSuccessors()){
			Side side=network.getSide(curNode, nextNode);
			weight[curNode.getSuccessors().indexOf(nextNode)]=side.getWieght();
		}
		int index=Wheel.getIndex(weight);		
		return curNode.getSuccessors().get(index);
	}
	private void updateSideWeight(Side side){
		
	}
	public List<Node> getPath() {
		return path;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}


}
