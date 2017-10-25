package manu.model.analyze;

import java.util.HashSet;
import java.util.Set;

import manu.model.ManufactureModel;
import manu.simulation.result.SimulationResult;

public class BottelneckUtil {

	public static void init(ManufactureModel manuModel) {

		
//		for(ToolGroupInfo toolGroup:manuModel.getToolGroups()){
//			toolGroup.getBottleneck().setProducts(manuModel.getProducts());
//		}
//		
//		Set<Bottleneck> bottlenecks=new HashSet<Bottleneck>();
//		Product[] products = manuModel.getProducts();
//		for (Product product : products) {
//			List<Process> flow = product.getProcessFlow();
//			String bottelName = null;
//			double time = Double.MIN_VALUE;
//			for (Process process : flow) {
//				ToolGroupInfo toolGroup = manuModel.getToolGroup(process
//						.getToolGroupName().get(0));
//				double d = process.getProcessTime().get(0)
//						/ toolGroup.getToolNuminGroup();
//			
//				if (toolGroup.isBatchTool()) {
//					process.getBatchID().get(0);
//					toolGroup.getBatch(process.getBatchID().get(0));
//					d = d
//							/ toolGroup.getBatch(process.getBatchID().get(0))
//									.getBatchNum();
//				}
//				toolGroup.getBottleneck().addControlledProduct(product);
//				toolGroup.getBottleneck().setWorkLoad(1
//						/ product.getReleaseIntervalTimeR().getParameter("mean")
//						* d + toolGroup.getBottleneck().getWorkLoad());
//				toolGroup.getBottleneck().setToolGroupName(toolGroup.getToolGroupName());
//				bottlenecks.add(toolGroup.getBottleneck());
//				
//				
////				if (top.containsKey(toolGroup.getToolGroupName())) {
////					A a = top.get(toolGroup.getToolGroupName());
////					a.addProduct(product.name);
////					a.setWorkLoad(1
////							/ product.releaseIntervalTimeR.getParameter("mean")
////							* d + a.getWorkLoad());
////
////				} else {
////					A a = new A();
////					a.setToolGroupName(toolGroup.getToolGroupName());
////					a.setWorkLoad(1
////							/ product.releaseIntervalTimeR.getParameter("mean")
////							* d);
////					a.addProduct(product.name);
////					top.put(toolGroup.getToolGroupName(), a);
////
////				}
//				if (d > time) {
//					time = d;
//					bottelName = toolGroup.getToolGroupName();
//				}
//
//			}
//			System.out
//					.println("Current tools can produce one product "
//							+ product.getName() + "  in " + time
//							+ " minutes, the bottelneck is at tool group "
//							+ bottelName);
//		}
//
//		for(ToolGroupInfo tg:manuModel.getToolGroups()){
//			tg.getBottleneck().calculateAimBufferSize();
//		}
//		Bottleneck[] b=bottlenecks.toArray(new Bottleneck[1]);
//		Arrays.sort(b);
//		for(Bottleneck bn:b)
//			System.out.println(bn.toString());
//		

	}

//	public static void init(ManufactureModel manuModel) {
//		Product[] products = manuModel.getProducts();
//		for (Product product : products) {
//			List<Process> flow = product.getProcessFlow();
//			for (Process process : flow) {
//				ToolGroupInfo toolGroup = manuModel.getToolGroup(process
//						.getToolGroupName());
//				toolGroup.getBottleneck().addControlledProduct(product);
//			}
//		}
//	}

	public static void analyseBufferSize(ManufactureModel manuModel,
			SimulationResult results) {
		// TODO Auto-generated method stub

//		for (Product product : manuModel.getProducts()) {
//			System.out.print("Product " + product.getName());
//			for (Process process : product.getProcessFlow()) {
//
//				System.out.print(", "
//						+ process.getToolGroupName()
//						+ " "
//						+ (int) results.getBufferData(process
//								.getToolGroupName().get(0)).avgBufferSize);
//			}
//			System.out.println();
//		}

	}

}

class A implements Comparable<A> {
	String toolGroupName;
	double workLoad;
	Set<String> products;

	public A() {
		products = new HashSet<String>();
	}

	public void addProduct(String p) {
		products.add(p);
	}

	@Override
	public int compareTo(A o) {
		// TODO Auto-generated method stub
		if (o.workLoad > this.workLoad)
			return -1;
		else if (o.workLoad == this.workLoad)
			return 0;
		else
			return 1;
	}

	public String toString() {
		String srt = "Work load ratio: " + workLoad + ", products: ";
		for (String s : products) {
			srt += " " + s + ", ";
		}

		return srt;

	}

	public String getToolGroupName() {
		return toolGroupName;
	}

	public void setToolGroupName(String toolGroupName) {
		this.toolGroupName = toolGroupName;
	}

	public double getWorkLoad() {
		return workLoad;
	}

	public void setWorkLoad(double workLoad) {
		this.workLoad = workLoad;
	}

}
