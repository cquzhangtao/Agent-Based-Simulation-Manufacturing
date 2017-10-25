package manu.scheduling.training;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kmeans<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Class<T> classT;
	private List<String> fieldNames = new ArrayList<String>();
	//private int classNum = 1;
	private T[] minMax;
	private double[][] minMaxMatrix;
	private List<T>[] classifiedDataset;
	private double[][] alldata;
	private int fieldNum;
	private ArrayList<Double> fErrors = new ArrayList<Double>();
	private ArrayList<Double>[] fMaxRadius;
	private ArrayList<Double>[] fAvgRadius;
	private ArrayList<Double> globalErrors=new ArrayList<Double>();
	private ClusterInfo clusterInfo;
	private Double[] weight;
	private double[][] fCenterData;
	private List<T> unNormalizedData;
	private List<T> normalizedData;
	private int finalClassNumber;
	private double[][][] normResults;
	private double minimalGlobalError;
	private boolean finished=false;
	private List<Double> classNumDetermineData=new ArrayList<Double>();
	private boolean cancled;
	
	public Kmeans() {

	}

	@SuppressWarnings("unchecked")
	public Kmeans(List<T> list, ClusterInfo info) {
		normalizedData=list;
		clusterInfo = info;
		T t = list.get(0);
		this.classT = (Class<T>) t.getClass();
		Field[] fields = this.classT.getDeclaredFields();
		fieldNames = getFeatureString(fields, t);
		fieldNum = fieldNames.size();
		alldata = new double[list.size()][fieldNum];

		int dataIndex = 0;
		for (T data : list) {
			alldata[dataIndex] = getFeatureData(fields, data);
			dataIndex++;
		}
	
	}
	public void randomInit( T t,ClusterInfo clusterInfo){
		this.classT = (Class<T>) t.getClass();
		Field[] fields = this.classT.getDeclaredFields();
		fieldNames = getFeatureString(fields, t);
		fieldNum = fieldNames.size();
		fCenterData=new double[clusterInfo.getClassNum()][fieldNum];
		Random rnd=new Random();
		for(int i=0;i<clusterInfo.getClassNum();i++){
			for(int j=0;j<fieldNum;j++){
				fCenterData[i][j]=rnd.nextDouble();
			}
		}
		
	}
	
	

	public void clear() {
		classifiedDataset = null;
		alldata = null;
		fErrors = null;
		fAvgRadius = null;
		fAvgRadius = null;
		globalErrors=null;
		normResults=null;
		unNormalizedData=null;
		normalizedData=null;
		
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public int getFieldNum() {
		return fieldNum;
	}

	public double[][] getMinMaxMatrix() {
		return minMaxMatrix;
	}

	public void setMinMaxMatrix(double[][] minMaxMatrix) {
		this.minMaxMatrix = minMaxMatrix;
	}

	public void setFieldNum(int fieldNum) {
		this.fieldNum = fieldNum;
	}

	public List<T>[] getResult() {
		return classifiedDataset;
	}

	public void setResult(List<T>[] result) {
		this.classifiedDataset = result;
	}

	public int getFeatureNum() {
		return fieldNum;
	}

	public T[] getMinMax() {
		return minMax;
	}

	public void setMinMax(T[] minMax) {
		this.minMax = minMax;
		minMaxMatrix = new double[2][fieldNum];
		Field[] fields = classT.getDeclaredFields();
		int index = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				if (field.getAnnotation(KmeansField.class) != null) {
					if (field.get(minMax[0]).getClass().isArray()) {
						double[] minv = (double[]) field.get(minMax[0]);
						double[] maxv = (double[]) field.get(minMax[1]);
						for (int i = 0; i < minv.length; i++) {
							minMaxMatrix[0][index] = minv[i];
							minMaxMatrix[1][index] = maxv[i];
							index++;
						}
					} else {
						minMaxMatrix[0][index] = field.getDouble(minMax[0]);
						minMaxMatrix[1][index] = field.getDouble(minMax[1]);
						index++;
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public int getClassNum() {
		return clusterInfo.getClassNum();
	}

	public void setClassNum(int classNum) {
		//this.classNum = classNum;
	}

	public ArrayList<Double>[] getMaxRadius() {
		return fMaxRadius;
	}

	public void setMaxRadius(ArrayList<Double>[] indivErrors) {
		this.fMaxRadius = indivErrors;
	}

	public ArrayList<Double> getErrors() {
		return fErrors;
	}

	public void setErrors(ArrayList<Double> errors) {
		this.fErrors = errors;
	}



	public double[][] getCenter() {
		double[][] centerData = new double[getClassNum()][fieldNum];
		Random rnd=new Random();
		ArrayList<Integer> selectedIndex=new ArrayList<Integer>();
		int classIndex=0;
		while(selectedIndex.size()<getClassNum()){
			int index=rnd.nextInt(alldata.length);
			if(!selectedIndex.contains(index)){
				selectedIndex.add(index);
				centerData[classIndex]=alldata[index].clone();
				classIndex++;
			}
		}
		
		return centerData;
		
//		double[][] centerData = new double[classNum][fieldNum];
//		Random rnd = new Random();
//		ArrayList<Integer> selectedDataIndex = new ArrayList<Integer>();
//		int index = rnd.nextInt(alldata.length);
//		selectedDataIndex.add(index);
//		centerData[0] = alldata[index].clone();
//		for (int i = 0; i < classNum - 1; i++) {
//			index = getMaximalDisData(selectedDataIndex);
//			selectedDataIndex.add(index);
//			centerData[i + 1] = alldata[index].clone();
//		}
//		return centerData;

	}

	public int getMaximalDisData(ArrayList<Integer> selectedDataIndex) {
		double disMax = 0;
		int maxIndex = 0;
		for (int i = 0; i < alldata.length; i++) {
			if (selectedDataIndex.contains(i))
				continue;
			double disSum = 0;
			for (int d : selectedDataIndex) {
				disSum += distance(alldata[d], alldata[i]);
			}
			if (disSum > disMax) {
				disMax = disSum;
				maxIndex = i;
			}

		}
		return maxIndex;
	}

	public double distance(double[] p1, double[] p2) {
		double sum = 0;
		for (int i = 0; i < p1.length; i++) {
			sum += Math.pow(p1[i] - p2[i], 2) /* weight[i] */;
		}
		if (sum == 0)
			return 0d;
		return Math.sqrt(sum);

	}
	public double squraedError(double[] p1, double[] p2) {
		double sum = 0;
		for (int i = 0; i < p1.length; i++) {
			sum += Math.pow(p1[i] - p2[i], 2) /* weight[i] */;
		}
		if (sum == 0)
			return 0d;
		return sum/p1.length;

	}

	private double[] getFeatureData(Field[] fields, T data) {
		double[] features = new double[fieldNum];
		int index = 0;
		for (Field field : fields) {
			Annotation kmeansAnnotation = field
					.getAnnotation(KmeansField.class);
			if (kmeansAnnotation != null) {
				try {
					field.setAccessible(true);
					if (field.get(data).getClass().isArray()) {
						double[] d = (double[]) field.get(data);
						for (double dd : d) {
							features[index] = dd;
							index++;
						}
					} else {
						features[index] = field.getDouble(data);
						index++;
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return features;
	}

	private List<String> getFeatureString(Field[] fields, T data) {
		List<String> features = new ArrayList<String>();
		//List<Double> weights = new ArrayList<Double>();
		for (Field field : fields) {
			Annotation kmeansAnnotation = field
					.getAnnotation(KmeansField.class);
			if (kmeansAnnotation != null) {
				try {
					Field weightField = data.getClass().getDeclaredField(
							field.getName() + "Weight");
					weightField.setAccessible(true);
					field.setAccessible(true);
					if (field.get(data).getClass().isArray()) {

						double[] d = (double[]) field.get(data);

						for (int i = 0; i < d.length; i++) {
							features.add(field.getName() + i);
							//weights.add(weightField.getDouble(temp) / d.length);
						}

					} else {
						features.add(field.getName());
						//weights.add(weightField.getDouble(temp));
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		//weight = weights.toArray(new Double[0]);
		return features;
	}

	public List<String> getFieldNameStringIncBP(T data) {
		List<String> features = new ArrayList<String>();
		Field[] fields = data.getClass().getDeclaredFields();
		for (Field field : fields) {
			Annotation kmeansAnnotation = field
					.getAnnotation(KmeansField.class);
			Annotation bpAnnotation = field.getAnnotation(BPField.class);
			if (kmeansAnnotation != null || bpAnnotation != null) {
				try {
					field.setAccessible(true);
					if (field.get(data).getClass().isArray()) {

						double[] d = (double[]) field.get(data);
						for (int i = 0; i < d.length; i++)
							features.add(field.getName() + i);

					} else {
						features.add(field.getName());
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return features;
	}

	public ArrayList<T>[] comput() {
		fMaxRadius = new ArrayList[getClassNum()];
		fAvgRadius = new ArrayList[getClassNum()];
		globalErrors.clear();
		normResults = new double[getClassNum()][][];

		for (int i = 0; i < getClassNum(); i++) {
			fMaxRadius[i] = new ArrayList<Double>();
			fAvgRadius[i] = new ArrayList<Double>();
		}
		@SuppressWarnings("unchecked")
		List<Integer>[] results = new ArrayList[getClassNum()];
		@SuppressWarnings("unchecked")
		ArrayList<T>[] unNorresults = new ArrayList[getClassNum()];
		@SuppressWarnings("unchecked")
		List<Integer>[] minDataset = new ArrayList[getClassNum()];
		@SuppressWarnings("unchecked")
		List<Double>[] maxRadius = new ArrayList[getClassNum()];
		@SuppressWarnings("unchecked")
		List<Double>[] avgRadius = new ArrayList[getClassNum()];
		List<Double> errors = new ArrayList<Double>();
		
		for (int i = 0; i < getClassNum(); i++) {
			results[i] = new ArrayList<Integer>();
			unNorresults[i] = new ArrayList<T>();
			minDataset[i] = new ArrayList<Integer>();
			maxRadius[i] = new ArrayList<Double>();
			avgRadius[i] = new ArrayList<Double>();
		}
		
		int times = 0;
		double minGlobalError = Double.MAX_VALUE;

		while (times < clusterInfo.getEpoch()) {
			if(cancled){
				cancled=false;
				break;
			}
			double globalError = 0;
			int iterate=1;
			boolean centerchange = true;
			double[][] centerData = getCenter();
			double[] maxDistance = new double[getClassNum()];
			double[] sumDistance = new double[getClassNum()];
			double preGlobalError=0;
			while (centerchange) {
				preGlobalError=globalError;
				globalError=0;
				for (int i = 0; i < getClassNum(); i++) {
					results[i].clear();
					maxDistance[i] = Double.MIN_VALUE;
					sumDistance[i] = 0;
				}

				for (int dataIndex = 0; dataIndex < alldata.length; dataIndex++) {
					double[] dists = new double[getClassNum()];
					for (int classIndex = 0; classIndex < getClassNum(); classIndex++) {
						dists[classIndex] = distance(centerData[classIndex],
								alldata[dataIndex]);
					}
					int min_dist_index = computOrder(dists);
					results[min_dist_index].add(dataIndex);
					if (dists[min_dist_index] > maxDistance[min_dist_index]) {
						maxDistance[min_dist_index] = dists[min_dist_index];
					}
					sumDistance[min_dist_index] += dists[min_dist_index];
					globalError+=dists[min_dist_index];
				}
				
				for (int i = 0; i < getClassNum(); i++) {
					maxRadius[i].add(maxDistance[i]);
					avgRadius[i].add(sumDistance[i] / results[i].size());
					//globalError += sumDistance[i];
				}
				// globalError = globalError / alldata.length;
				errors.add(globalError);
//				System.out.println(times + "," + iterate + ","
//						+ globalError);

				for (int i = 0; i < getClassNum(); i++) {
					double[] data_new;
					if (results[i].size() == 0) {
						data_new = findOneCenter(results[i], i, centerData);
					} else {
						data_new = buildNewCenter(results[i]);
					}
					double[] data_old = centerData[i];
					centerData[i] = data_new;
					if (!isDataEqual(data_new, data_old)) {
						centerchange = true;					
					}

				}
				if(Math.abs(globalError-preGlobalError)<clusterInfo.getTolerance()){
					centerchange = false;
				}
				if(iterate>clusterInfo.getMaxIterate()){
					break;
				}
				iterate++;

			}
			if (globalError < minGlobalError) {
				minGlobalError = globalError;
				fCenterData = centerData;
				fErrors = new ArrayList<Double>(errors);
				for (int i = 0; i < getClassNum(); i++) {
					minDataset[i] = new ArrayList<Integer>(results[i]);
					fAvgRadius[i] = new ArrayList<Double>(avgRadius[i]);
					fMaxRadius[i] = new ArrayList<Double>(maxRadius[i]);
				}
			}
			globalErrors.add(globalError);
			minimalGlobalError=minGlobalError;
			errors.clear();
			for (int i = 0; i < getClassNum(); i++) {
				avgRadius[i].clear();
				maxRadius[i].clear();
			}
			times++;
		}
		
		
		for (int i = 0; i < minDataset.length; i++) {
			int j=0;
			normResults[i]=new double[minDataset[i].size()][];
			for (int index : minDataset[i]) {
				unNorresults[i].add(unNormalizedData.get(index));
				normResults[i][j]=alldata[index];
				j++;
			}
		}
		classifiedDataset = unNorresults;
		//removeEmptyClass();
		return unNorresults;
	}

	public boolean isDataEqual(double[] p1, double[] p2) {

		for (int i = 0; i < p1.length; i++) {
			if (Math.abs(p1[i] - p2[i])>0.001) {
				return false;
			}
		}
		return true;
	}
	public ArrayList<Double>[] getSilhouette (){
		ArrayList<Double>[] silhout=new ArrayList[normResults.length];
		for(int i=0;i<normResults.length;i++){
			silhout[i]=new ArrayList<Double>();
			for(int j=0;j<normResults[i].length;j++){
				
				double sum=0;
				for(int k=0;k<normResults[i].length;k++){
					if(j==k){
						continue;
					}
					
					sum+=distance(normResults[i][j],normResults[i][k]);
				}
				double inner=sum/(normResults[i].length-1);
				sum=0;
				int num=0;
				for(int l=0;l<normResults.length;l++){
					if(l==i){
						continue;
					}
					for(int m=0;m<normResults[l].length;m++){
						sum+=distance(normResults[i][j],normResults[l][m]);
						num++;
					}
					
				}
				double outer=sum/num;
				silhout[i].add((outer-inner)/Math.max(inner, outer));
			}
			
		}
		return silhout;
	}
	
	public double getElbow(){
		double within=0,between=0;
		for(int l=0;l<normResults.length;l++){
			
			for(int i=0;i<normResults[l].length;i++){
				within+=squraedError(fCenterData[l],normResults[l][i]);
				
				for(int j=0;j<fCenterData.length;j++){
					if(l==j){
						continue;
					}
					between+=squraedError(fCenterData[j],normResults[l][i])/(fCenterData.length-1);
				}
			}			
			
		}
		if(between!=0){
			return within/between;
		}
		return Double.NaN;
	}

	private double[] findOneCenter(List<Integer> ps, int classIndex,
			double[][] centerData) {

		double maxdist = 0;
		int maxIndex = 0;
		int dataIndex = 0;
		for (double[] d : alldata) {
			double sumdist = 0;
			for (int i = 0; i < getClassNum(); i++) {
				if (i == classIndex)
					continue;
				sumdist += distance(centerData[i], d);
			}
			if (sumdist > maxdist) {
				maxdist = sumdist;
				maxIndex = dataIndex;
			}
			dataIndex++;
		}
		return alldata[maxIndex].clone();
		// return alldata[new Random().nextInt(alldata.length)].clone();
	}

	public double[] buildNewCenter(List<Integer> ps) {
		try {

			double[] newCenter = new double[fieldNum];
			double[] ds = new double[fieldNum];
			for (int dataIndex : ps) {
				for (int fieldIndex = 0; fieldIndex < fieldNum; fieldIndex++) {
					ds[fieldIndex] += alldata[dataIndex][fieldIndex];
				}
			}
			for (int fieldIndex = 0; fieldIndex < fieldNum; fieldIndex++) {
				newCenter[fieldIndex] = ds[fieldIndex] / ps.size();
			}
			return newCenter;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public int computOrder(double[] dists) {
		double min = dists[0];
		int index = 0;
		for (int i = 1; i < dists.length; i++) {
			double dist1 = dists[i];
			if (min > dist1) {
				min = dist1;
				index = i;
			}
		}
		return index;
	}

	public int patternRecognition(T d) {
		Normalizer.<T>normalize(d, minMax, new Class[]{KmeansField.class});
		double[] dis = new double[fCenterData.length];
		double[] features = getFeatureData(d.getClass().getDeclaredFields(), d);
		for (int i = 0; i < fCenterData.length; i++) {
			dis[i] = distance(fCenterData[i], features);
		}
		return computOrder(dis);
	}

	@SuppressWarnings("unchecked")
	public void removeEmptyClass() {
		// ArrayList<Double []> r=new ArrayList<Double[]>();
		double[][] r = new double[getClassNum()][fieldNum];
		List<T>[] t = new ArrayList[getClassNum()];
		int finalClassNum = 0;
		for (int i = 0; i < fCenterData.length; i++) {
			if (classifiedDataset[i].size() > 0) {
				r[finalClassNum] = fCenterData[i];
				t[finalClassNum] = classifiedDataset[i];
				finalClassNum++;
			}
		}
		finalClassNumber = finalClassNum;
		fCenterData = new double[finalClassNum][fieldNum];
		System.arraycopy(r, 0, fCenterData, 0, finalClassNum);
		classifiedDataset = new ArrayList[finalClassNum];
		System.arraycopy(t, 0, classifiedDataset, 0, finalClassNum);

	}

	public Double[][] getUnnormalizedData() {
		// TODO Auto-generated method stub
		return unNormalize(alldata);
	}

	public Double[][] getUnnormalizedCenter() {

		return unNormalize(fCenterData);
	}

	public Double[][] unNormalize(double[][] normalizedData) {
		Double[][] unNormalizedData = new Double[normalizedData.length][fieldNum];
		for (int i = 0; i < normalizedData.length; i++) {
			for (int j = 0; j < fieldNum; j++) {
				unNormalizedData[i][j] = normalizedData[i][j]
						* (minMaxMatrix[1][j] - minMaxMatrix[0][j])
						+ minMaxMatrix[0][j];
			}
		}
		return unNormalizedData;
	}

	public int getDataNum() {
		// TODO Auto-generated method stub
		return alldata.length;
	}

	public ArrayList<Double>[] getAvgRadius() {
		return fAvgRadius;
	}

	public void setAvgRadius(ArrayList<Double>[] avgRadius) {
		this.fAvgRadius = avgRadius;
	}

	public List<T>[] getClassifiedDataset() {
		return classifiedDataset;
	}

	public void setClassifiedDataset(List<T>[] classifiedDataset) {
		this.classifiedDataset = classifiedDataset;
	}

	public List<T> getUnNormalizedData() {
		return unNormalizedData;
	}

	public void setUnNormalizedData(List<T> unNormalizedData) {
		this.unNormalizedData = unNormalizedData;
	}

	public int getFinalClassNumber() {
		//return finalClassNumber;
		return clusterInfo.getClassNum();
	}

	public void setFinalClassNumber(int finalClassNumber) {
		this.finalClassNumber = finalClassNumber;
	}

	public ClusterInfo getClusterInfo() {
		return clusterInfo;
	}

	public void setClusterInfo(ClusterInfo clusterInfo) {
		this.clusterInfo = clusterInfo;
	}

	public ArrayList<Double> getGlobalErrors() {
		return globalErrors;
	}

	public void setGlobalErrors(ArrayList<Double> globalErrors) {
		this.globalErrors = globalErrors;
	}

	public List<T> getNormalizedData() {
		return normalizedData;
	}

	public void setNormalizedData(List<T> normalizedData) {
		this.normalizedData = normalizedData;
	}

	public double getMinimalGlobalError() {
		return minimalGlobalError;
	}

	public void setMinimalGlobalError(double minimalGlobalError) {
		this.minimalGlobalError = minimalGlobalError;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public List<Double> getClassNumDetermineData() {
		return classNumDetermineData;
	}

	public void setClassNumDetermineData(List<Double> classNumDetermineData) {
		this.classNumDetermineData = classNumDetermineData;
	}

	public boolean isCancled() {
		return cancled;
	}

	public void setCancled(boolean cancled) {
		this.cancled = cancled;
	}

}
