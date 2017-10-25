package manu.scheduling.training.data;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class TrainingDataset<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<T> trainingDataset = new ArrayList<T>();
	String groupName;

	public TrainingDataset(String name) {
		// TODO Auto-generated constructor stub
		groupName = name;
	}


	public ArrayList<T> getTrainingDataset() {
		return trainingDataset;
	}

	public void setTrainingDataset(ArrayList<T> trainingDataset) {
		this.trainingDataset = trainingDataset;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String toolGroupName) {
		this.groupName = toolGroupName;
	}

	public void add(T data) {
		trainingDataset.add(data);
	}
	public void add(TrainingDataset<T> data) {
		if(data!=null)
		trainingDataset.addAll(data.getTrainingDataset());
	}
	public int size() {
		return trainingDataset.size();
	}

	public void clear() {
		trainingDataset.clear();
	}

	public void saveToXls() {
		try {
			saveToXls(trainingDataset, "beforeNorm");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void saveToXls(ArrayList<T> itrainingDataset, String flag)
			throws Exception {
		WritableWorkbook book;
		WritableSheet sheet = null;
		String sd = new java.text.SimpleDateFormat("_yyyyMMddHHmmss")
				.format(new Date());
		if (!new File(".//training").exists())
			new File(".//training").mkdir();
		book = Workbook.createWorkbook(new File("training//" + groupName
				+ flag + sd + ".xls"));
		sheet = book.createSheet("data", 0);
		//Field[] fields = new TrainingData().getClass().getDeclaredFields();
		Field[] fields = itrainingDataset.get(0).getClass().getDeclaredFields();
		int rowIndex = 1;
		int colIndex = 0;
		int sheetIndex=1;
		for (T data : itrainingDataset) {
			if(rowIndex%10000==0){
				sheet = book.createSheet("data"+sheetIndex, sheetIndex);
				sheetIndex++;
				rowIndex = 1;
			}
			colIndex = 0;
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.get(data) != null) {
					if (rowIndex == 1) {
						if (field.get(data) != null
								&& field.get(data).getClass().isArray()) {
							// double[] d = (double[]) field.get(data);
							for (int i = 0; i < Array
									.getLength(field.get(data)); i++) {
								Label label = new Label(colIndex + i, 0,
										field.getName() + (i + 1));
								sheet.addCell(label);
							}
						} else {
							Label label = new Label(colIndex, 0,
									field.getName());
							sheet.addCell(label);
						}
					}
					if (field.getType().equals(String.class)) {
						Label label = new Label(colIndex, rowIndex,
								(String) field.get(data));
						sheet.addCell(label);
						colIndex++;
					}

					else if (field.get(data) != null
							&& field.get(data).getClass().isArray()) {
						double[] d = (double[]) field.get(data);
						if (d == null)
							System.out.println("error" + field.getName());
						for (int i = 0; i < d.length; i++) {
							jxl.write.Number numberz = new jxl.write.Number(
									colIndex, rowIndex, d[i]);
							sheet.addCell(numberz);
							colIndex++;
						}
						// }
					} else {
						jxl.write.Number numberz = new jxl.write.Number(
								colIndex, rowIndex, field.getDouble(data));
						sheet.addCell(numberz);
						colIndex++;
					}
				}

			}
			rowIndex++;
			
		}

		book.write();
		book.close();
	}
}
