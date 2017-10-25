package manu.simulation.others;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class FXModelTransfer {
	String FXModelFile;
	String filePath;

	public FXModelTransfer(String excelFile, String filePath) {
		FXModelFile = excelFile;
		this.filePath = filePath;
	}

	public int transfer() {
		FxSetups fxSetups = new FxSetups();
		String modelName = FXModelFile.substring(0,
				FXModelFile.lastIndexOf("."));
		ArrayList<String> processName = new ArrayList<String>();
		ArrayList<String> productName = new ArrayList<String>();
		ArrayList<Integer> numberUnitinLot = new ArrayList<Integer>();
		// Workbook book = null;
		Document doc = DocumentHelper.createDocument();
		InputStream myxls = null;
		try {
			myxls = new FileInputStream(filePath + FXModelFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		try {
			HSSFWorkbook wb = new HSSFWorkbook(myxls);

			Element root = DocumentHelper.createElement("Products");
			doc.setRootElement(root);
			Element rel = root.addElement("ReleasePolicy");
			rel.addAttribute("type", "ConstantTimeDetail");
			rel.addAttribute("active", "True");
			// Sheet sheet = book.getSheet("Products");
			HSSFSheet sheet = wb.getSheet("Products");
			int productIndex = getColIndex(sheet, "PRODUCT");
			int processIndex = getColIndex(sheet, "PROCESS");
			int releaseIndex = getColIndex(sheet, "RELEASE");
			int betweenIndex = getColIndex(sheet, "BETWEEN");
			int numUnitinLotIndex = getColIndex(sheet, "SIZE");
			if (productIndex == -1 || processIndex == -1 || releaseIndex == -1
					|| betweenIndex == -1) {
				System.out.println("FX model error!");
				return -1;
			}
			for (int i = 9; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row == null)
					continue;
				if (row.getCell(0) == null)
					continue;
				if (!row.getCell(0).getStringCellValue().equals("DATA")
						|| row.getCell(productIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
					continue;
				}
				Element product = root.addElement("Product");
				String productstr=row.getCell(productIndex)
						.getStringCellValue().replace("-", "_").trim();
				product.addAttribute("type", productstr);
				productName.add(productstr);
				Element release = product.addElement("ReleaseIntervalTime");
				release.addAttribute("distribution", "constant");
				
				Element parameter=release.addElement("Parameter");
				parameter.addAttribute("name", "mean");
				parameter.setText(String.valueOf(row.getCell(betweenIndex)
						.getNumericCellValue() * 60.0));
				parameter=release.addElement("Parameter");
				parameter.addAttribute("name", "variance");
				parameter.setText("10");
				Element process = product.addElement("Process");
				HSSFCell cp = row.getCell(processIndex);
				if (cp.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					//process.setText(String.valueOf((int) cp
					//		.getNumericCellValue()));
					processName.add(String.valueOf((int) cp
							.getNumericCellValue()));
				} else {
					//process.setText(cp.getStringCellValue());
					processName.add(cp.getStringCellValue());
				}
				process.setText(productstr);
			
				numberUnitinLot.add((int) row.getCell(numUnitinLotIndex)
						.getNumericCellValue());

			}
			saveXMLFile(doc, filePath + modelName + "_Product.xml");
			doc.clearContent();

			Element troot = DocumentHelper.createElement("ToolGroups");
			doc.setRootElement(troot);
			HSSFSheet tsheet = wb.getSheet("Tools");
			int toolgroupIndex = getColIndex(tsheet, "TOOLGROUP");
			int numberIndex = getColIndex(tsheet, "NUMBER");
			int ruleIndex = getColIndex(tsheet, "DISPATCHRULE");
			int batchidIndex = getColIndex(tsheet, "BATCHID");
			int batchnumIndex = getColIndex(tsheet, "MAXBATCH");
			int interruptNameIndex = getColIndex(tsheet, "INTNAME");
			int interruptTypeIndex = getColIndex(tsheet, "INTTYPE");
			int interruptTimeIndex = getColIndex(tsheet, "TTF");
			int recoveryTimeIndex = getColIndex(tsheet, "TTR");
			int setupNameIndex = getColIndex(tsheet, "SETUP");
			int setupIDIndex = getColIndex(tsheet, "SETUPID");
			int setupTimeIndex = getColIndex(tsheet, "SETUPTIME");
			Element toolgroup = null;
			Element batchs = null;
			Element interrupts = null;
			int toolGroupNum = 0;
			FxSetup fxSetup = null;
			for (int i = 9; i < tsheet.getLastRowNum() + 1; i++) {

				HSSFRow row = tsheet.getRow(i);
				if (row == null)
					continue;
				if (row.getCell(0) == null)
					continue;
				if (!row.getCell(0).getStringCellValue().equals("DATA"))
					continue;
				if (row.getCell(toolgroupIndex) == null
						|| row.getCell(toolgroupIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
					if (!(row.getCell(batchidIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK)
							&& batchs != null) {
						Element batch = batchs.addElement("Batch");
						batch.addAttribute("ID", String.valueOf((int) row
								.getCell(batchidIndex).getNumericCellValue()));
						batch.addAttribute(
								"number",
								String.valueOf((int) (row
										.getCell(batchnumIndex)
										.getNumericCellValue() / numberUnitinLot
										.get(0))));
					}
					if (!(row.getCell(interruptNameIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK)
							&& interrupts != null) {
						Element interrupt = interrupts.addElement("Interrupt");
						interrupt.addAttribute("type", "Breakdown");
						interrupt.addAttribute("applyTo", "EachTool");
						interrupt.addAttribute("name",
								row.getCell(interruptNameIndex)
										.getStringCellValue());
						Element occur = interrupt.addElement("Occurrence");
						occur.addAttribute("type",
								row.getCell(interruptTypeIndex)
										.getStringCellValue());
						Element distribution = occur.addElement("Distribution");
						distribution.addAttribute("name", "Exponential");
						distribution.addElement("Para").setText(
								String.valueOf(row.getCell(interruptTimeIndex)
										.getNumericCellValue()));
						Element recovery = interrupt.addElement("Recovery");
						Element redistribution = recovery
								.addElement("Distribution");
						redistribution.addAttribute("name", "Exponential");
						redistribution.addElement("Para").setText(
								String.valueOf(row.getCell(recoveryTimeIndex)
										.getNumericCellValue() * 60));
					}
					if (!(row.getCell(setupIDIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK)
							&& fxSetup != null) {
						fxSetup.add(row.getCell(setupNameIndex)
								.getStringCellValue(), row
								.getCell(setupIDIndex).getNumericCellValue(),
								row.getCell(setupTimeIndex)
										.getNumericCellValue());
					}
				} else {
					toolgroup = troot.addElement("ToolGroup");
					String toolGroupName = row.getCell(toolgroupIndex)
							.getStringCellValue().replace("-", "_").trim();
					toolgroup.addAttribute("name", toolGroupName);
					Element number = toolgroup.addElement("ToolNumber");
					number.setText(String.valueOf((int) row
							.getCell(numberIndex).getNumericCellValue()));
					Element rule = toolgroup.addElement("Rule");
					if (row.getCell(ruleIndex) == null
							|| row.getCell(ruleIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK)
						rule.setText("FIFO");
					else
						rule.setText(row.getCell(ruleIndex)
								.getStringCellValue());
					Element bufferType = toolgroup.addElement("BufferType");
					Element bufferNum = toolgroup.addElement("BufferSize");
					bufferNum.setText("500");
					// Element setup=toolgroup.addElement("SetupTime");
					// setup.setText("10");

					batchs = toolgroup.addElement("Batchs");
					HSSFCell cell = row.getCell(batchidIndex);
					if (cell == null
							|| cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
						batchs.addAttribute("isBatch", "False");
						bufferType.setText("AllInOne");
					} else {
						bufferType.setText("AllInOne");
						batchs.addAttribute("isBatch", "True");
						Element batch = batchs.addElement("Batch");
						batch.addAttribute("ID", String.valueOf((int) cell
								.getNumericCellValue()));
						batch.addAttribute(
								"number",
								String.valueOf((int) (row
										.getCell(batchnumIndex)
										.getNumericCellValue() / numberUnitinLot
										.get(0))));
					}

					interrupts = toolgroup.addElement("Interrupts");
					HSSFCell rcell = row.getCell(interruptNameIndex);
					if (rcell == null
							|| rcell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
					} else {
						Element interrupt = interrupts.addElement("Interrupt");
						interrupt.addAttribute("type", "Breakdown");
						interrupt.addAttribute("applyTo", "EachTool");
						interrupt.addAttribute("name",
								rcell.getStringCellValue());
						Element occur = interrupt.addElement("Occurrence");
						occur.addAttribute("type",
								row.getCell(interruptTypeIndex)
										.getStringCellValue());
						Element distribution = occur.addElement("Distribution");
						distribution.addAttribute("name", "Exponential");
						distribution.addElement("Para").setText(
								String.valueOf(row.getCell(interruptTimeIndex)
										.getNumericCellValue()));
						Element recovery = interrupt.addElement("Recovery");
						Element redistribution = recovery
								.addElement("Distribution");
						redistribution.addAttribute("name", "Exponential");
						redistribution.addElement("Para").setText(
								String.valueOf(row.getCell(recoveryTimeIndex)
										.getNumericCellValue() * 60));
					}

					HSSFCell scell = row.getCell(setupNameIndex);
					if (scell == null
							|| scell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
					} else {
						fxSetup = new FxSetup(toolGroupName);
						fxSetup.add(row.getCell(setupNameIndex)
								.getStringCellValue(), row
								.getCell(setupIDIndex).getNumericCellValue(),
								row.getCell(setupTimeIndex)
										.getNumericCellValue());
						fxSetups.add(fxSetup);

					}

					toolGroupNum++;
				}

			}
			troot.addAttribute("number", String.valueOf(toolGroupNum));
			saveXMLFile(doc, filePath + modelName + "_ToolGroup.xml");
			myxls.close();

			doc.clearContent();

			Element proot = DocumentHelper.createElement("Processes");
			doc.setRootElement(proot);
			for (int i = 0; i < processName.size(); i++) {
				HSSFSheet psheet = wb.getSheet(processName.get(i));
				int stepIndex = getColIndex(psheet, "STEP");
				int toolgroupIndex1 = getColIndex(psheet, "TOOLGROUP");
				int loadIndex = getColIndex(psheet, "LOAD");
				int perunitIndex = getColIndex(psheet, "PERUNIT");
				int perlotIndex = getColIndex(psheet, "PERLOT");
				int perbatchIndex = getColIndex(psheet, "PERBATCH");
				int usebatchidIndex = getColIndex(psheet, "USEBATCHID");
				int unloadIndex = getColIndex(psheet, "UNLOAD");
				int travelIndex = getColIndex(psheet, "STEPTRAVEL");
				int useSetupIndex = getColIndex(psheet, "USESETUP");
				int useSetupIDIndex = getColIndex(psheet, "SETUPID");
				Element process = proot.addElement("Process");
				process.addAttribute("name", productName.get(i));
				Element steps = process.addElement("Steps");
				int stepNum = 0;
				for (int j = 9; j < psheet.getLastRowNum() + 1; j++) {
					if (psheet.getRow(j) == null)
						continue;
					if (psheet.getRow(j).getCell(0) == null)
						continue;
					if (!psheet.getRow(j).getCell(0).getStringCellValue()
							.equals("DATA")
							|| psheet.getRow(j).getCell(stepIndex)
									.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
						continue;
					}
					Element step = steps.addElement("Step");
					step.addAttribute("name",
							psheet.getRow(j).getCell(stepIndex)
									.getStringCellValue());
					Element toolgroup1 = step.addElement("ToolGroupName");
					String toolGroupName = psheet.getRow(j)
							.getCell(toolgroupIndex1).getStringCellValue()
							.replace("-", "_").trim();
					toolgroup1.setText(toolGroupName);
					String travelstr = String.valueOf(psheet.getRow(j)
							.getCell(travelIndex).getNumericCellValue() * 60);
					Element travel = step.addElement("TransportTime");
					if (travelstr.isEmpty())
						travel.setText("0");
					else
						travel.setText(travelstr);

					HSSFCell bc = psheet.getRow(j).getCell(usebatchidIndex);
					boolean isBatchTool = false;

					if (bc != null
							&& !(bc.getCellType() == HSSFCell.CELL_TYPE_BLANK)) {
						Element batchid = step.addElement("BatchID");
						batchid.setText(String.valueOf((int) bc
								.getNumericCellValue()));
						isBatchTool = true;
					}

					HSSFCell sc = psheet.getRow(j).getCell(useSetupIndex);

					if (sc != null
							&& !(sc.getCellType() == HSSFCell.CELL_TYPE_BLANK)) {
						Element setupsEle = step.addElement("Setups");
						setupsEle.addAttribute("type", "sequence-independent");
						Element setupEle = setupsEle.addElement("Setup");
						if (isBatchTool) {
							setupEle.addAttribute("curBatchID", String
									.valueOf((int) bc.getNumericCellValue()));
						}
						setupEle.addAttribute(
								"time",
								fxSetups.getSetupTime(toolGroupName, psheet
										.getRow(j).getCell(useSetupIndex)
										.getStringCellValue(), psheet.getRow(j)
										.getCell(useSetupIDIndex)
										.getNumericCellValue()));
					}
					double processTime = (getContent(psheet, loadIndex, j)
							+ getContent(psheet, perunitIndex, j)
							* numberUnitinLot.get(i)
							+ getContent(psheet, perlotIndex, j)
							+ getContent(psheet, perbatchIndex, j) + getContent(
							psheet, unloadIndex, j)) * 60;
					Element time = step.addElement("ProcessTime");
					time.setText(String.valueOf(processTime));
					stepNum++;
				}
				steps.addAttribute("number", String.valueOf(stepNum));
			}
			saveXMLFile(doc, filePath + modelName + "_Process.xml");
			doc.clearContent();

			Element mroot = DocumentHelper.createElement("Model");
			doc.setRootElement(mroot);
			Element tgf = mroot.addElement("ToolGroup");
			tgf.setText(modelName + "_ToolGroup.xml");
			Element pf = mroot.addElement("Process");
			pf.setText(modelName + "_Process.xml");
			Element prf = mroot.addElement("Product");
			prf.setText(modelName + "_Product.xml");
			saveXMLFile(doc, filePath + modelName + ".mod");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}

		return 1;

	}

	private void saveXMLFile(Document doc, String xmlFileName) {
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

	private int getColIndex(HSSFSheet sheet, String colName) {
		HSSFRow row = sheet.getRow(0);
		for (int i = 0; i < row.getLastCellNum(); i++) {
			if (row.getCell(i).getStringCellValue().equals(colName))
				return i;
		}
		return -1;
	}

	private double getContent(HSSFSheet sheet, int col, int row) {
		HSSFCell cell = sheet.getRow(row).getCell(col);
		if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK)
			return 0;
		else
			return cell.getNumericCellValue();
	}

	class FxSetups {
		ArrayList<FxSetup> fxSubSetups = new ArrayList<FxSetup>();

		void add(FxSetup fxsetup) {
			fxSubSetups.add(fxsetup);
		}

		String getSetupTime(String toolGroupName, String setupName,
				double setupID) {
			for (int i = 0; i < fxSubSetups.size(); i++) {
				if (fxSubSetups.get(i).toolGroupName.equals(toolGroupName)) {
					return fxSubSetups.get(i).getSetupTime(setupName, setupID);
				}
			}
			System.out.println("Read setup time error");
			return "0";
		}
	}

	class FxSetup {
		String toolGroupName;
		ArrayList<FxSubSetup> fxSubSetups = new ArrayList<FxSubSetup>();

		FxSetup(String toolGroupName) {
			this.toolGroupName = toolGroupName;
		}

		void add(FxSubSetup subsetup) {
			fxSubSetups.add(subsetup);
		}

		void add(String setupName, double setupID, double setupTime) {
			fxSubSetups.add(new FxSubSetup(setupName, setupID, setupTime));
		}

		String getSetupTime(String setupName, double setupID) {
			for (int i = 0; i < fxSubSetups.size(); i++) {
				if (fxSubSetups.get(i).setupName.equals(setupName)
						&& fxSubSetups.get(i).setupID == setupID) {
					return String.valueOf(fxSubSetups.get(i).setupTime * 60);
				}
			}
			System.out.println("Read setup time error");
			return "0";
		}
	}

	class FxSubSetup {
		String setupName;
		double setupID;
		double setupTime;

		FxSubSetup(String setupName, double setupID, double setupTime) {
			this.setupID = setupID;
			this.setupName = setupName;
			this.setupTime = setupTime;
		}
	}

}
