//package manu.model.feature;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import manu.agent.ToolGroup;
//import manu.model.component.Product;
//import manu.utli.Wheel;
//import simulation.framework.Message;
//
//public class Bottleneck1 implements Comparable<Bottleneck1>{
//
//	public static int levelNum = 0;
//	public static List<Bottleneck1> bottelnecks = new ArrayList<Bottleneck1>();
//	public String highLevelBottleneck;
//	public String lowLevelBottleneck;
//	public int bottleneckLevel = 0;
//	public boolean isBottleneck = false;
//	public int bottleneckAimBuffer = 5;
//	public boolean isBottleneckReleasePolicy = false;
//	public boolean hasGotControl = false;
//	public boolean ishandOverControltoLowLevel = false;
//	public boolean isStarting = false;
//	private List<Product> controledProducts;
//	private List<Double> controledProductRatio;
//	private String toolGroupName;
//	private double workLoad;
//
//	private int totalFinishedNum = 0;
//	Map<Product,List<Bottleneck1>> m=new HashMap<Product,List<Bottleneck1>>();
//
//	public Bottleneck1() {
//		controledProducts = new ArrayList<Product>();
//		controledProductRatio= new ArrayList<Double>();
//
//	}
//
//	public void addControlledProduct(Product p) {
//		if (!controledProducts.contains(p)){
//			controledProducts.add(p);
//			controledProductRatio.add(1/p.getReleaseIntervalTimeR().getParameter("mean"));
//		}
//	}
//
//	public int getWIPbefore(ToolGroup tool) {
//		tool.send(tool.release, "Released Num", "");
//		Message msg = tool.receive("Released Num");
//		while (msg == null) {
//			msg = tool.receive("Released Num");
//		}
//		return Integer.valueOf(msg.getContent()) - totalFinishedNum;
//
//	}
//
//	public boolean needControl(ToolGroup tool) {
//		// return getWIPbefore(tool)>bottleneckAimBuffer;
//		return tool.bufferInfo.size() > bottleneckAimBuffer;
//	}
//
//	public void finishedOneJob() {
//		totalFinishedNum++;
//	}
//
//	public void handOverControltoHighLevel(ToolGroup tool) {
//		if (hasGotControl) {
//
//			System.out.println("Level --" + bottleneckLevel + "--" + tool.name
//					+ " hands over control to high level "
//					+ highLevelBottleneck);
//			tool.send(highLevelBottleneck, "Bottleneck Control", "LowLevelStop");
//			Message msg = tool.receive("BottleneckConfirm");
//			while (msg == null || !msg.getSender().equals(highLevelBottleneck)) {
//				msg = tool.receive("BottleneckConfirm");
//			}
//			hasGotControl = false;
//
//		}
//
//	}
//
//	public void handOverControltoLowLevel(ToolGroup tool) {
//		if (!isStarting && isFirstLevel()) {
//			tool.send(lowLevelBottleneck, "BottleneckConfirm", "Refuse");
//			System.out.println("Level --" + bottleneckLevel + "--" + tool.name
//					+ " refuses the request from low level "
//					+ lowLevelBottleneck);
//			return;
//		}
//		if (!hasGotControl) {
//			String str = requestControlFromHighLevel(tool);
//			if (str.equals("Accept")) {
//				hasGotControl = false;
//				ishandOverControltoLowLevel = true;
//				System.out.println("Level --" + bottleneckLevel + "--"
//						+ tool.name + " hands over control to low level "
//						+ lowLevelBottleneck);
//				System.out.println("Current control level: ----"
//						+ (bottleneckLevel + 1) + "----");
//				tool.send(lowLevelBottleneck, "BottleneckConfirm", "Accept");
//			} else {
//				tool.send(lowLevelBottleneck, "BottleneckConfirm", "Refuse");
//				System.out.println("Level --" + bottleneckLevel + "--"
//						+ tool.name + " refuses the request from low level "
//						+ lowLevelBottleneck);
//				return;
//			}
//		} else {
//			// if(tool.bufferInfo.size()==this.bottleneckAimBuffer){
//			hasGotControl = false;
//			ishandOverControltoLowLevel = true;
//			System.out.println("Level --" + bottleneckLevel + "--" + tool.name
//					+ " hands over control to low level " + lowLevelBottleneck);
//			System.out.println("Current control level: ----"
//					+ (bottleneckLevel + 1) + "----");
//
//			tool.send(lowLevelBottleneck, "BottleneckConfirm", "Accept");
//			// }else{
//			// tool.send(lowLevelBottleneck, "BottleneckConfirm", "Refuse");
//			// }
//
//		}
//
//	}
//
//	public String requestControlFromHighLevel(ToolGroup tool) {
//		if (hasGotControl) {
//			return "Refuse";
//		}
//
//		if (isFirstLevel() && isStarting) {
//			return "Refuse";
//		}
//
//		if (!ishandOverControltoLowLevel) {
//			System.out
//					.println("Level --" + bottleneckLevel + "--" + tool.name
//							+ " request control from high level "
//							+ highLevelBottleneck);
//			if (isFirstLevel()) {
//				String s = "";
//				for (Product p : controledProducts) {
//					s += p.getIndex() + "-";
//				}
//				tool.send(highLevelBottleneck, "Bottleneck Control", s);
//			} else {
//				tool.send(highLevelBottleneck, "Bottleneck Control",
//						"LowLevelStart");
//			}
//			Message msg = tool.receive("BottleneckConfirm");
//			while (msg == null || !msg.getSender().equals(highLevelBottleneck)) {
//				msg = tool.receive("BottleneckConfirm");
//			}
//			if (msg.getContent().equals("Accept")) {
//				hasGotControl = true;
//				isStarting = true;
//			}
//			return msg.getContent();
//
//		} else {
//			return "Refuse";
//		}
//	}
//
//	public void acceptControlFromLowLevel(ToolGroup tool) {
//		hasGotControl = true;
//		ishandOverControltoLowLevel = false;
//		System.out.println("Level --" + bottleneckLevel + "--" + tool.name
//				+ " accepts control from low level " + lowLevelBottleneck);
//		System.out.println("Current control level: ----" + bottleneckLevel
//				+ "----");
//		if (tool.bufferInfo.size() <= bottleneckAimBuffer) {
//			// if (!needControl(tool)) {
//			if (!isFirstLevel()) {
//				handOverControltoHighLevel(tool);
//			} else {
//				// tool.send(tool.release, "Job Release", String.valueOf(1));
//				releaseJob(tool);
//			}
//		}
//
//		tool.send(lowLevelBottleneck, "BottleneckConfirm", "");
//	}
//
//	public void releaseJob(ToolGroup tool) {
//		if(controledProducts.size()==0)
//			return;
//		int productIndex = 0;
//		double[] d = new double[controledProducts.size()];
//		
//		for (int index = 0;index<controledProducts.size();index++) {
//			d[index] = controledProductRatio.get(index);
//		}
//		int index = Wheel.getIndex(d);
//		Random rnd=new Random();
//		if(rnd.nextDouble()<1.0/m.get(controledProducts.get(index)).size()*workLoad
//				/*1.0/m.get(controledProducts.get(index)).indexOf(this)*/)
//		{
//			productIndex = controledProducts.get(index).getIndex();
//			tool.send(tool.release, "Job Release", String.valueOf(productIndex));
//		}
//	
//	}
//
//	public boolean isBottleneckControl() {
//		return /* isBottleneck && */isBottleneckReleasePolicy;
//	}
//
//	public boolean isFirstLevel() {
//		return bottleneckLevel == 1;
//	}
//
//	public void reset() {
//		hasGotControl = false;
//		ishandOverControltoLowLevel = false;
//	}
//
//	public void dealwithRequestFromLowLevel(ToolGroup tool, int rBufferSize,String rootTool) {
//		int bufferSize = tool.bufferInfo.size();
//
//		if (hasGotControl) {
//			String str;
//			if (rBufferSize > bottleneckAimBuffer * 1.8
//					&& bufferSize - bottleneckAimBuffer < bottleneckAimBuffer * 0.5) {
//				str = "Accept";
//				hasGotControl = false;
//				ishandOverControltoLowLevel = true;
//			} else {
//
//				if (bufferSize > bottleneckAimBuffer) {
//					str = "Refuse";
//				} else if (bufferSize < bottleneckAimBuffer) {
//					str = "Refuse";
//				} else {
//					str = "Accept";
//					hasGotControl = false;
//					ishandOverControltoLowLevel = true;
//				}
//			}
//			tool.send(lowLevelBottleneck, "BottleneckConfirm", str);
//			System.out.println(tool.name+" "+tool.bufferInfo.size()+", "+rootTool+" "+rBufferSize);
//			return;
//		} else {
//			boolean response = requestControlFromHighLevel1(tool,rBufferSize,rootTool);
//			String str;
//			if (response) {
//				str = "Accept";
//				ishandOverControltoLowLevel = true;
//			} else {
//				str = "Refuse";
//			}
//			tool.send(lowLevelBottleneck, "BottleneckConfirm", str);
//		}
//	}
//
//	public void dealwithRequestFromHighLevel(ToolGroup tool, int rBufferSize,String rootTool) {
//		int bufferSize = tool.bufferInfo.size();
//		if (hasGotControl) {
//			String str;
//			if (bufferSize > bottleneckAimBuffer * 1.8
//					&& rBufferSize - bottleneckAimBuffer < bottleneckAimBuffer * 0.5) {
//				str = "Refuse";
//			} 
//			else {
//				if (bufferSize > bottleneckAimBuffer) {
//					str = "Accept";
//				} else if (bufferSize < bottleneckAimBuffer) {
//					str = "Accept";
//				} else {
//					str = "Accept";
//				}
//				hasGotControl = false;
//				ishandOverControltoLowLevel = false;
//			}
//			tool.send(highLevelBottleneck, "BottleneckConfirm", str);
//			System.out.println(tool.name+" "+tool.bufferInfo.size()+", "+rootTool+" "+rBufferSize);
//			return;
//		} else {
//			boolean response = requestControlFromLowLevel(tool,rBufferSize,rootTool);
//			String str;
//			if (response) {
//				str = "Accept";
//				ishandOverControltoLowLevel = false;
//			} else {
//				str = "Refuse";
//			}
//			tool.send(highLevelBottleneck, "BottleneckConfirm", str);
//		}
//	}
//
//	public boolean requestControlFromLowLevel(ToolGroup tool,int bs,String rootTool) {
//		if (isLastLevel()) {
//			return false;
//		}
//
//		System.out.println("Level--" + bottleneckLevel + "--" + tool.name
//				+ " request control from low level " + lowLevelBottleneck);
//
//		tool.send(lowLevelBottleneck, "Bottleneck Control", "FromHighLevel-"+bs+"-"+rootTool);
//
//		Message msg = tool.receive("BottleneckConfirm");
//		while (msg == null || !msg.getSender().equals(lowLevelBottleneck)) {
//			msg = tool.receive("BottleneckConfirm");
//		}
//		System.out.println(" ," + msg.getContent());
//		if (msg.getContent().equals("Accept")) {
//			return true;
//		}
//		return false;
//
//	}
//
//	public boolean requestControlFromHighLevel1(ToolGroup tool,int bs,String rootTool) {
//		if (isFirstLevel() && !isStarting) {
//			return false;
//		}
//		System.out.println("Level--" + bottleneckLevel + "--" + tool.name
//				+ " request control from high level " + highLevelBottleneck);
//
//		if (isFirstLevel()) {
//			String s = "";
//			for (Product p : controledProducts) {
//				s += p.getIndex() + "-";
//			}
//			tool.send(highLevelBottleneck, "Bottleneck Control", s);
//		} else {
//			tool.send(highLevelBottleneck, "Bottleneck Control", "FromLowLevel-"+bs+"-"+rootTool);
//		}
//
//		Message msg = tool.receive("BottleneckConfirm");
//		while (msg == null || !msg.getSender().equals(highLevelBottleneck)) {
//			msg = tool.receive("BottleneckConfirm");
//		}
//		System.out.println(" ," + msg.getContent());
//		if (msg.getContent().equals("Accept")) {
//
//			return true;
//		}
//		return false;
//	}
//
//	public void onBufferChanging(ToolGroup tool) {
//		int bufferSize = tool.bufferInfo.size();
//
//		if (bufferSize >= bottleneckAimBuffer && !isStarting) {
//			isStarting = true;
//			levelNum++;
//			bottleneckLevel = levelNum;
//			toolGroupName = tool.name;
//			if (bottleneckLevel < 2) {
//				highLevelBottleneck = tool.release;
//			} else {
//				highLevelBottleneck = bottelnecks.get(bottelnecks.size() - 1).toolGroupName;
//				bottelnecks.get(bottelnecks.size() - 1).lowLevelBottleneck = toolGroupName;
//			}
//			bottelnecks.add(this);
//			System.out.println("Find bottelneck " + tool.name + ", level "
//					+ bottleneckLevel);
//		}
//		if (!isStarting) {
//			return;
//		}
//		if (!hasGotControl) {
//			System.out
//					.println("*****************SSS****************************");
//			if (ishandOverControltoLowLevel) {
//				if (requestControlFromLowLevel(tool,tool.bufferInfo.size(),tool.name)) {
//					hasGotControl = true;
//					System.out.println("Current level: " + bottleneckLevel
//							+ ", tool group: " + toolGroupName);
//				} else {
//					// do nothing
//				}
//
//			} else {
//				if (requestControlFromHighLevel1(tool,tool.bufferInfo.size(),tool.name)) {
//					hasGotControl = true;
//					System.out.println("Current level: " + bottleneckLevel
//							+ ", tool group: " + toolGroupName);
//				} else {
//					// do nothing
//				}
//			}
//			System.out
//					.println("***************EEE******************************");
//		}
//		if (!hasGotControl) {
//			return;
//		}
//
//		ishandOverControltoLowLevel = false;
//		if (bufferSize > bottleneckAimBuffer) {
//			// do nothing
//		} else if (bufferSize < bottleneckAimBuffer) {
//			releaseJob(tool);
//		} else {
//			// do nothing
//		}
//
//	}
//
//	public boolean isLastLevel() {
//		return bottleneckLevel == levelNum;
//	}
//	
//	public void onBufferChanging1(ToolGroup tool) {
//		int bufferSize = tool.bufferInfo.size();
//
//		if (bufferSize >= bottleneckAimBuffer && !isStarting) {
//			isStarting = true;
//			levelNum++;
//			bottleneckLevel = levelNum;
//			bottelnecks.add(this);
//			updateBottelnecks();
//			System.out.println("Find bottelneck " + tool.name + ", level "
//					+ levelNum);
//			String s = "";
//			for (Product p : controledProducts) {
//				s += p.getIndex() + "-";
//			}
//			tool.send(tool.release, "Bottleneck Control", s);
//		}
//		if (!isStarting) {
//			return;
//		}
//		if (bufferSize > bottleneckAimBuffer) {
//			// do nothing
//		} else if (bufferSize < bottleneckAimBuffer) {
//			releaseJob(tool);
//		} else {
//			// do nothing
//		}
//	}
//	
//	public void updateBottelnecks(){
//	
//		for(Bottleneck1 b:bottelnecks){
//			for(Product p:b.controledProducts){
//				if(!m.containsKey(p)){
//					m.put(p, new ArrayList<Bottleneck1>());
//				}
//				m.get(p).add(b);
//			}
//		}
//		for(Bottleneck1 b:bottelnecks){
//			int index=0;
//			for(Product p:b.controledProducts){
//				controledProductRatio.add(index,1/p.getReleaseIntervalTimeR().getParameter("mean")/*/m.get(p).size()*/);
//				index++;
//			}
//		}
//	}
//	public int jc(int d){
//		int sum=0;
//		for(int i=0;i<=d;i++){
//			sum+=i;
//		}
//		return sum;
//	}
//
//	public double getWorkLoad() {
//		return workLoad;
//	}
//
//	public void setWorkLoad(double workLoad) {
//		this.workLoad = workLoad;
//	}
//
//	@Override
//	public int compareTo(Bottleneck1 o) {
//		// TODO Auto-generated method stub
//		return Double.compare(workLoad, o.workLoad);
//	}
//	
//	public String toString(){
//		String srt = toolGroupName+", "+"Work load ratio: " + workLoad + ", products: ";
//		for (Product s : controledProducts) {
//			srt += " " + s.getName() + ", ";
//		}
//
//		return srt;
//	}
//
//	public String getToolGroupName() {
//		return toolGroupName;
//	}
//
//	public void setToolGroupName(String toolGroupName) {
//		this.toolGroupName = toolGroupName;
//	}
//
//}
