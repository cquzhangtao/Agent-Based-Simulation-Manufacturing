package manu.simulation.others;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import simulation.distribution.RollbackRandom;


public class CommonMethods {

	public static void isleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void isleep(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void isleep(double millisD) {

		long millis = (long) millisD;
		int nanos = (int) (millisD - millis) * 1000000;
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double SumDoubleArray(ArrayList<Double> arr) {

		if (arr.size() < 0) {
			return 0;
		}
		double result = 0;
		for (int i = 0; i < arr.size(); i++) {
			result += arr.get(i);
		}
		return result;
	}

	public static long SumLongArray(ArrayList<Long> arr) {

		if (arr.size() < 0) {
			return 0;
		}
		long result = 0;
		for (int i = 0; i < arr.size(); i++) {
			result += arr.get(i);
		}
		return result;
	}

	public static double[] GetStatFromArray(ArrayList<?> arr) {
		double[] result = new double[4];
		double[] temp;
		if (arr.size() > 0) {
			Object[] d = arr.toArray();
			temp = new double[d.length];
			String a = d[0].getClass().getSimpleName();
			if (a.equals("Double"))
				for (int i = 0; i < d.length; i++)
					temp[i] = (Double) d[i];
			else if (a.equals("Integer"))
				for (int i = 0; i < d.length; i++) {
					temp[i] = (Integer) d[i];
				}
			else
				for (int i = 0; i < d.length; i++) {
					temp[i] = (Long) d[i];
				}
			double min = temp[0];
			double max = temp[0];
			double sum = temp[0];

			for (int i = 1; i < temp.length; i++) {
				sum = sum + temp[i];
				if (temp[i] < min)
					min = temp[i];
				if (temp[i] > max)
					max = temp[i];
			}
			double avg = sum / d.length;
			sum = 0;
			for (int i = 1; i < temp.length; i++) {
				sum += (temp[i] - avg) * (temp[i] - avg);
			}
			result[0] = min;
			result[1] = max;
			result[2] = avg;
			result[3] = Math.pow(sum / temp.length, 0.5);

		} else {
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
			result[3] = 0;
		}

		return result;
	}

	public static double[] GetStatFromArray(@SuppressWarnings("rawtypes") ArrayList[] arr) {
		double[] result = new double[3];
		if (arr != null && arr.length > 0) {
			double min = 10000;
			double max = 0;
			double sum = 0;
			int count = 0;
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].size(); j++) {
					if ((Double) arr[i].get(j) < min) {
						min = (Double) arr[i].get(j);
					}
					if ((Double) arr[i].get(j) > max) {
						max = (Double) arr[i].get(j);
					}
					sum = sum + (Double) arr[i].get(j);
				}
				count = count + arr[i].size();
			}
			if (count > 0) {
				result[0] = min;
				result[1] = max;
				result[2] = sum / count;
				return result;
			}
		}

