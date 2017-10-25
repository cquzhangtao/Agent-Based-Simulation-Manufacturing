package manu.scheduling.training;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BPNetwork<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[] input;
	private double[] hidden;
	private double[] output;
	private double[] target;
	
	private double[] hidDelta;
	private double[] optDelta1;
	private double[] optDelta;
	
	private double[][] iptHidWeights;
	private double[][] hidOptWeights;
	private double[][] iptHidPrevUptWeights;
	private double[][] hidOptPrevUptWeights;
	
	private String outputTransferFnc = "Sigmoid";// "Linear";//"Sigmoid";

	private double[][] inputData;
	private double[][] outputData;
	private double[][] inputDataTest;
	private double[][] outputDataTest;
	private double[][] inputDataValidate;
	private double[][] outputDataValidate;
	private double[][] testResult;
	private double[][] validateResult;
	
	private Random random;
	private BPInfo bpInfo;
	private boolean finished;

	private T[] minMax;
	private double accuracy = 0;
	private boolean cancled=false;

	private ArrayList<String> inputFieldName = new ArrayList<String>();
	private ArrayList<String> outputFieldName = new ArrayList<String>();
	private ArrayList<String> inputFieldName1 = new ArrayList<String>();
	private ArrayList<String> outputFieldName1 = new ArrayList<String>();

	private ArrayList<Double> outputErrors = new ArrayList<Double>();
	private ArrayList<Double> outputTestErrors = new ArrayList<Double>();
	private ArrayList<Double> outputValidateErrors = new ArrayList<Double>();
	private ArrayList<Double> hiddenErrors = new ArrayList<Double>();

	private ArrayList<Double> increasedNumList = new ArrayList<Double>();
	private ArrayList<Double> accuracyList = new ArrayList<Double>();
	private String endReason = "";
	private List<T> trainingDataset;
	
	public BPNetwork(List<T> dataset, BPInfo bpInfo) {
		this.bpInfo=bpInfo;
		this.trainingDataset=dataset;
		
		// get test dataset
		setRelatedFields(dataset.get(0));
		List<T> datasetN=new ArrayList<T>(dataset);
		randomSort(datasetN);
		ArrayList<T> testDataset = new ArrayList<T>();
		ArrayList<T> validateDataset = new ArrayList<T>();
		Random rnd = new Random(19821221);
		for (T data : datasetN) {
			double d = rnd.nextDouble();
			if (d > 0.8) {
				testDataset.add(data);
			}
			if (d > 0.6 && d <= 0.8) {
				validateDataset.add(data);
			}
		}
		datasetN.removeAll(testDataset);
		datasetN.removeAll(validateDataset);

		inputData = new double[datasetN.size()][inputFieldName1.size()];
		outputData = new double[datasetN.size()][outputFieldName1.size()];
		inputDataTest = new double[testDataset.size()][inputFieldName1.size()];
		outputDataTest = new double[testDataset.size()][outputFieldName1.size()];
		inputDataValidate = new double[validateDataset.size()][inputFieldName1
				.size()];
		outputDataValidate = new double[validateDataset.size()][outputFieldName1
				.size()];
		getDataFromListT(datasetN, inputData, outputData);
		getDataFromListT(testDataset, inputDataTest, outputDataTest);
		getDataFromListT(validateDataset, inputDataValidate, outputDataValidate);

		
	}
	
	public BPNetwork() {
		// TODO Auto-generated constructor stub
	}

	public void randomInit(T t,BPInfo bpInfo){
		setRelatedFields(t);
		init(inputFieldName1.size(), bpInfo.getHiddenNodeNum(),
				outputFieldName1.size());
	}
	
	public void clear() {
		outputErrors = null;
		hiddenErrors = null;
		inputData = null;
		outputData = null;
		inputDataTest = null;
		outputDataTest = null;
		inputDataValidate = null;
		outputDataValidate = null;
		testResult = null;
		validateResult=null;
		outputTestErrors=null;
		outputValidateErrors=null;
		increasedNumList=null;
		accuracyList=null;
		trainingDataset=null;
		
	}


	public String getEndReason() {
		return endReason;
	}

	public void setEndReason(String endReason) {
		this.endReason = endReason;
	}
	public ArrayList<Double> getAccuracyList() {
		return accuracyList;
	}

	public void setAccuracyList(ArrayList<Double> accuracyList) {
		this.accuracyList = accuracyList;
	}

	public ArrayList<Double> getIncreasedNumList() {
		return increasedNumList;
	}

	public void setIncreasedNumList(ArrayList<Double> increasedNumList) {
		this.increasedNumList = increasedNumList;
	}

	public ArrayList<Double> getOutputTestErrors() {
		return outputTestErrors;
	}

	public void setOutputTestErrors(ArrayList<Double> outputTestErrors) {
		this.outputTestErrors = outputTestErrors;
	}

	public ArrayList<Double> getOutputValidateErrors() {
		return outputValidateErrors;
	}

	public void setOutputValidateErrors(ArrayList<Double> outputValidateErrors) {
		this.outputValidateErrors = outputValidateErrors;
	}



	public ArrayList<Double> getHiddenErrors() {
		return hiddenErrors;
	}

	public void setHiddenErrors(ArrayList<Double> hiddenErrors) {
		this.hiddenErrors = hiddenErrors;
	}

	public ArrayList<String> getInputFieldName1() {
		return inputFieldName1;
	}

	public void setInputFieldName1(ArrayList<String> inputFieldName1) {
		this.inputFieldName1 = inputFieldName1;
	}

	public ArrayList<String> getOutputFieldName1() {
		return outputFieldName1;
	}

	public void setOutputFieldName1(ArrayList<String> outputFieldName1) {
		this.outputFieldName1 = outputFieldName1;
	}

	public double[][] getInputData() {
		return inputData;
	}

	public void setInputData(double[][] inputData) {
		this.inputData = inputData;
	}

	public T[] getMinMax() {
		return minMax;
	}

	public void setMinMax(T[] minMax) {
		this.minMax = minMax;
	}
	
	public Double[][] getUnnormalizedValidateResult() {
		if (validateResult != null && validateResult.length > 0)
			return Normalizer.unNormalize(validateResult, outputFieldName, minMax);
		else
			return null;
	}
	public Double[][] getUnnormalizedValidateOutput() {
		if (outputDataValidate != null && outputDataValidate.length > 0)
			return Normalizer.unNormalize(outputDataValidate, outputFieldName, minMax);
		else
			return null;
	}
	public Double[][] getUnnormalizedValidateInput() {
		if (inputDataValidate != null && inputDataValidate.length > 0)
			return Normalizer.unNormalize(inputDataValidate, inputFieldName, minMax);
		else
			return null;
	}

	public Double[][] getUnnormalizedTestResult() {
		if (testResult != null && testResult.length > 0)
			return Normalizer.unNormalize(testResult, outputFieldName, minMax);
		else
			return null;
	}

	public Double[][] getUnnormalizedTestOutput() {

		if (outputDataTest != null && outputDataTest.length > 0)
			return Normalizer.unNormalize(outputDataTest, outputFieldName,
					minMax);
		else
			return null;
	}

	public Double[][] getUnnormalizedTestInput() {

		if (inputDataTest != null && inputDataTest.length > 0)
			return Normalizer
					.unNormalize(inputDataTest, inputFieldName, minMax);
		else
			return null;
	}

	public Double[][] getUnnormalizedInput() {
		if (inputData != null && inputData.length > 0)
			return Normalizer.unNormalize(inputData, inputFieldName, minMax);
		else
			return null;
	}

	public Double[][] getUnnormalizedOutput() {
		if (outputData != null && outputData.length > 0)
			return Normalizer.unNormalize(outputData, outputFieldName, minMax);
		else
			return null;
	}

	

	public int getTraningDataNum() {
		return inputData.length;
	}

	public int getTestDataNum() {
		return inputDataTest.length;
	}

	public int getInputNum() {
		return input.length - 1;
	}

	public int getOutputNum() {
		return output.length - 1;
	}

	public int getHiddenNodeNum() {
		return hidden.length - 1;
	}


