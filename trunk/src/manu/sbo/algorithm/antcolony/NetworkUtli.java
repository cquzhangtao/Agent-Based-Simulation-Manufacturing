package manu.sbo.algorithm.antcolony;

public class NetworkUtli {
	
	public static Network createNetwork(int colNum) {
		Network network=new Network();
		Node start=new Node();
		start.setId(0);
		start.setValue(0);
		network.setStart(start);
		network.addNode(start);
		
		Node end=new Node();
		end.setId((colNum+1)*10);
		end.setValue(0);
		network.setEnd(end);
		network.addNode(end);
		for(int i=1;i<colNum+1;i++){			
			for(int j=0;j<10;j++){
				Node node=new Node();
				node.setId(i*10+j);
				node.setValue(j);
				network.addNode(node);
			}
		}
		for(Node node:network.getNodes().values()){
			if(node==start){
				
			}else if(node.getId()<20){
				node.addPredecessor(start);
				network.addSide(start, node);
			}else{
				for(int i=0;i<10;i++){
					Node st=network.getNode((node.getId()/10-1)*10+i);
					node.addPredecessor(st);
					network.addSide(st, node);
				}
			}
			
			if(node==end){
				
			}else if(node.getId()>=colNum*10){
				node.addSuccessor(end);
				network.addSide(node, end);
			}else{
				for(int i=0;i<10;i++){
					Node en=network.getNode((node.getId()/10+1)*10+i);
					node.addSuccessor(en);
					network.addSide(node, en);
				}
			}

		}
		return network;
	}
}
