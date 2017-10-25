package manu.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import manu.model.component.Process;
import manu.model.component.ProcessingTimePool;
import manu.model.component.Product;
import manu.model.component.ReleasePolicy;
import manu.model.component.Skill;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolGroupPoolInfo;
import manu.model.component.TransportTimePool;
import manu.scheduling.DecisionMaker;
import manu.scheduling.training.data.ReleaseTrainingData;
import manu.scheduling.training.data.RoutingTrainingData;
import manu.scheduling.training.data.SequencingTrainingData;
import manu.simulation.others.FXModelTransfer;
import manu.simulation.others.FileRead;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ManufactureModel {
	private ArrayList<ToolGroupInfo> toolGroups = new ArrayList<ToolGroupInfo>();
	private Product[] products;
	private ReleasePolicy policy = new ReleasePolicy();
	private Map<String, ToolGroupPoolInfo> toolGroupPoolInfos = new HashMap<String, ToolGroupPoolInfo>();
	private String modelName;
	private String modelFilePath;
	private String resultFilePath;
	private TransportTimePool transportTimePool = new TransportTimePool();
	private ProcessingTimePool processingTimePool = new ProcessingTimePool();
	private Map<String, DecisionMaker<SequencingTrainingData>> dispatchers;
	private Map<String, DecisionMaker<RoutingTrainingData>> routers;
	private Map<String, DecisionMaker<ReleaseTrainingData>> releasers;

	public int loadModel(File file) {
		setModelName(file.getName().substring(0, file.getName().lastIndexOf(".")));
		setModelFilePath(file.getParent() + "\\");
		setResultFilePath(getModelFilePath() + getModelName() + "\\");
		if (file.getPath().substring(file.getPath().length() - 4).equalsIgnoreCase(".xls")) {
			FXModelTransfer transfer = new FXModelTransfer(file.getName(), file.getParent() + "\\");
			// TODO please update the transfer
			int r = transfer.transfer();
			if (r == 1) {
				JOptionPane.showMessageDialog(null, "Read FX model sucessfully!");

				int b = generateModel(new File(file.getParent() + "\\" + getModelName() + ".mod"));
				return b;

			} else {
				JOptionPane.showMessageDialog(null, "Read FX model error!");
				return 0;
			}

		} else {
			return generateModel(file);
		}

	}

	public int generateModel(File file) {
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(file);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		Element root = doc.getRootElement();
		String toolGroupXML = file.getParent() + "//" + root.elementText("ToolGroup");
		String processXML = file.getParent() + "//" + root.elementText("Process");
		String productXML = file.getParent() + "//" + root.elementText("Product");
		if (root.elementText("Dispatcher") != null) {
			//String dispatcherFile = root.elementText("Dispatcher");
			//dispatchers = FileRead.readObject(dispatcherFile);
		}
		if (root.elementText("Router") != null) {
			String routerFile = root.elementText("Router");
			routers = FileRead.readObject(routerFile);
		}

		if (root.elementText("Releaser") != null) {
			String routerFile = root.elementText("Releaser");
			releasers = FileRead.readObject(routerFile);
		}

		String r = FileRead.GetToolGroup(toolGroupXML, toolGroups);
		if (!r.equals("OK")) {
			JOptionPane.showMessageDialog(null, r);
			System.out.println(r);
			return 0;
		}
		ArrayList<Product> p = new ArrayList<Product>();
		r = FileRead.getProducts(processXML, productXML, p, getPolicy());
		if (!r.equals("OK")) {
			JOptionPane.showMessageDialog(null, r);
			System.out.println(r);
			return 0;
		}

		String timeXls = file.getParent() + "//" + root.elementText("Times");

		FileRead.getProcessingTimes(timeXls, processingTimePool);
		FileRead.getTransportTimes(timeXls, transportTimePool);

		products = p.toArray(new Product[0]);
		Arrays.sort(products);
		for (int i = 0; i < products.length; i++) {
			products[i].setIndex(i);
			products[i].setProcessingTimePool(processingTimePool);
			products[i].setTransportTimePool(transportTimePool);
		}
		ArrayList<String> toolGroupNames = new ArrayList<String>();
		for (ToolGroupInfo t : toolGroups) {
			t.setAllToolGroupName(toolGroupNames);
			toolGroupNames.add(t.getToolGroupName());
			
			if(dispatchers!=null){
				t.setDecisionMaker(dispatchers.get(t.getToolGroupName()));
			}
			else{
				DecisionMaker<SequencingTrainingData> dm = new DecisionMaker<SequencingTrainingData>(t.getToolGroupName());
				//dm.randomInit();
				t.setDecisionMaker(dm);
			}

			FileRead.getSetupTimes(timeXls, t.getToolGroupName(), t.getSetups());

			for (Skill skill : t.getSkills()) {
				if (!toolGroupPoolInfos.containsKey(skill.getName())) {
					toolGroupPoolInfos.put(skill.getName(), new ToolGroupPoolInfo());
				}
				toolGroupPoolInfos.get(skill.getName()).getToolGroups().add(t);

			}

		}
		for (Product productTemp : products) {
			if(releasers!=null)
			productTemp.setDecisionMaker(releasers.get(productTemp.getName()));

			for (Process process : productTemp.getProcessFlow()) {
				process.setToolGroups(toolGroupPoolInfos.get(process.getSkill()).getToolGroups());
			}
		}
		
		for(ToolGroupPoolInfo tgp:toolGroupPoolInfos.values()){
			if(routers!=null)
			tgp.setDecisionMaker(routers.get(tgp.getSkill().getName()));
		}

		return 1;
	}

	public void renew() {
		toolGroups.clear();
		products = null;
		setPolicy(new ReleasePolicy());
	}

	public ArrayList<ToolGroupInfo> getToolGroups() {
		return toolGroups;
	}

	public Product[] getProducts() {
		return products;
	}

	public ProcessingTimePool getProcessingTimePool() {
		return processingTimePool;
	}

	public void setProcessingTimePool(ProcessingTimePool processingTimePool) {
		this.processingTimePool = processingTimePool;
	}

	public TransportTimePool getTransportTimePool() {
		return transportTimePool;
	}

	public void setTransportTimePool(TransportTimePool transportTimePool) {
		this.transportTimePool = transportTimePool;
	}

	public Map<String, ToolGroupPoolInfo> getToolGroupBySkill() {
		return toolGroupPoolInfos;
	}

	public void setToolGroupBySkill(Map<String, ToolGroupPoolInfo> toolGroupBySkill) {
		this.toolGroupPoolInfos = toolGroupBySkill;
	}

	public String getResultFilePath() {
		return resultFilePath;
	}

	public void setResultFilePath(String resultFilePath) {
		this.resultFilePath = resultFilePath;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelFilePath() {
		return modelFilePath;
	}

	public void setModelFilePath(String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}

	public ReleasePolicy getPolicy() {
		return policy;
	}

	public void setPolicy(ReleasePolicy policy) {
		this.policy = policy;
	}

	public ToolGroupInfo getToolGroup(String toolGroupName) {
		for (ToolGroupInfo t : toolGroups) {
			if (t.getToolGroupName().equals(toolGroupName)) {
				return t;
			}
		}
		return null;
	}

}
