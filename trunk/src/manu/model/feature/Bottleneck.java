//package manu.model.feature;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import org.exolab.castor.dsml.Producer;
//
//import manu.agent.ToolGroup;
//import manu.model.component.Product;
//import manu.model.componentnew.ProductNew;
//import manu.utli.Wheel;
//import simulation.framework.Message;
//
//public class Bottleneck implements Comparable<Bottleneck> {
//
//	private static int levelNum = 0;
//	private static List<Bottleneck> bottelnecks = new ArrayList<Bottleneck>();
//	private int bottleneckAimBuffer = 10;
//	private boolean isBottleneckReleasePolicy = false;
//	private boolean isStarting = false;
//	private List<ProductNew> controledProducts;
//	private List<Double> controledProductRatio;
//	private String toolGroupName;
//	private double workLoad;
//	private Producer[] products;
//
//	private Map<Integer, Integer> totalFinishedNum = new HashMap<Integer, Integer>();
//	private Map<ProductNew, List<Bottleneck>> m = new HashMap<Product, List<Bottleneck>>();
//	private int bottleneckLevel = 0;
//
//	public Bottleneck() {
//		controledProducts = new ArrayList<Product>();
//		controledProductRatio = new ArrayList<Double>();
//
//	}
//
//	public void addControlledProduct(Product p) {
//		if (!controledProducts.contains(p)) {
//			controledProducts.add(p);
//			controledProductRatio.add(1 / p.getReleaseIntervalTimeR()
//					.getParameter("mean"));
//			totalFinishedNum.put(p.getIndex(), 0);
//		}
//	}
//
//	public int[] getWIPbefore(ToolGroup tool) {
//		String str = "";
//		for (Product p : controledProducts) {
//			str += p.getIndex() + "-";
//		}
//		tool.send(tool.getRelease(), "Released Num", str);
//		Message msg = tool.receive("Released Num");
//		while (msg == null) {
//			msg = tool.receive("Released Num");
//		}
//		String s[] = msg.getContent().split("-");
//
//		int[] result = new int[controledProducts.size()];
//		for (int i = 0; i < result.length; i++) {
//			result[i] = Integer.valueOf(s[i]) - totalFinishedNum.get(controledProducts.get(i).getIndex());
//		}
//
//		return result;
//
//	}
//	private int getTotalWIPbefore(ToolGroup tool){
//		int[] wip = getWIPbefore(tool);
//		int sum=0;
//		for(int n:wip){
//			sum+=n;
//		}
//		return sum;
//	}
//
//	public void finishedOneJob(int productIndex) {
//		if(isBottleneckReleasePolicy)
//		totalFinishedNum.put(productIndex,
//				totalFinishedNum.get(productIndex) + 1);
//	}
//	
//	public void calculateAimBufferSize(){
//		int aimbuffer=0;
//		for(Product p:controledProducts){
//			int step=0;
//			for(manu.model.component.Process process:p.getProcessFlow()){
//				step++;
//				if(process.getToolGroupName().equals(toolGroupName)){
//					break;
//				}
//			}
//			aimbuffer+=step;
//			
//		}
//		if(aimbuffer<5){
//			aimbuffer=5;
//		}
//		bottleneckAimBuffer=20;
//	}
//
//	public void releaseJob(ToolGroup tool) {
//		double[] d1 = new double[controledProducts.size()];
//		for (int i = 0; i < d1.length; i++) {
//			d1[i] = controledProductRatio.get(i);
//		}
//		int index = Wheel.getIndex(d1);
//		int productIndex = controledProducts.get(index).getIndex();
//		Random rnd = new Random();
//		int n=getBotnecNumControllingProduct(productIndex);
//		if (rnd.nextDouble() < 1/(getBotnecLevelForProduct(productIndex)+1))
//		{
//				/// getBotnecNumControllingProduct(productIndex) * 1.0
//				/// getBotnecLevelForProduct(productIndex)) {
//
//			tool.send(tool.getRelease(), "Job Release", String.valueOf(productIndex));
//		}
//	}
//
//	public void releaseJob1(ToolGroup tool) {
//
//		double[] d = new double[products.length];
//		for (int i = 0; i < d.length; i++) {
//			d[i] = 1 / products[i].getReleaseIntervalTimeR().getParameter("mean");
//		}
//		int productIndex = Wheel.getIndex(d);
//		if (controledProducts.contains(products[productIndex])) {
//			Random rnd = new Random();
//			if (rnd.nextDouble() < 1.0
//					/ getBotnecNumControllingProduct(productIndex) * 1.0
//					/ getBotnecLevelForProduct(productIndex)) {
//
//				tool.send(tool.getRelease(), "Job Release",
//						String.valueOf(productIndex));
//			}
//		} else {
//			if (tool.getBufferInfo().size() == 0) {
//				releaseJob(tool);
//			}
//		}
//
//	}
//
//	private int getBotnecNumControllingProduct(int index) {
//		return m.get(products[index]).size();
//	}
//
//	private int getBotnecLevelForProduct(int index) {
//		return m.get(products[index]).indexOf(this);
//	}
//
//	public boolean isBottleneckControl() {
//		return isBottleneckReleasePolicy;
//	}
//
//	public void reset() {
//
//	}
//
//	public void onBufferChanging1(ToolGroup tool) {
//		//int bufferSize = getTotalWIPbefore(tool);//
//		int bufferSize=tool.getBufferInfo().size();
//
//		if (bufferSize >= bottleneckAimBuffer && !isStarting) {
//			isStarting = true;
//			levelNum++;
//			bottleneckLevel = levelNum;
//			bottelnecks.add(this);
//			updateBottelnecks();
//			System.out.println("Find bottelneck " + tool.getName() + ", level "
//					+ levelNum);
//			String s = "";
//			for (Product p : controledProducts) {
//				s += p.getIndex() + "-";
//			}
//			tool.send(tool.getRelease(), "Bottleneck Control", s);
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
//	public void updateBottelnecks() {
//
//		for (Bottleneck b : bottelnecks) {
//			for (Product p : b.controledProducts) {
//				if (!m.containsKey(p)) {
//					m.put(p, new ArrayList<Bottleneck>());
//				}
//				m.get(p).add(b);
//			}
//		}
//		for (Bottleneck b : bottelnecks) {
//			int index = 0;
//			for (Product p : b.controledProducts) {
//				controledProductRatio.add(index,
//						1 / p.getReleaseIntervalTimeR().getParameter("mean"));
//				index++;
//			}
//		}
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
//	public int compareTo(Bottleneck o) {
//		// TODO Auto-generated method stub
//		return Double.compare(workLoad, o.workLoad);
//	}
//
//	public String toString() {
//		String srt = toolGroupName + ", " + "Work load ratio: " + workLoad
//				+ ", products: ";
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
//	public boolean isBottleneckReleasePolicy() {
//		return isBottleneckReleasePolicy;
//	}
//
//	public void setBottleneckReleasePolicy(boolean isBottleneckReleasePolicy) {
//		this.isBottleneckReleasePolicy = isBottleneckReleasePolicy;
//	}
//
//	public Product[] getProducts() {
//		return products;
//	}
//
//	public void setProducts(Product[] products) {
//		this.products = products;
//	}
//
//}
