package manu.simulation.others;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import manu.model.component.ProcessingTimePool;
import manu.model.component.Product;
import manu.model.component.ReleasePolicy;
import manu.model.component.Skill;
import manu.model.component.ToolGroupInfo;
import manu.model.component.TransportTimePool;
import manu.model.feature.Batch;
import manu.model.feature.DispatchingRules;
import manu.model.feature.Interrupt;
import manu.scheduling.DecisionMaker;
import manu.scheduling.training.BPInfo;
import manu.scheduling.training.ClusterInfo;
import manu.simulation.SimEndCondition;
import manu.simulation.info.SimulationInfo;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import simulation.distribution.RandomVariable;

public class FileRead {

	public static void simulatorInfo(SimulationInfo simulatorInfo) {
		try {
			File f = new File(".//Simulator.xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			String path = "./SimEndCondition[@active='True']";
			Element se = (Element) root.selectSingleNode(path);

			if (se.attributeValue("type").equals("ByPlanTime")) {
				simulatorInfo.simulateEndCondition = SimEndCondition.EndByPlanTime;
				simulatorInfo.planTime = Integer.valueOf(se
						.elementText("PlanTime"));
			} else if (se.attributeValue("type").equals("ByPlanLot")) {
				simulatorInfo.simulateEndCondition = SimEndCondition.EndByPlantLot;
				simulatorInfo.planLotNum = Integer.valueOf(se
						.elementText("PlanLotNum"));
			} else if (se.attributeValue("type").equals("ByDetailPlanTime")) {
				simulatorInfo.simulateEndCondition = SimEndCondition.EndByDetailPlanTime;
			} else if (se.attributeValue("type").equals("ByDetailPlanTime")) {
				simulatorInfo.simulateEndCondition = SimEndCondition.EndByDetailPlanTime;
			}
			if (root.element("GUI").attributeValue("show").equals("True")) {
				simulatorInfo.guiShowOrNot = 1;
			} else {
				simulatorInfo.guiShowOrNot = 0;
			}
			if (root.element("SimTimes") != null) {
				simulatorInfo.simulationTimes = Integer.valueOf(root
						.elementText("SimTimes"));
			} else {
				simulatorInfo.simulationTimes = 1;
			}
			if (root.element("DateAcqTime") != null) {
				// simulatorInfo.dataAcqTime = Integer.valueOf(root
				// .elementText("DateAcqTime"))*24*3600;
				String unit = root.element("DateAcqTime")
						.attributeValue("unit");
				int value = Integer.valueOf(root.elementText("DateAcqTime"));
				simulatorInfo.dataAcqTime = convert2second(unit, value);
				simulatorInfo.dataAcqTimeStr = ((value == 1) ? unit : "per "
						+ String.valueOf(value) + " " + unit + "s");
			} else {
				simulatorInfo.dataAcqTime = 7 * 24 * 3600;
				simulatorInfo.dataAcqTimeStr = "week";
			}
			if (root.element("SampleTrainingData") != null
					&& root.elementText("SampleTrainingData").equals("True")) {
				simulatorInfo.sampleTrainingData = true;
			} else {
				simulatorInfo.sampleTrainingData = false;
			}
			Element output = root.element("Output");
			if (output.elementText("WaitTime").equals("True")) {
				simulatorInfo.waitTimeOutput = true;
			} else {
				simulatorInfo.waitTimeOutput = false;
			}

			if (output.elementText("BlockTime").equals("True")) {
				simulatorInfo.blockTimeOutput = true;
			} else {
				simulatorInfo.blockTimeOutput = false;
			}

			if (output.elementText("BufferData").equals("True")) {
				simulatorInfo.bufferDataOutput = true;
			} else {
				simulatorInfo.bufferDataOutput = false;
			}

			if (output.elementText("ToolData").equals("True")) {
				simulatorInfo.toolDataOutput = true;
			} else {
				simulatorInfo.toolDataOutput = false;
			}
			if (output.elementText("ReleaseData").equals("True")) {
				simulatorInfo.ReleaseDataOutput = true;
			} else {
				simulatorInfo.ReleaseDataOutput = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void trainingInfo(ClusterInfo clusterInfo, BPInfo bpInfo) {

		File f = new File(".//Training.xml");
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element bp = root.element("BPNetwork");
			bpInfo.setEpoch(Integer.valueOf(bp.elementText("Epoch")));
			bpInfo.setEta(Double.valueOf(bp.elementText("LearningRate")));
			bpInfo.setHiddenNodeNum(Integer.valueOf(bp
					.elementText("HiddenNodeNum")));
			bpInfo.setMomentum(Double.valueOf(bp.elementText("Momentum")));
			bpInfo.setTolerance(Double.valueOf(bp.elementText("Tolerance")));
			bpInfo.setMinGradient(Double.valueOf(bp.elementText("Gradient")));
			bpInfo.setMaxFailNum(Integer.valueOf(bp.elementText("MaxFailNum")));
			Element cluster = root.element("Cluster");
			clusterInfo.setClassNum(Integer.valueOf(cluster
					.elementText("ClassNum")));
			clusterInfo.setMaxIterate(Integer.valueOf(cluster
					.elementText("MaxIterate")));
			clusterInfo.setEpoch(Integer.valueOf(cluster.elementText("Epoch")));
			clusterInfo.setTolerance(Double.valueOf(cluster
					.elementText("Tolerance")));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static String GetToolGroup(
			String toolGroupXML,
			/*String processXML1,
			 String productXML,SimulatorInfo simulatorInfo, */ArrayList<ToolGroupInfo> toolGroups) {

		try {

			// File df = new File(productXML);
			// SAXReader dreader = new SAXReader();
			// Document ddoc = dreader.read(df);
			// Element droot = ddoc.getRootElement();
			// String xpath = "/Products/Product";
			// List<Element> product = droot.selectNodes(xpath);
			// if (product == null) {
			// return "No product in product file";
			// }
			// if (product.size() == 0) {
			// return "No product in product file";
			// }
			// simulatorInfo.jobType = new String[product.size() + 1];
			// simulatorInfo.jobType[0] = "ALL";
			// for (int i = 1; i < product.size() + 1; i++) {
			// String jobtype = product.get(i - 1).attributeValue("type");
			// if (jobtype == null || jobtype.isEmpty())
			// return "Didn't sepcify type for product";
			// simulatorInfo.jobType[i] = jobtype;
			// }

//			File pf1 = new File(processXML1);
//			SAXReader preader = new SAXReader();
//			Document pdoc = preader.read(pf1);
//			Element proot = pdoc.getRootElement();

			File f = new File(toolGroupXML);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();

			Element foo;
			Element foo1;
			Element ele;

//			// for bottleneck
//			int[] bottlenecks = new int[100];
//			int tindex = 0;
//			int bottleneckNum = 0;
			// for bottleneck

			// for bpnetwork
			
			for (Iterator<Element> i = root.elementIterator("ToolGroup"); i
					.hasNext();) {
				foo = i.next();
				String toolGroupName = foo.attributeValue("name").replace("-",
						"_");
				if (toolGroupName == null || toolGroupName.isEmpty())
					return "Didn't specify attribute name for tool group";
				
			

				String toolNumber = foo.elementText("ToolNumber");
				if (toolNumber == null || toolNumber.isEmpty()
						|| toolNumber.trim().equals("0"))
					return "Didn't specify toolNumber for tool group "
							+ toolGroupName;
				// String bufferType = foo.elementText("BufferType");
				// if (bufferType == null || bufferType.isEmpty())
				// return "Didn't specify bufferType for tool group "
				// + toolGroupName;
				String rule = foo.elementText("Rule");
				if (rule == null || rule.isEmpty())
					return "Didn't specify rule for tool group "
							+ toolGroupName;

				ToolGroupInfo tgi = new ToolGroupInfo(toolGroupName.trim(),
						Integer.valueOf(toolNumber), DispatchingRules.String2Int(rule));
				
				
				//skills
				String skillstr = foo.elementText("Skill");
				String[]skills=skillstr.split(",");
				for(String sk:skills){
					tgi.getSkills().add(new Skill(sk));
				}
				
				// avoid setup
//				String avoidSetup = foo.elementText("AvoidSetup");
//				if (avoidSetup == null || avoidSetup.isEmpty()
//						|| avoidSetup.trim().equals("False"))
//					tgi.avoidSetup = false;
//				else
//					tgi.avoidSetup = true;
//				// multi-objects weight
//				Element mulobj = foo.element("MultiObjWeight");
//				if (mulobj == null && tgi.avoidSetup)
//					return "Didn't specify multi-object weight for tool group "
//							+ toolGroupName;
//				if (mulobj != null) {
//					String ruleWeight = mulobj.elementText("RuleWeight");
//					if (tgi.avoidSetup
//							&& (ruleWeight == null || ruleWeight.isEmpty()))
//						return "Didn't specify ruleWeight for tool group "
//								+ toolGroupName;
//					tgi.multiObjWeight.ruleWeight = Double.valueOf(ruleWeight
//							.trim());
//					String avoidSetupWeight = mulobj
//							.elementText("AvoidSetupWeight");
//					if (tgi.avoidSetup
//							&& (avoidSetupWeight == null || avoidSetupWeight
//									.isEmpty()))
//						return "Didn't specify avoidsetupWeight for tool group "
//								+ toolGroupName;
//					tgi.multiObjWeight.avoidSetupWeight = Double
//							.valueOf(avoidSetupWeight.trim());
//					String hotLotWeight = mulobj.elementText("HotLotWeight");
//					if (hotLotWeight != null && !hotLotWeight.isEmpty())
//						tgi.multiObjWeight.hotLotWeight = Double
//								.valueOf(hotLotWeight.trim());
//					String criticalTimeWeight = mulobj
//							.elementText("CriticalTimeWeight");
//					if (criticalTimeWeight != null
//							&& !criticalTimeWeight.isEmpty())
//						tgi.multiObjWeight.criticalTimeWeight = Double
//								.valueOf(criticalTimeWeight.trim());
//				}

				// bottleneck
				Element bottleneck = foo.element("Bottelneck");
				if (bottleneck != null) {
					// if(bottleneck.attributeValue("isBottleneck").contains("True"))
					// {
					// tgi.isBottleneck=true;
					// tgi.bottleneckAimBuffer=Integer.valueOf(bottleneck.attributeValue("aimBufferSize"));
					// tgi.bottleneckLevel=Integer.valueOf(bottleneck.attributeValue("level"));
					// }else{
					// tgi.isBottleneck=false;
					// }
					if (bottleneck.attributeValue("isBottelneck").contains(
							"True")) {
//						tgi.bottleneck.isBottleneck = true;
//						tgi.bottleneck.bottleneckAimBuffer = Integer
//								.valueOf(bottleneck
//										.attributeValue("aimBufferSize"));
//						tgi.bottleneck.bottleneckLevel = Integer
//								.valueOf(bottleneck.attributeValue("level"));
//				
//						bottlenecks[tgi.bottleneck.bottleneckLevel - 1] = tindex;
					//	bottleneckNum++;

					} else {
						//tgi.bottleneck.isBottleneck = false;
					}
				}
				// batchs
				foo1 = foo.element("Batchs");
				ArrayList<Batch> batchs = null;
				if (foo1 != null) {
					batchs = new ArrayList<Batch>();
					for (Iterator<Element> j = foo1.elementIterator("Batch"); j
							.hasNext();) {
						ele = j.next();
						Batch batch = new Batch();
						batch.BatchID = Integer.valueOf(ele
								.attributeValue("ID"));
						if (Integer.valueOf(ele.attributeValue("number")) <= 1)
						{
							//return "Batching size is less than two";
						}
						else
						{
				
						}
						batch.BatchNum = Integer.valueOf(ele
								.attributeValue("number"));
						batchs.add(batch);
					}
			
				}
				// batch ends

//				String path = "/Processes/Process/Steps/Step[ToolGroupName='"
//						+ toolGroupName + "']";
//				List<Element> nodes = proot.selectNodes(path);
//				if (nodes.size() < 1) {
//					System.out.println("Tool group " + toolGroupName
//							+ " is not used.");
//					continue;
//				}

//				// setup
//				Element node;
//				Setups setups = new Setups();
//				for (Iterator<Element> itnodes = nodes.iterator(); itnodes
//						.hasNext();) {
//					node = itnodes.next();
//					// previous tools and next tools
//					Element previousTool = (Element) node
//							.selectSingleNode("preceding-sibling::*[1]/ToolGroupName");
//					if (previousTool != null)
//						tgi.addPreTool(previousTool.getTextTrim());
//
//					Element nextTool = (Element) node
//							.selectSingleNode("following-sibling::*[1]");
//					if (nextTool != null) {
//						boolean isBatchNextTool = false;
//						if (nextTool.element("BatchID") != null)
//							isBatchNextTool = true;
//						tgi.addNextBuffer(
//								nextTool.elementText("ToolGroupName"),
//								isBatchNextTool);
//
//					}
//					// if (previousTool == null && nextTool == null)
//					// return "Tool group " + toolGroupName
//					// + " error in process file.";
//
//					String curJobType = node.getParent().getParent()
//							.attributeValue("name");
//					if (curJobType == null || curJobType.isEmpty())
//						return "Didn't specify process name in process file";
//
//					String curJobStep = node.attributeValue("name");
//					if (curJobStep == null || curJobStep.isEmpty())
//						return "Didn't specify step name for process:"
//								+ curJobType + " in process file";
//
//					Element setupele = node.element("Setups");
//					if (setupele != null) {
//						String setupType = setupele.attributeValue("type");
//						if (setupType == null || setupType.isEmpty())
//							return "Didn't specify setup type for process:"
//									+ curJobType + ",step:" + curJobStep
//									+ " in process file";
//
//						Setup isetup = new Setup(setupType, curJobType,
//								curJobStep);
//						Element setupTimeele;
//						for (Iterator<Element> k = setupele
//								.elementIterator("Setup"); k.hasNext();) {
//							setupTimeele = k.next();
//							SubSetup subSetup = new SubSetup();
//							String setupTime = setupTimeele
//									.attributeValue("time");
//							if (setupTime == null || setupTime.isEmpty())
//								return "Didn't specify setup time for process:"
//										+ curJobType + ",step:" + curJobStep
//										+ " in process file";
//							subSetup.setupTime = Double.valueOf(setupTime);
//							if (setupType.equals("sequence-dependent")) {
//								if (batchs != null && batchs.size() > 0) {
//									String batchID = node
//											.elementText("BatchID");
//									if (batchID == null || batchID.isEmpty())
//										return "Didn't specify batchID for process:"
//												+ curJobType
//												+ ",step:"
//												+ curJobStep
//												+ " in process file";
//									if (batchID.contains(",")) {
//										String curBatchID = setupTimeele
//												.attributeValue("curBatchID");
//										if (curBatchID == null
//												|| curBatchID.isEmpty())
//											return "Didn't specify curBatchID for mulit batchID step in process:"
//													+ curJobType
//													+ ",step:"
//													+ curJobStep
//													+ " in process file";
//										subSetup.curBatchID = Integer
//												.valueOf(curBatchID);
//									} else {
//										subSetup.curBatchID = Integer
//												.valueOf(batchID);
//									}
//									String preBatchID = setupTimeele
//											.attributeValue("preBatchID");
//									if (preBatchID == null
//											|| preBatchID.isEmpty())
//										return "Didn't specify prebatchID for setup in process:"
//												+ curJobType
//												+ ",step:"
//												+ curJobStep
//												+ " in process file";
//									subSetup.preBatchID = Integer
//											.valueOf(preBatchID);
//								} else {
//									String stepofPreJob = setupTimeele
//											.attributeValue("stepofPreJob");
//									if (stepofPreJob == null
//											|| stepofPreJob.isEmpty())
//										return "Didn't specify stepofPreJob in process:"
//												+ curJobType
//												+ ",step:"
//												+ curJobStep
//												+ " in process file";
//									subSetup.stepofPreJob = stepofPreJob;
//
//									String typeofPreJob = setupTimeele
//											.attributeValue("typeofPerJob");
//									if (typeofPreJob == null
//											|| typeofPreJob.isEmpty())
//										return "Didn't specify typeofPreJob in process:"
//												+ curJobType
//												+ ",step:"
//												+ curJobStep
//												+ " in process file";
//									subSetup.typeofPreJob = typeofPreJob;
//									// isetup.stepofCurJob = curJobStep;
//									// isetup.typeofCurJob = curJobType;
//
//								}
//							} else if (setupType.equals("sequence-independent")) {
//								if (batchs != null && batchs.size() > 0) {
//									String batchID = node
//											.elementText("BatchID");
//									if (batchID == null || batchID.isEmpty())
//										return "Didn't specify batchID for process:"
//												+ curJobType
//												+ ",step:"
//												+ curJobStep
//												+ " in process file";
//									if (batchID.contains(",")) {
//										String curBatchID = setupTimeele
//												.attributeValue("curBatchID");
//										if (curBatchID == null
//												|| curBatchID.isEmpty())
//											return "Didn't specify curBatchID for mulit batchID step in process:"
//													+ curJobType
//													+ ",step:"
//													+ curJobStep
//													+ " in process file";
//										subSetup.curBatchID = Integer
//												.valueOf(curBatchID);
//									} else {
//										subSetup.curBatchID = Integer
//												.valueOf(batchID);
//									}
//									// }else{
//									// isetup.stepofCurJob = curJobStep;
//									// isetup.typeofCurJob = curJobType;
//									// }
//								}
//							} else {
//								return "Setup type error in process:"
//										+ curJobType + ",step:" + curJobStep
//										+ " in process file";
//							}
//
//							isetup.add(subSetup);
//						}
//						setups.addSetup(isetup);
//					}
//				}
				// System.out.println(setups.toString(toolGroupName,
				// isBatchTool));
				// interruptions
				ArrayList<Interrupt> interrupts = new ArrayList<Interrupt>();
				Element interruptele = foo.element("Interrupts");
				if (interruptele != null) {
					//interrupts = new ArrayList<Interrupt>();
					Element einterrpt;
					int index = 0;
					for (Iterator<Element> k = interruptele
							.elementIterator("Interrupt"); k.hasNext();) {
						einterrpt = k.next();
						Interrupt interrpt = new Interrupt();
						interrpt.index = index;
						index++;
						String applyto = einterrpt.attributeValue("applyTo");
						if (applyto == null || applyto.equals(""))
							return "Term of applyto in interrupts error in tool group "
									+ toolGroupName;
						interrpt.applyTo = applyto;
						interrpt.interruptType = einterrpt
								.attributeValue("type");

						if (interrpt.applyTo.equals("SpecifyTool")) {
							if (einterrpt.element("ToolIndex") == null) {
								return "Terms of specified tool error in tool group "
										+ toolGroupName;
							}
							interrpt.specifiedToolIndex = Integer
									.valueOf(einterrpt
											.elementTextTrim("ToolIndex"));
							if (interrpt.specifiedToolIndex > Integer
									.valueOf(toolNumber)) {
								return "Terms of specified tool index error (greater than tool number) in tool group "
										+ toolGroupName;
							}
						}

						Element epara;
						int paraIndex = 0;
						if (einterrpt.element("Occurrence") == null
								|| einterrpt.element("Occurrence").element(
										"Distribution") == null) {
							return "Terms of occurrence or distribution error in tool group "
									+ toolGroupName;
						}

						Element eDistri = einterrpt.element("Occurrence")
								.element("Distribution");
						interrpt.distribution = eDistri.attributeValue("name");
						if (interrpt.distribution == null
								&& interrpt.distribution.isEmpty())
							return "Distribution name of occurrence is not specified in tool group "
									+ toolGroupName;
						for (Iterator<Element> p = eDistri
								.elementIterator("Para"); p.hasNext();) {
							epara = p.next();
							interrpt.para[paraIndex] = Double.valueOf(epara
									.getTextTrim());
							paraIndex++;
						}
						Element erepair = einterrpt.element("Recovery");
						if (erepair == null)
							return "No recovery data of interrupt in tool group "
									+ toolGroupName;
						paraIndex = 0;
						eDistri = erepair.element("Distribution");
						if (eDistri == null) {
							return "Distribution name of recovery is not specified in tool group "
									+ toolGroupName;
						}
						interrpt.recoveryDistribution = eDistri
								.attributeValue("name");
						if (interrpt.recoveryDistribution == null
								&& interrpt.recoveryDistribution.isEmpty())
							return "Distribution name of recovery is not specified in tool group "
									+ toolGroupName;
						for (Iterator<Element> r = eDistri
								.elementIterator("Para"); r.hasNext();) {
							epara = r.next();
							interrpt.recoveryPara[paraIndex] = Double
									.valueOf(epara.getTextTrim());
							paraIndex++;
						}
						interrupts.add(interrpt);
					}
				}

				int bufferSize = 0;
				if (foo.elementText("BufferSize") != null)
					bufferSize = Integer.valueOf(foo.elementText("BufferSize"));
				else {
					return "Buffer size error in tool group " + toolGroupName;
				}
				tgi.setToolGroup(batchs, bufferSize,interrupts);

//				if (dispatchers != null/*&&(tgi.toolGroupName.equals("MC")||tgi.toolGroupName.equals("MD"))*/) {
//					tgi.setDecisionMaker(dispatchers.get(tgi.getToolGroupName()));
//				}

				toolGroups.add(tgi);
				//tindex++;

			}
//			for (int i = 0; i < bottleneckNum; i++) {
//				if (i == 0) {
//					toolGroups.get(bottlenecks[i]).bottleneck.highLevelBottleneck = "Release";
//
//				} else {
//					toolGroups.get(bottlenecks[i]).bottleneck.highLevelBottleneck =  toolGroups.get(bottlenecks[i - 1]).toolGroupName;
//				}
//				if (i == bottleneckNum - 1) {
//
//				} else {
//					toolGroups.get(bottlenecks[i]).bottleneck.lowLevelBottleneck =  toolGroups.get(bottlenecks[i + 1]).toolGroupName;
//
//				}
//			}
		} catch (Exception e) {
			System.out.println("read tool group data error!");
			e.printStackTrace();
			return "Read tool group data error!";
		}
		return "OK";

	}

	public static String getProducts(String processXML, String productXML,
			ArrayList<Product> products, ReleasePolicy policy) {
		// ArrayList<Product> products = new ArrayList<Product>();
		// Product[] sortedProduct;
		int stepNum = 0;
		try {
			File pf = new File(processXML);
			SAXReader preader = new SAXReader();
			Document pdoc = preader.read(pf);

			File f = new File(productXML);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);

			Element root = doc.getRootElement();
			String path = "./ReleasePolicy[@active='True']";
			Element rp = (Element) root.selectSingleNode(path);
			if (rp == null)
				return "no releae policy";
			if (rp.attributeValue("type").equals("ConstantTime")) {
				policy.releasePolicy = ReleasePolicy.ConstantTime;
				policy.intervalTime = Double.valueOf(rp
						.elementText("IntervalTime"));
			} else if (rp.attributeValue("type").equals("ConstantWIP")) {
				policy.releasePolicy = ReleasePolicy.ConstantWIP;
				policy.intervalTime = Double.valueOf(rp
						.elementText("IntervalTime"));
				policy.costWIP = Integer.valueOf(rp.elementText("ConstantWIP"));
			} else if (rp.attributeValue("type").equals("ConstantWIPDetail")) {
				policy.releasePolicy = ReleasePolicy.ConstantWIPDetail;
			} else if (rp.attributeValue("type").equals("ConstantTimeDetail")) {
				policy.releasePolicy = ReleasePolicy.ConstantTimeDetail;
			}else if (rp.attributeValue("type").equals("IntelligentControl")) {
				policy.releasePolicy = ReleasePolicy.IntelligentControl;
			}
			else if (rp.attributeValue("type").equals("PlanLotAndDueDate")) {
				policy.releasePolicy = ReleasePolicy.PlanLotAndDueDate;
			} else if (rp.attributeValue("type").equals("Bottelneck")) {
				policy.releasePolicy = ReleasePolicy.Bottleneck;
				if (rp.elementText("BeginningPolicy") == null
						|| rp.element("BeginningPolicy") == null
						|| rp.elementText("BeginningPolicy").equals(
								"ConstantTime")) {
					policy.beginningReleasePolicy = ReleasePolicy.ConstantTime;

					if (rp.elementText("IntervalTime") == null
							|| rp.element("IntervalTime") == null) {
						policy.intervalTime = 1000;
						System.out
								.println("No interval time found for the bottleneck release policy");
					} else {
						policy.intervalTime = Double.valueOf(rp
								.elementText("IntervalTime"));
					}
				} else if (rp.elementText("BeginningPolicy").equals(
						"ConstantTimeDetail")) {
					policy.beginningReleasePolicy = ReleasePolicy.ConstantTimeDetail;
				}
			} else {
				return "release policy error";
			}
			Element foo;
			int productIndex = 0;

			for (@SuppressWarnings("unchecked")
			Iterator<Element> i = root.elementIterator("Product"); i.hasNext();) {
				foo = i.next();
				Product product = new Product();

				product.setName(foo.attributeValue("type").trim());

				String prio = foo.elementText("Priority");
				if (prio != null && prio.isEmpty())
					product.setPriority (Integer.valueOf(prio.trim()));
				else
					product.setPriority(1);

				if (policy.isPolicy(ReleasePolicy.ConstantTimeDetail)||policy.isPolicy(ReleasePolicy.ConstantTimeDetail)||
						policy.isPolicy(ReleasePolicy.Bottleneck)
						&& policy.beginningReleasePolicy == ReleasePolicy.ConstantTimeDetail) {
					Element ectd = foo.element("ReleaseIntervalTime");
					Map<String, Double> para = new HashMap<String, Double>();
					String str = ectd.attributeValue("distribution");

					for (Object obj : ectd.elements("Parameter")) {
						Element paraele = (Element) obj;
						para.put(paraele.attributeValue("name"),
								Double.valueOf(paraele.getTextTrim()));
					}
					product.setReleaseIntervalTimeR(new RandomVariable(str, para));

					// product.releaseIntervalTime = Double.valueOf(foo
					// .elementText("ReleaseIntervalTime"));
				}
				//if (policy.isPolicy(ReleasePolicy.ConstantWIPDetail)||policy.isPolicy(ReleasePolicy.IntelligentControl)) {
					product.setReleaseConstantWIP((int) (Integer.valueOf(foo
							.elementText("ReleaseConstantWIP")) ));
					Element ectd = foo.element("ReleaseIntervalTime");
					Map<String, Double> para = new HashMap<String, Double>();
					String str = ectd.attributeValue("distribution");

					for (Object obj : ectd.elements("Parameter")) {
						Element paraele = (Element) obj;
						para.put(paraele.attributeValue("name"),
								Double.valueOf(paraele.getTextTrim()));
					}
					product.setReleaseIntervalTimeR(new RandomVariable(str, para));
					product.setMaxWIP(Integer.valueOf(foo
							.elementText("MaxWIP")));

				//}
//				if (policy.isPolicy(ReleasePolicy.Bottleneck)
//						&& policy.beginningReleasePolicy == ReleasePolicy.ConstantTimeDetail) {
//					product.releaseIntervalTime = Double.valueOf(foo
//							.elementText("ReleaseIntervalTime"));
//				}

				product.setSortType(0);
				Element releaseProb = foo.element("ReleaseProb");
				if (releaseProb == null)
					product.setReleaseProb(1);
				else
					product.setReleaseProb(Double.valueOf(releaseProb.getText()));
				// System.out.println(product.releaseProb);
				String xpath = "/Processes/Process[@name='"
						+ foo.elementText("Process") + "']";
				Element process = (Element) pdoc.selectSingleNode(xpath);
				Element steps = process.element("Steps");
				Element step;
				ArrayList<Double> productCap = new ArrayList<Double>();

				for (@SuppressWarnings("unchecked")
				Iterator<Element> j = steps.elementIterator("Step"); j
						.hasNext();) {
					step = j.next();
					stepNum++;
//					String processtime = step
//							.elementText("ProcessTime");
//			
//					double transporttime = Double.valueOf(step
//							.elementText("TransportTime"));
//					if (transporttime < 0)
//						transporttime = 0;
//
//					String criticalTimeStr = step.elementText("CriticalTime");
//					double criticalTime = 9999999;
//					if (criticalTimeStr != null && !criticalTimeStr.isEmpty())
//						criticalTime = Double.valueOf(criticalTimeStr);
//
//					// int batchID = 0;
//					String id = "";
//					if (step.element("BatchID") != null) {
//						id = step.elementText("BatchID");
//					}
//					String temp[] = id.split(",");
//					ArrayList<Integer> batchIDs = null;
//					if (temp.length > 0 && !temp[0].equals("")) {
//						batchIDs = new ArrayList<Integer>();
//						for (int u = 0; u < temp.length; u++) {
//							batchIDs.add(Integer.valueOf(temp[u]));
//						}
//					}
					String stepName = step.attributeValue("name");
					String skill=step.attributeValue("skill");
					//String toolGroupName = step.elementText("ToolGroupName");
					// String xxpath = "/ToolGroups/ToolGroup[@Name='"
					// + toolGroupName + "']";
					// Element xxprocess = (Element)
					// tdoc.selectSingleNode(xxpath);
					// 20130219
					// int bufferType = 0;
					// for (int t = 0; t < toolGroupInfo.length; t++) {
					// if (toolGroupInfo[t].toolGroupName.equals(step
					// .elementText("ToolGroupName"))) {
					// bufferType = toolGroupInfo[t].bufferType;
					// break;
					// }
					// }
					// 20130219

					product.addProcess(stepName,skill);

					// for (int m = 0; m < toolGroupInfo.length; m++) {
					// if (toolGroupInfo[m].toolGroupName.equals(step
					// .elementText("ToolGroupName"))) {
					// productCap
					// .add(processtime
					// / (toolGroupInfo[m].toolNuminGroup * toolGroupInfo[m]
					// .BatchNumber(batchIDs)));
					// break;
					// }
					// }
				}

				// double r[] = CommonMethods.GetStatFromArray(productCap);
				// product.productCap = r[1];
				// System.out.println("current tools can produce one job with "
				// + product.name + " type in " + product.productCap
				// + " minutes");
				if (policy.isPolicy(ReleasePolicy.PlanLotAndDueDate)) {
//					SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//					product.setDueDate(df.parse(foo.elementText("DueDate"))
//							.getTime());
//					for (int k = 0; k < Integer.valueOf(foo
//							.elementText("PlanLotNum")); k++) {
//						Product productc = new Product();
//						productc.clone(product);
//						productc.setDueDate((long) (productc.getDueDate() - productc.getProductCap()
//								* (k + 1) * 60 * 1000));
//						productc.setJobName(productc.getName()
//								+ String.format("%05d", k + 1));
//						products.add(productc);
//
//					}
				} else {
					product.setSortType(1);
					products.add(product);
				}
				productIndex++;

			}
		} catch (Exception e) {
			System.out.println("read product data error!");
			e.printStackTrace();
			return "read product data error!";
		}
		policy.avgProcessStepNum = stepNum / products.size();
		// sortedProduct = products.toArray(new Product[0]);
		// Arrays.sort(sortedProduct);
		// for(int i=0;i<sortedProduct.length;i++){
		// sortedProduct[i].index=i;
		// }
		return "OK";

	}

	protected static Date date(long millis) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis * 1000);
		return calendar.getTime();
	}

	private static int convert2second(String unit, int value) {
		if (unit.equalsIgnoreCase("year"))
			return value * 365 * 24 * 3600;
		if (unit.equalsIgnoreCase("quarter"))
			return value * 90 * 24 * 3600;
		if (unit.equalsIgnoreCase("month"))
			return value * 30 * 24 * 3600;
		if (unit.equalsIgnoreCase("week"))
			return value * 7 * 24 * 3600;
		if (unit.equalsIgnoreCase("day"))
			return value * 24 * 3600;
		if (unit.equalsIgnoreCase("hour"))
			return value * 3600;
		if (unit.equalsIgnoreCase("minute"))
			return value * 60;
		if (unit.equalsIgnoreCase("second"))
			return value;

		return 0;
	}

	public static int getProcessingTimes(String processingTimeXls,
			ProcessingTimePool processingTimePool) {

		InputStream myxls = null;
		try {
			myxls = new FileInputStream(processingTimeXls);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		try {
			XSSFWorkbook wb = new XSSFWorkbook(myxls);
			XSSFSheet sheet = wb.getSheet("ProcessingTime");

			for (int i = 1; i < sheet.getLastRowNum() ; i++) {
				XSSFRow row = sheet.getRow(i);
				for(int j=1;j<row.getLastCellNum();j++){
					double procTime=row.getCell(j).getNumericCellValue();
					String toolName=sheet.getRow(0).getCell(j).getStringCellValue();
					String processName=row.getCell(0).getStringCellValue();
					processingTimePool.put(toolName+processName, (long) (procTime*60));
				}


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		try {
			myxls.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 1;
	
		
	}

	public static int getTransportTimes(String transportTimeXls,
			TransportTimePool transportTimePool) {
		InputStream myxls = null;
		try {
			myxls = new FileInputStream(transportTimeXls);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		try {
			XSSFWorkbook wb = new XSSFWorkbook(myxls);
			XSSFSheet sheet = wb.getSheet("TransportTime");

			for (int i = 1; i < sheet.getLastRowNum() ; i++) {
				XSSFRow row = sheet.getRow(i);
				for(int j=1;j<row.getLastCellNum();j++){
					double transTime=row.getCell(j).getNumericCellValue();
					String toolName1=sheet.getRow(0).getCell(j).getStringCellValue();
					String toolName2=row.getCell(0).getStringCellValue();
					transportTimePool.put(toolName1+toolName2, (long) (transTime*60));
				}


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		try {
			myxls.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
		
	}

	public static int getSetupTimes(String timeXls, String toolGroupName,
			Map<String, Long> setups) {
		InputStream myxls = null;
		try {
			myxls = new FileInputStream(timeXls);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		try {
			XSSFWorkbook wb = new XSSFWorkbook(myxls);
			XSSFSheet sheet = wb.getSheet("Setup_"+toolGroupName);

			for (int i = 1; i < sheet.getLastRowNum() ; i++) {
				XSSFRow row = sheet.getRow(i);
				for(int j=1;j<row.getLastCellNum();j++){
					double setupT=row.getCell(j).getNumericCellValue();
					String operation1=sheet.getRow(0).getCell(j).getStringCellValue();
					String operation2=row.getCell(0).getStringCellValue();
					setups.put(operation1+operation2, (long) (setupT*60));
				}


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		try {
			myxls.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
		
	}
	
	public static <T> T readObject(String path){
		
		T t=null;
			ObjectInputStream objectInputStream = null;
			try {
				InputStream freader = new FileInputStream(new File(path));
				objectInputStream = new ObjectInputStream(freader);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				 t= (T) objectInputStream
						.readObject();
				objectInputStream.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return t;
			
	

	}



}