		result[0] = 0;
		result[1] = 0;
		result[2] = 0;
		return result;
	}

	public static String GetDateStringFromMinutes(double minutes) {
		int day = (int) Math.floor(minutes / (60 * 24));
		int hour = (int) Math.floor((minutes - day * 24 * 60) / 60);
		int minute = (int) (minutes - day * 24 * 60 - hour * 60);
		String str = String.format("%3d", day) + " days "
				+ String.format("%2d", hour) + " hours "
				+ String.format("%2d", minute) + " minutes ";
		return str;
	}

	public static String FormatStringbyLength(String targetStr, int length) {
		int curLength = targetStr.getBytes().length;
		if (targetStr != null && curLength > length)
			while (targetStr.getBytes().length > length)
				targetStr = targetStr.substring(0, targetStr.length() - 1);
		String newString = "";
		int cutLength = length - targetStr.getBytes().length;
		for (int i = 0; i < cutLength; i++)
			newString += "\b";
		return targetStr + newString;
	}

	public static void WriteFile(String fileName, String content, String mode) {
		if (mode.equals("NEW")) {
			File f = new File(fileName);
			if (f.exists()) {
				f.delete();
			}
		}
		WriteFile(fileName, content);
	}

	public static void WriteFile(String fileName, String content) {

		class FileThread extends Thread {
			String fileName;
			String content;

			public FileThread(String fileName, String content) {
				this.fileName = fileName;
				this.content = content;
			}

			@Override
			public void run() {
				FileWriter writer;
				try {
					writer = new FileWriter(fileName, true);
					writer.write(content + "\r\n");
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		FileThread thread = new FileThread(fileName, content);
		thread.run();

	}

	public static void WriteArrayToFile(String fileName, ArrayList<?> arr,
			String header) {
		class FileThread extends Thread {
			String fileName;
			String header;
			ArrayList<?> arr;

			public FileThread(String fileName, ArrayList<?> arr, String header) {
				this.fileName = fileName;
				this.header = header;
				this.arr = arr;
			}

			@Override
			public void run() {
				File f = new File(fileName + ".txt");
				if (f.exists())
					f.delete();
				FileWriter writer;
				try {
					writer = new FileWriter(fileName + ".txt", true);
					writer.write(header + "\r\n");
					for (int i = 0; i < arr.size(); i++) {
						writer.write(String.valueOf(arr.get(i)) + "\r\n");
					}

					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		FileThread thread = new FileThread(fileName, arr, header);
		thread.run();

	}

	public static void WriteArrayToFile(String fileName, @SuppressWarnings("rawtypes") ArrayList[] arr,
			String[] header) {
		class FileThread extends Thread {
			String fileName;
			String[] header;
			@SuppressWarnings("rawtypes")
			ArrayList[] arr;

			public FileThread(String fileName, @SuppressWarnings("rawtypes") ArrayList[] arr, String[] header) {
				this.fileName = fileName;
				this.header = header;
				this.arr = arr;
			}

			@Override
			public void run() {
				// File f = new File(".//result//" + fileName
				// + String.valueOf(System.currentTimeMillis()) + ".txt");
				// if (f.exists())
				// f.delete();
				FileWriter writer;
				try {
					if (arr.length < 1 || arr[0].size() < 1)
						return;
					writer = new FileWriter(fileName + ".txt", false);

					if (header.length < 1)
						return;
					for (int i = 0; i < header.length; i++) {
						writer.write(header[i] + "          ");
					}
					writer.write("\r\n");

					String formatstr = "%d";
					if (arr[0].get(0) instanceof Double) {
						formatstr = "%.4f";
					}

					for (int i = 0; i < arr[0].size(); i++) {
						for (int j = 0; j < arr.length; j++) {
							writer.write(String.format(formatstr, arr[j].get(i))
									+ "          ");

						}
						writer.write("\r\n");
					}

					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		FileThread thread = new FileThread(fileName, arr, header);
		thread.run();

	}

	class TwoDimArrayComparator implements Comparator<Object> {
		private int keyColumn = 0;
		private int sortOrder = 1;

		public TwoDimArrayComparator() {
		}

		public TwoDimArrayComparator(int keyColumn) {
			this.keyColumn = keyColumn;
		}

		public TwoDimArrayComparator(int keyColumn, int sortOrder) {
			this.keyColumn = keyColumn;
			this.sortOrder = sortOrder;
		}

		@Override
		public int compare(Object a, Object b) {
			if (a instanceof String[]) {
				return sortOrder
						* ((String[]) a)[keyColumn]
								.compareTo(((String[]) b)[keyColumn]);
			} else if (a instanceof int[]) {
				return sortOrder
						* (((int[]) a)[keyColumn] - ((int[]) b)[keyColumn]);
			} else {
				return 0;
			}
		}
	}

	public static void delDirectory(String filepath) throws IOException {
		File f = new File(filepath);// 瀹氫箟鏂囦欢璺�?
		if (f.exists() && f.isDirectory()) {
			if (f.listFiles().length == 0) {// 鑻ョ洰褰曚笅娌℃湁鏂囦欢鍒欑洿鎺ュ垹闄�
				f.delete();
			} else {
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						delDirectory(delFile[j].getAbsolutePath());
					}
					delFile[j].delete();
				}
			}
		}
	}
	//static Random rand = new Random(600);
	public static double randExp(double mean,RollbackRandom rand)// mean is 1/lambda
	{
		//Random rand = new Random(System.currentTimeMillis());
		double p;
		if (mean > 10000)
			mean = 10000;
		double randres;
		while (true) {
			p = rand.nextDouble();
			if (p < 1 / mean)
				break;
		}
		// p=rand.nextDouble()*1 / mean;
		randres = -mean * Math.log(mean * p);
		return randres;

	}

	public static double getRandom(String distribution, double para[], RollbackRandom rand) {
		if (distribution.equals("Exponential")) {
			return randExp(para[0],rand);
		} else if (distribution.equals("Constant")) {
			return para[0];
		} else
			return 0;
	}

	public static double getVariance(ArrayList<Double> data) {
		if (data.size() < 1)
			return 0;
		double sum = 0;
		for (int i = 0; i < data.size(); i++)
			sum += data.get(i);
		double avg = sum / data.size();
		sum = 0;
		for (int i = 0; i < data.size(); i++)
			sum += (data.get(i) - avg) * (data.get(i) - avg);
		return Math.pow(sum / data.size(), 0.5);
	}
	
	public static String TimeFormat(long simulatedTime)
	{
		simulatedTime /= 1000;
		int hour1 = (int) Math.floor(simulatedTime / 3600);
		int minute1 = (int) Math.floor((simulatedTime - hour1 * 3600) / 60);
		int second1 = (int) (simulatedTime - hour1 * 3600 - minute1 * 60);
		return String.format("%3d : %02d : %02d", hour1, minute1,
				second1);
	}
}