//	public double getGlobalError() {
//		return globalOptError;
//	}
//
//	public void setGlobalError(double globalError) {
//		this.globalOptError = globalError;
//	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public ArrayList<Double> getErrors() {
		return outputErrors;
	}

	public void setErrors(ArrayList<Double> errors) {
		this.outputErrors = errors;
	}

	public void init(int inputSize, int hiddenSize, int outputSize) {

		input = new double[inputSize + 1];
		hidden = new double[hiddenSize + 1];
		output = new double[outputSize + 1];
		target = new double[outputSize + 1];

		hidDelta = new double[hiddenSize + 1];
		optDelta = new double[outputSize + 1];
		optDelta1 = new double[outputSize + 1];

		iptHidWeights = new double[inputSize + 1][hiddenSize + 1];
		hidOptWeights = new double[hiddenSize + 1][outputSize + 1];

		random = new Random();
		randomizeWeights(iptHidWeights);
		randomizeWeights(hidOptWeights);

		iptHidPrevUptWeights = new double[inputSize + 1][hiddenSize + 1];
		hidOptPrevUptWeights = new double[hiddenSize + 1][outputSize + 1];

	}

	private void randomizeWeights(double[][] matrix) {
		for (int i = 0, len = matrix.length; i != len; i++)
			for (int j = 0, len2 = matrix[i].length; j != len2; j++) {
				double real = random.nextDouble();
				matrix[i][j] = random.nextDouble() > 0.5 ? real : -real;
			}
	}



	private void setRelatedFields(T t) {
		Field[] fields = t.getClass().getDeclaredFields();
		for (Field field : fields) {
			Annotation bpAnnotation = field.getAnnotation(BPField.class);
			Annotation bpoutAnnotation = field.getAnnotation(BPOutput.class);
			field.setAccessible(true);
			// Annotation kmeansAnnotation =
			// field.getAnnotation(KmeanField.class);
			if (bpAnnotation != null && bpoutAnnotation == null) {
				inputFieldName.add(field.getName());
				try {
					if (field.get(t).getClass().isArray()) {
						double[] d = null;

						d = (double[]) field.get(t);

						for (int i = 0; i < d.length; i++) {
							inputFieldName1.add(field.getName() + i);
						}
						// inputFieldSize.add(d.length);
					} else {
						inputFieldName1.add(field.getName());
						// inputFieldSize.add(1);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (bpAnnotation != null && bpoutAnnotation != null) {
				outputFieldName.add(field.getName());
				try {
					if (field.get(t).getClass().isArray()) {
						double[] d = null;

						d = (double[]) field.get(t);

						for (int i = 0; i < d.length; i++) {
							outputFieldName1.add(field.getName() + i);
						}
					} else {
						outputFieldName1.add(field.getName());
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
	}

	private void randomSort(List<T> dataset) {
		Random random=new Random();
		for (int index = dataset.size() - 1; index >= 0; index--) {
			int newindex = random.nextInt(index + 1);
			T t = dataset.get(index);
			dataset.set(index, dataset.get(newindex));
			dataset.set(newindex, t);
		}
	}

	private void createNetwork( BPInfo bpInfo) {
		
		init(inputFieldName1.size(), bpInfo.getHiddenNodeNum(),
				outputFieldName1.size());
	
	}

	private void getDataFromListT(List<T> dataset, double[][] inputData,
			double[][] outputData) {
		int indexd = 0;
		for (T data : dataset) {
			int indexI = 0;
			for (String fieldName : inputFieldName) {
				try {
					Field field = data.getClass().getDeclaredField(fieldName);
					field.setAccessible(true);

					if (field.get(data).getClass().isArray()) {
						double[] d = (double[]) field.get(data);
						for (int i = 0; i < d.length; i++) {
							inputData[indexd][indexI] = d[i];
							indexI++;
						}
					} else {
						inputData[indexd][indexI] = field.getDouble(data);
						indexI++;
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
			int indexO = 0;
			for (String fieldName : outputFieldName) {

				try {
					Field field = data.getClass().getDeclaredField(fieldName);
					field.setAccessible(true);
					if (field.get(data).getClass().isArray()) {
						double[] d = (double[]) field.get(data);
						for (int i = 0; i < d.length; i++) {
							outputData[indexd][indexO] = d[i];
							indexO++;
						}
					} else {
						outputData[indexd][indexO] = field.getDouble(data);
						indexO++;
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
			indexd++;
		}

	}

	public void train(double[] trainData, double[] target) {
		loadInput(trainData);
		loadTarget(target);
		forward();
		calculateDelta();
		adjustWeight();
	}

	public void train(double[][] trainData, double[][] target,
			double[][] validateData, double[][] validateTarget,
			double[][] testData, double[][] testTarget) {
		
		double preValidatePerf = Double.MAX_VALUE;
		outputValidateErrors.clear();
		boolean validatePerfIncreased = false;
		int increasedNum = 0;
		increasedNumList.clear();
		outputTestErrors.clear();
		accuracyList.clear();
		outputErrors.clear();
		hiddenErrors.clear();
		
		for (int j = 0; j < bpInfo.getEpoch(); j++) {
			if(cancled){
				cancled=false;
				break;
			}
			double globalOptError = 0;
			double globalHidError = 0;
			double accuracy = 0;
			if (validateData.length > 0) {
				for (int i = 0; i < validateData.length; i++) {
					loadInput(validateData[i]);
					loadTarget(validateTarget[i]);
					forward();
					globalOptError += calculateDelta()[0];
				}
				globalOptError = globalOptError / validateData.length / 2.0;
				outputValidateErrors.add(globalOptError);
				if (globalOptError > preValidatePerf && !validatePerfIncreased) {
					validatePerfIncreased = true;
				}
				if (globalOptError <= preValidatePerf) {
					validatePerfIncreased = false;
					increasedNum = 0;
				}
				if (globalOptError > preValidatePerf && validatePerfIncreased) {
					increasedNum++;
				}
				preValidatePerf = globalOptError;
				increasedNumList.add((double) increasedNum);
			}
			if (testData.length > 0) {
				globalOptError = 0;
				double[][] results = new double[testData.length][output.length];
				for (int i = 0; i < testData.length; i++) {
					loadInput(testData[i]);
					loadTarget(testTarget[i]);
					forward();
					globalOptError +=calculateDelta()[0];
					results[i] = getNetworkOutput();
				}
				globalOptError = globalOptError / testData.length / 2.0;
				outputTestErrors.add(globalOptError);

				double[] ev = evaluate(Normalizer.unNormalize(results,
						outputFieldName, minMax), Normalizer.unNormalize(
						testTarget, outputFieldName, minMax));
				accuracyList.add(ev[1]);
				accuracy = ev[1];
			}

			globalOptError = 0;
			for (int i = 0; i < trainData.length; i++) {
				loadInput(trainData[i]);
				loadTarget(target[i]);
				forward();
				double[] eros=calculateDelta();
				globalOptError += eros[0];
				globalHidError += eros[1];
				adjustWeight();
			}
			globalOptError = globalOptError / trainData.length / 2.0;
			outputErrors.add(globalOptError);
			hiddenErrors.add(globalHidError / trainData.length);

			if (reachMinGradient(outputErrors)) {
				endReason = "Reach minimal gradient";
				return;
			}
			if (increasedNum > bpInfo.getMaxFailNum() && accuracy > .6) {
				endReason = "Reach maximal fail number";
				return;
			}
			//System.out.println("epoch="+j+",error="+globalOptError+",accuracy="+accuracy);
		}
		endReason = "Reach maximal epoch";
		//System.out.println("accuracy="+accuracy);
	}

	private boolean reachMinGradient(ArrayList<Double> errors) {
		int stepNum = 100;
		if (errors.size() < stepNum) {
			return false;
		}
		double sum = 0;
		for (int i = errors.size() - 1; i > errors.size() - stepNum; i--) {
			sum += Math.abs(errors.get(i) - errors.get(i - 1));
		}
		if (sum > bpInfo.getMinGradient()) {
			return false;
		}
		return true;

	}

	public void trainB(double[][] trainData, double[][] target) {
		for (int j = 0; j < bpInfo.getEpoch(); j++) {
			double globalOptError = 0;
			double globalHidError = 0;
			for (int i = 0; i < trainData.length; i++) {
				loadInput(trainData[i]);
				loadTarget(target[i]);
				forward();
				double [] eros=calculateDelta();
				globalOptError += eros[0];
				globalHidError += eros[1];

			}
			adjustWeight();
			globalOptError = globalOptError / trainData.length / 2.0;
			outputErrors.add(globalOptError);
			// hiddenErrors.add(globalHidError / trainData.length);
		}
	}

	public void train() {
		createNetwork( bpInfo);
		train(inputData, outputData, inputDataValidate, outputDataValidate,
				inputDataTest, outputDataTest);
		testResult = test(inputDataTest);	
		accuracy=evaluate(this.getUnnormalizedTestResult(),this.getUnnormalizedTestOutput())[1];
		validateResult=test(inputDataValidate);
	}

	public double[] test(double[] inData) {
		if (inData.length != input.length - 1) {
			throw new IllegalArgumentException("Size Do Not Match.");
		}
		System.arraycopy(inData, 0, input, 1, inData.length);
		forward();
		return getNetworkOutput();
	}

	public double[][] test(double[][] inData) {
		if (inData.length == 0)
			return null;
		double[][] results = new double[inData.length][inData[0].length];
		for (int i = 0; i < inData.length; i++) {
			results[i] = test(inData[i]);
		}
		return results;
	}

	private double[] evaluate(Double[][] outputD, Double[][] target) {

		if (outputD == null) {
			return null;
		}
		int sum = 0;
		double globalOptError = 0;
		double accuracy;

		for (int i = 0; i < outputD.length; i++) {
			double ierror = 0;
			double ierror1 = 0;
			for (int j = 0; j < outputD[i].length; j++) {
				ierror += Math.pow(outputD[i][j] - target[i][j], 2);
				if (outputD[i][j] < 10e-5 && target[i][j] < 10e-5) {

				} else {
//					ierror1 += 2 * Math.abs(outputD[i][j] - target[i][j])
//							/ (target[i][j] + outputD[i][j]);
					ierror1 +=  Math.abs(outputD[i][j] - target[i][j])/target[i][j];
				}
			}
			ierror1 /= outputD[i].length;
			if (ierror1 - bpInfo.getTolerance()/100 < 0)
				sum++;
			globalOptError += ierror;
		}
		globalOptError = globalOptError / outputD.length / 2d;
		accuracy = 1d * sum / outputD.length;

		return new double[] { globalOptError, accuracy };
	}

//	private double evaluate() {
//		Double[][] results = getUnnormalizedTestResult();
//		Double[][] outputs = getUnnormalizedTestOutput();
//		if (results == null) {
//			return 0;
//		}
//		int sum = 0;
//		double globalOptError=0;
//		for (int i = 0; i < results.length; i++) {
//			double ierror = 0;
//			double ierror1 = 0;
//			for (int j = 0; j < results[i].length; j++) {
//				ierror += Math.pow(results[i][j] - outputs[i][j], 2);
//				if (results[i][j] < 10e-5 && outputs[i][j] < 10e-5) {
//
//				} else {
////					ierror1 += 2 * Math.abs(results[i][j] - outputs[i][j])
////							/ (outputs[i][j] + results[i][j]);
//					ierror1 +=  Math.abs(results[i][j] - outputs[i][j])/outputs[i][j];
//				}
//			}
//			// ierror /= results[i].length;
//			ierror1 /= results[i].length;
//			if (ierror1 - bpInfo.getTolerance()/100 < 0)
//				sum++;
//			globalOptError += ierror;
//		}
//		globalOptError = globalOptError / inputDataTest.length / 2d;
//		return 1d * sum / inputDataTest.length;
//	}

	public double[]  predict(T inData) {
		
		Normalizer.<T>normalize(inData, minMax, new Class[]{BPField.class});
		double[] data = new double[inputFieldName1.size()];
		int index = 0;
		for (String fieldName : inputFieldName) {

			try {
				Field field = inData.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				if (field.get(inData).getClass().isArray()) {
					double[] d = (double[]) field.get(inData);
					for (int i = 0; i < d.length; i++) {
						data[index] = d[i];
						index++;
					}
				} else {
					data[index] = field.getDouble(inData);
					index++;
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
		index = 0;
		double[] r = test(data);
		for (String fieldName : outputFieldName) {

			try {
				Field field = inData.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				if (field.get(inData).getClass().isArray()) {
					double[] d = new double[((double[]) field.get(minMax[0])).length];
					for (int i = 0; i < d.length; i++) {
						d[i] = r[index];
						index++;
					}
					field.set(inData, d);
				} else {
					field.setDouble(inData, r[index]);
					index++;
				}

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
		return r;
	}

	private double[] getNetworkOutput() {
		int len = output.length;
		double[] temp = new double[len - 1];
		for (int i = 1; i != len; i++)
			temp[i - 1] = output[i];
		return temp;
	}

	private void loadTarget(double[] arg) {
		if (arg.length != target.length - 1) {
			throw new IllegalArgumentException("Size Does Not Match.");
		}
		System.arraycopy(arg, 0, target, 1, arg.length);
	}

	private void loadInput(double[] inData) {
		if (inData.length != input.length - 1) {
			throw new IllegalArgumentException("Size Does Not Match.");
		}
		System.arraycopy(inData, 0, input, 1, inData.length);
	}

	private void forward(String transFnc, double[] layer0, double[] layer1,
			double[][] weight) {
		// threshold unit.
		layer0[0] = 1.0;
		for (int j = 1, len = layer1.length; j != len; ++j) {
			double sum = 0;
			for (int i = 0, len2 = layer0.length; i != len2; ++i)
				sum += weight[i][j] * layer0[i];
			if (transFnc.equals("Sigmoid")) {
				layer1[j] = sigmoid(sum);
			} else if (transFnc.equals("Linear")) {
				layer1[j] = sum;
			} else {
				System.out.println("Transer function error");
			}
		}
	}

	private void forward() {
		forward("Sigmoid", input, hidden, iptHidWeights);
		forward(outputTransferFnc, hidden, output, hidOptWeights);
	}

	/**
	 * Calculate output error.
	 */
	private double outputErr() {
		double errSum = 0;
		for (int idx = 1, len = optDelta.length; idx != len; ++idx) {
			double o = output[idx];
			if (outputTransferFnc.equals("Sigmoid")) {
				optDelta[idx] = o * (1d - o) * (target[idx] - o);
				optDelta1[idx] = o * (1d - o) * (target[idx] - o);
			} else if (outputTransferFnc.equals("Linear")) {
				optDelta[idx] = (target[idx] - o);
				optDelta1[idx] = (target[idx] - o);
			} else {
				System.out.println("output transfer function error");
			}
			// errSum += Math.abs(optDelta[idx]);
			errSum += Math.pow(target[idx] - o, 2);
		}
		return errSum;
	}

	/**
	 * Calculate hidden errors.
	 */
	private double hiddenErr() {
		double errSum = 0;
		for (int j = 1, len = hidDelta.length; j != len; ++j) {
			double o = hidden[j];
			double sum = 0;
			for (int k = 1, len2 = optDelta1.length; k != len2; ++k) {
				sum += hidOptWeights[j][k] * optDelta1[k];
			}
			hidDelta[j] = o * (1d - o) * sum;
			errSum += Math.abs(hidDelta[j]);
		}
		return errSum;
	}

	/**
	 * Calculate errors of all layers.
	 */
	private double[] calculateDelta() {
		return new double[]{outputErr(),hiddenErr()};
	}

	/**
	 * Adjust the weight matrix.
	 */
	private void adjustWeight(double[] delta, double[] layer,
			double[][] weight, double[][] prevWeight) {

		layer[0] = 1;
		for (int i = 1, len = delta.length; i != len; ++i) {
			for (int j = 0, len2 = layer.length; j != len2; ++j) {
				double newVal = bpInfo.getMomentum() * prevWeight[j][i] + bpInfo.getEta() * delta[i]
						* layer[j] * (1 - bpInfo.getMomentum());
				weight[j][i] += newVal;
				prevWeight[j][i] = newVal;
			}
			delta[i] = 0;
		}
	}

	/**
	 * Adjust all weight matrices.
	 */
	private void adjustWeight() {
		adjustWeight(optDelta, hidden, hidOptWeights, hidOptPrevUptWeights);
		adjustWeight(hidDelta, input, iptHidWeights, iptHidPrevUptWeights);
	}

	/**
	 * Sigmoid.
	 */
	private double sigmoid(double val) {
		return 1d / (1d + Math.exp(-val));
	}

	public double[][] getInputMinMaxMatrix() {
		return getMinMaxMatrix(inputFieldName);
	}

	public double[][] getOutputMinMaxMatrix() {
		return getMinMaxMatrix(outputFieldName);
	}

	public double[][] getMinMaxMatrix(ArrayList<String> fieldName) {
		T min = minMax[0];
		T max = minMax[1];
		double[][] result = new double[2][getInputNum()];
		int index = 0;
		for (String name : fieldName) {

			try {
				Field field = min.getClass().getDeclaredField(name);
				field.setAccessible(true);
				if (field.get(min).getClass().isArray()) {
					double[] minv = (double[]) field.get(min);
					double[] maxv = (double[]) field.get(max);
					for (int i = 0; i < minv.length; i++) {
						result[0][index] = minv[i];
						result[1][index] = maxv[i];
						index++;
					}
				} else {
					double minv = field.getDouble(min);
					double maxv = field.getDouble(max);
					result[0][index] = minv;
					result[1][index] = maxv;
					index++;
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
		return result;
	}

	public int getValidateDataNum() {
		// TODO Auto-generated method stub
		return inputDataValidate.length;
	}


	public BPInfo getBpInfo() {
		return bpInfo;
	}


	public void setBpInfo(BPInfo bpInfo) {
		this.bpInfo = bpInfo;
	}

	public List<T> getTrainingDataset() {
		return trainingDataset;
	}

	public void setTrainingDataset(List<T> trainingDataset) {
		this.trainingDataset = trainingDataset;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isCnacled() {
		return cancled;
	}

	public void setCancled(boolean cnacled) {
		this.cancled = cnacled;
	}
}
