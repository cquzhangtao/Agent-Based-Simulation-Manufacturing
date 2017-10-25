package manu.simulation.gui.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import manu.model.component.ReleasePolicy;
import manu.simulation.info.SimulationInfo;
import manu.simulation.others.CommonMethods;
import manu.simulation.result.data.AverageData;
import manu.simulation.result.data.BlockDataset;
import manu.simulation.result.data.BufferData;
import manu.simulation.result.data.BufferDataset;
import manu.simulation.result.data.ReleaseData;
import manu.simulation.result.data.ReleaseDataset;
import manu.simulation.result.data.ToolData;
import manu.simulation.result.data.ToolGroupDataset;
import manu.simulation.result.data.WaitDataset;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class ReportXmlData {
	public static void saveResult(ReleasePolicy[] policy,
			SimulationInfo[] simulatorInfo, ReleaseDataset[] releaseDataset,
			BufferDataset[] bufferDataset, ToolGroupDataset[] toolGroupDataset) {

		Document doc = DocumentHelper.createDocument();
		Document simdoc = DocumentHelper.createDocument();

		int times = simulatorInfo.length;
		Element simulation = DocumentHelper.createElement("Simulation");
		simdoc.setRootElement(simulation);
		Element e = simulation.addElement("Mode");
		e.setText(simulatorInfo[0].currentModelName);
		e = simulation.addElement("BeginTime");
		Date date = new Date(simulatorInfo[0].simulateBeginTime);
		e.setText(DateFormat.getInstance().format(date));
		e = simulation.addElement("RunTime");
		long runTime = 0;
		String runTimeD = "";
		String simTimeD = "";
		String releasePolicyD = "";
		for (int i = 0; i < simulatorInfo.length; i++) {
			runTime += simulatorInfo[i].runTime;
			runTimeD += ","
					+ String.format("%.2f", simulatorInfo[i].runTime / 60000.0);
			simTimeD += ","
					+ String.valueOf((int) (simulatorInfo[i].simulationTime / 60.0 / 60 / 24));
			releasePolicyD += ","
					+ ReleasePolicy.getString(policy[i].releasePolicy);
		}
		e.setText(String.format("%s (%s)",
				CommonMethods.TimeFormat(runTime / times),
				runTimeD.substring(1)));
		e = simulation.addElement("SimulationTime");
		e.setText(simTimeD.substring(1) + " days");
		e = simulation.addElement("ReleasePolicy");
		e.setText(releasePolicyD.substring(1));
		e = simulation.addElement("ReleasePolicyPara");
		if (policy[0].releasePolicy == ReleasePolicy.ConstantTime)
			e.setText("Interval Time: " + policy[0].intervalTime + " minutes");
		else if (policy[0].releasePolicy == ReleasePolicy.ConstantTime) {
			e.setText("Target WIP: " + policy[0].costWIP + " lots");
		} else {
			e.setText("Parameter: see the following");
		}
		e = simulation.addElement("ProductNum");
		e.setText(String.valueOf(releaseDataset[0].size()));
		e = simulation.addElement("AvgProcessStep");
		e.setText(String.valueOf(policy[0].avgProcessStepNum));
		e = simulation.addElement("ToolGroupNum");
		e.setText(String.valueOf(toolGroupDataset[0].size()));
		e = simulation.addElement("ToolpNum");
		e.setText(String.valueOf(toolGroupDataset[0].toolNum));

		Element products = DocumentHelper.createElement("Products");
		doc.setRootElement(products);
		products.addAttribute("number",
				String.valueOf(releaseDataset[0].size()));
		for (int i = -1; i < releaseDataset[0].size(); i++) {
			for (int j = -1; j < times; j++) {
				ReleaseData data;
				String productName;
				if (i == -1) {
					// data=releaseDataset[0].allReleaseDataset;
					if (j == -1) {
						ReleaseData[] rds = new ReleaseData[times];
						for (int k = 0; k < times; k++) {
							rds[k] = releaseDataset[k].allReleaseDataset;
						}
						data = AverageData.average(rds, rds[0].product);
						productName = "--" + data.product;
					} else {
						data = releaseDataset[j].allReleaseDataset;
						productName = simulatorInfo[j].currentModelName;
					}
				} else {
					// data=releaseDataset[0].get(i);
					if (j == -1) {
						ReleaseData[] rds = new ReleaseData[times];
						for (int k = 0; k < times; k++) {
							rds[k] = releaseDataset[k].get(i);
						}
						data = AverageData.average(rds, rds[0].product);
						productName = "--" + data.product;
					} else {
						data = releaseDataset[j].get(i);
						productName = simulatorInfo[j].currentModelName;
					}
				}
				Element product = products.addElement("Product");
				product.addAttribute("name", productName);
				Element releasedNum = product.addElement("ReleasedNum");
				releasedNum.setText(String.valueOf(data.releasedLotNum));

				Element releaseRatio = product.addElement("ReleaseRatio");
				releaseRatio.setText(String.valueOf(data.releaseRatio));
				Element finishedLotNum = product.addElement("FinishedNum");
				finishedLotNum.setText(String.valueOf(data.finishedLotNum));
				Element productivity = product.addElement("Productivity");
				productivity.setText(String.valueOf(data.productivity));
				Element avgWip = product.addElement("AvgWIPLevel");
				avgWip.setText(String.valueOf(data.avgWip));
				Element maxWip = product.addElement("MaxWIPLevel");
				maxWip.setText(String.valueOf(data.maxWip));
				Element minCycleTime = product.addElement("MinCycleTime");
				minCycleTime.setText(String.valueOf(data.minCycleTime));
				Element avgCycleTime = product.addElement("AvgCycleTime");
				avgCycleTime.setText(String.valueOf(data.avgCycleTime));
				Element maxCycleTime = product.addElement("MaxCycleTime");
				maxCycleTime.setText(String.valueOf(data.maxCycleTime));
				Element rawProcessTime = product
						.addElement("RawProcessingTime");
				rawProcessTime.setText(String.valueOf(data.rawProcessTime));
				if (i == -1 && j == -1) {
					simulation.add((Element) (product.clone()));
				}
			}
		}

		saveXMLFile(doc, simulatorInfo[0].currentModelPath
				+ simulatorInfo[0].currentModelName + "_ProductResult.xml");
		doc.clearContent();

		Element toolGroups = DocumentHelper.createElement("ToolGroups");
		doc.setRootElement(toolGroups);
		toolGroups.addAttribute("number",
				String.valueOf(toolGroupDataset[0].size()));
		for (int i = -1; i < toolGroupDataset[0].size(); i++) {
			for (int j = -1; j < times; j++) {
				ToolData tooldata;
				BufferData bufferData;
				WaitDataset waitData;
				BlockDataset blockData;
				int showInHead = 0;
				String toolName = "";
				if (i == -1) {
					// tooldata=toolGroupDataset.allToolData;
					// bufferData=bufferDataset.allBufferData;
					// waitData=toolGroupDataset.allWaitDataset;
					// blockData=toolGroupDataset.allBlockDataset;
					showInHead = 1;
					if (j == -1) {
						ToolData[] tds = new ToolData[times];
						BufferData[] bds = new BufferData[times];
						WaitDataset[] wds = new WaitDataset[times];
						BlockDataset[] kds = new BlockDataset[times];
						for (int k = 0; k < times; k++) {
							tds[k] = toolGroupDataset[k].allToolData;
							bds[k] = bufferDataset[k].allBufferData;
							wds[k] = toolGroupDataset[k].allWaitDataset;
							kds[k] = toolGroupDataset[k].allBlockDataset;
						}
						tooldata = AverageData.average(tds);
						toolName = "-" + "All";
						bufferData = AverageData.average(bds, "");
						waitData = AverageData.average(wds);
						blockData = AverageData.average(kds);
					} else {
						tooldata = toolGroupDataset[j].allToolData;
						bufferData = bufferDataset[j].allBufferData;
						waitData = toolGroupDataset[j].allWaitDataset;
						blockData = toolGroupDataset[j].allBlockDataset;
						toolName = simulatorInfo[j].currentModelName;
					}
				} else {

					if (j == -1) {
						ToolData[] tds = new ToolData[times];
						BufferData[] bds = new BufferData[times];
						WaitDataset[] wds = new WaitDataset[times];
						BlockDataset[] kds = new BlockDataset[times];
						for (int k = 0; k < times; k++) {
							tds[k] = toolGroupDataset[k].get(i).toolGroupDataSum;
							bds[k] = bufferDataset[k].get(i);
							wds[k] = toolGroupDataset[k].get(i).waitDataset;
							kds[k] = toolGroupDataset[k].get(i).blockDataset;
						}
						tooldata = AverageData.average(tds);
						toolName = tds[0].toolName;
						bufferData = AverageData.average(bds, "");
						waitData = AverageData.average(wds);
						blockData = AverageData.average(kds);
					} else {
						tooldata = toolGroupDataset[j].get(i).toolGroupDataSum;
						bufferData = bufferDataset[j].get(i);
						waitData = toolGroupDataset[j].get(i).waitDataset;
						blockData = toolGroupDataset[j].get(i).blockDataset;
						toolName = simulatorInfo[j].currentModelName;
					}
				}
				Element toolGroup = toolGroups.addElement("ToolGroup");
				toolGroup.addAttribute("name", toolName);
				toolGroup
						.addAttribute("showInHead", String.valueOf(showInHead));
				Element avgBuffer = toolGroup.addElement("avgBufferSize");
				avgBuffer.setText(String.valueOf(bufferData.avgBufferSize));
				Element maxBuffer = toolGroup.addElement("maxBufferSize");
				maxBuffer.setText(String.valueOf(bufferData.maxBufferSize));
				Element avgWaitTime = toolGroup.addElement("avgWaitTime");
				avgWaitTime.setText(String.valueOf(waitData.avgWaitTime));
				Element maxWaitTime = toolGroup.addElement("maxWaitTime");
				maxWaitTime.setText(String.valueOf(waitData.maxWaitTime));
				Element avgBlockTime = toolGroup.addElement("avgBlockTime");
				avgBlockTime.setText(String.valueOf(blockData.avgBlockTime));
				Element maxBlockTime = toolGroup.addElement("maxBlcokTime");
				maxBlockTime.setText(String.valueOf(blockData.maxBlockTime));

				e = toolGroup.addElement("FreeRatio");
				e.setText(String.valueOf(tooldata.freeTime * 100));
				e = toolGroup.addElement("SetupRatio");
				e.setText(String.valueOf(tooldata.setupTime * 100));
				e = toolGroup.addElement("processRatio");
				e.setText(String.valueOf(tooldata.processTime * 100));
				e = toolGroup.addElement("blockRatio");
				e.setText(String.valueOf(tooldata.blockTime * 100));
				e = toolGroup.addElement("breakdownRatio");
				e.setText(String.valueOf(tooldata.breakdownTime * 100));
				e = toolGroup.addElement("maintenanceRatio");
				e.setText(String.valueOf(tooldata.maintenanceTime * 100));
				e = toolGroup.addElement("TotalSetupTime");
				e.setText(String.valueOf(tooldata.totalSetupTime));
				e = toolGroup.addElement("InterruptNum");
				e.setText(String.valueOf(tooldata.interruptNum));
				if (i == -1 && j == -1) {
					simulation.add((Element) (toolGroup.clone()));
				}
			}

		}
		saveXMLFile(doc, simulatorInfo[0].currentModelPath
				+ simulatorInfo[0].currentModelName + "_ToolResult.xml");
		doc.clearContent();
		saveXMLFile(simdoc, simulatorInfo[0].currentModelPath
				+ simulatorInfo[0].currentModelName + "_SummaryResult.xml");

	}

	public static ReportInfo saveResult(ReleasePolicy policy,
			SimulationInfo simulatorInfo, ReleaseDataset releaseDataset,
			BufferDataset bufferDataset, ToolGroupDataset toolGroupDataset) {

		ReportInfo reportInfo = new ReportInfo();
		reportInfo.productResultFile = simulatorInfo.currentModelPath
				+ simulatorInfo.currentModelName + "_ProductResult.xml";
		reportInfo.toolResultFile = simulatorInfo.currentModelPath
				+ simulatorInfo.currentModelName + "_ToolResult.xml";
		reportInfo.summaryResultFile = simulatorInfo.currentModelPath
				+ simulatorInfo.currentModelName + "_SummaryResult.xml";

		Document doc = DocumentHelper.createDocument();
		Document simdoc = DocumentHelper.createDocument();
		Element simulation = DocumentHelper.createElement("Simulation");
		simdoc.setRootElement(simulation);
		Element e = simulation.addElement("Mode");
		e.setText(simulatorInfo.currentModelName);
		e = simulation.addElement("BeginTime");
		Date date = new Date(simulatorInfo.simulateBeginTime);
		e.setText(DateFormat.getInstance().format(date));
		e = simulation.addElement("RunTime");
		e.setText(CommonMethods.TimeFormat(simulatorInfo.runTime));
		e = simulation.addElement("SimulationTime");
		e.setText(CommonMethods
				.GetDateStringFromMinutes(simulatorInfo.simulationTime / 60.0));
		e = simulation.addElement("ReleasePolicy");
		e.setText(policy.toString());
		e = simulation.addElement("ReleasePolicyPara");
		if (policy.isPolicy(ReleasePolicy.ConstantTime))
			e.setText("Interval Time: " + policy.intervalTime + " minutes");
		else if (policy.isPolicy(ReleasePolicy.ConstantTime)) {
			e.setText("Target WIP: " + policy.costWIP + " lots");
		} else {
			e.setText("Parameter: see the following");
		}
		e = simulation.addElement("ProductNum");
		e.setText(String.valueOf(releaseDataset.size()));
		e = simulation.addElement("AvgProcessStep");
		e.setText(String.valueOf(policy.avgProcessStepNum));
		e = simulation.addElement("ToolGroupNum");
		e.setText(String.valueOf(toolGroupDataset.size()));
		e = simulation.addElement("ToolpNum");
		e.setText(String.valueOf(toolGroupDataset.toolNum));

		Element products = DocumentHelper.createElement("Products");
		doc.setRootElement(products);
		products.addAttribute("number", String.valueOf(releaseDataset.size()));
		
		//ReleaseData data1 = releaseDataset.get(1);
		//System.out.println(policy.intervalTime+" "+data1.finishedLotNum+"  "+data1.avgWip+"  "+data1.avgCycleTime);
		
		for (int i = -1; i < releaseDataset.size(); i++) {
			ReleaseData data;
			if (i == -1) {
				data = releaseDataset.allReleaseDataset;
				//System.out.println(policy.intervalTime+" "+data.finishedLotNum+"  "+data.avgWip+"  "+data.avgCycleTime);
				//return reportInfo;
			} else {
				data = releaseDataset.get(i);
			}
			Element product = products.addElement("Product");
			product.addAttribute("name", data.product);
			Element releasedNum = product.addElement("ReleasedNum");
			releasedNum.setText(String.valueOf(data.releasedLotNum));

			Element releaseRatio = product.addElement("ReleaseRatio");
			releaseRatio.setText(String.valueOf(data.releaseRatio));
			Element finishedLotNum = product.addElement("FinishedNum");
			finishedLotNum.setText(String.valueOf(data.finishedLotNum));
			Element productivity = product.addElement("Productivity");
			productivity.setText(String.valueOf(data.productivity));
			Element avgWip = product.addElement("AvgWIPLevel");
			avgWip.setText(String.valueOf(data.avgWip));
			Element maxWip = product.addElement("MaxWIPLevel");
			maxWip.setText(String.valueOf(data.maxWip));
			Element minCycleTime = product.addElement("MinCycleTime");
			minCycleTime.setText(String.valueOf(data.minCycleTime));
			Element avgCycleTime = product.addElement("AvgCycleTime");
			avgCycleTime.setText(String.valueOf(data.avgCycleTime));
			Element maxCycleTime = product.addElement("MaxCycleTime");
			maxCycleTime.setText(String.valueOf(data.maxCycleTime));
			Element rawProcessTime = product.addElement("RawProcessingTime");
			rawProcessTime.setText(String.valueOf(data.rawProcessTime));
			if (i == -1) {
				simulation.add((Element) (product.clone()));
			}
		}

		saveXMLFile(doc, reportInfo.productResultFile);
		doc.clearContent();

		Element toolGroups = DocumentHelper.createElement("ToolGroups");
		doc.setRootElement(toolGroups);
		toolGroups.addAttribute("number",
				String.valueOf(toolGroupDataset.size()));
		for (int i = -1; i < toolGroupDataset.size(); i++) {
			ToolData tooldata;
			BufferData bufferData;
			WaitDataset waitData;
			BlockDataset blockData;
			int showInHead = 0;
			if (i == -1) {
				tooldata = toolGroupDataset.allToolData;
				bufferData = bufferDataset.allBufferData;
				waitData = toolGroupDataset.allWaitDataset;
				blockData = toolGroupDataset.allBlockDataset;
				showInHead = 1;
			} else {
				tooldata = toolGroupDataset.get(i).toolGroupDataSum;
				bufferData = bufferDataset.get(i);
				waitData = toolGroupDataset.get(i).waitDataset;
				blockData = toolGroupDataset.get(i).blockDataset;
			}
			Element toolGroup = toolGroups.addElement("ToolGroup");
			toolGroup.addAttribute("name", tooldata.toolName);
			toolGroup.addAttribute("showInHead", String.valueOf(showInHead));
			Element avgBuffer = toolGroup.addElement("avgBufferSize");
			avgBuffer.setText(String.valueOf(bufferData.avgBufferSize));
			Element maxBuffer = toolGroup.addElement("maxBufferSize");
			maxBuffer.setText(String.valueOf(bufferData.maxBufferSize));
			Element avgWaitTime = toolGroup.addElement("avgWaitTime");
			avgWaitTime.setText(String.valueOf(waitData.avgWaitTime));
			Element maxWaitTime = toolGroup.addElement("maxWaitTime");
			maxWaitTime.setText(String.valueOf(waitData.maxWaitTime));
			Element avgBlockTime = toolGroup.addElement("avgBlockTime");
			avgBlockTime.setText(String.valueOf(blockData.avgBlockTime));
			Element maxBlockTime = toolGroup.addElement("maxBlcokTime");
			maxBlockTime.setText(String.valueOf(blockData.maxBlockTime));

			e = toolGroup.addElement("FreeRatio");
			e.setText(String.valueOf(tooldata.freeTime * 100));
			e = toolGroup.addElement("SetupRatio");
			e.setText(String.valueOf(tooldata.setupTime * 100));
			e = toolGroup.addElement("processRatio");
			e.setText(String.valueOf(tooldata.processTime * 100));
			e = toolGroup.addElement("blockRatio");
			e.setText(String.valueOf(tooldata.blockTime * 100));
			e = toolGroup.addElement("breakdownRatio");
			e.setText(String.valueOf(tooldata.breakdownTime * 100));
			e = toolGroup.addElement("maintenanceRatio");
			e.setText(String.valueOf(tooldata.maintenanceTime * 100));
			e = toolGroup.addElement("TotalSetupTime");
			e.setText(String.valueOf(tooldata.totalSetupTime));
			e = toolGroup.addElement("InterruptNum");
			e.setText(String.valueOf(tooldata.interruptNum));
			if (i == -1) {
				simulation.add((Element) (toolGroup.clone()));
			}

		}
		saveXMLFile(doc, reportInfo.toolResultFile);
		doc.clearContent();
		saveXMLFile(simdoc, reportInfo.summaryResultFile);
		return reportInfo;

	}

	private static void saveXMLFile(Document doc, String xmlFileName) {
		try {
			File file = new File(xmlFileName);
			FileWriter fw;
			fw = new FileWriter(file);
			XMLWriter output = new XMLWriter(fw);
			output.write(doc);
			output.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
