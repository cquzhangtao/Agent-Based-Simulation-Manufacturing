package manu.scheduling.training;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Normalizer {

	public static <T> T[] normalize(List<T> originalDataset,
			List<T> newDataset, Class<Annotation>[] fieldAnno) {
		@SuppressWarnings("unchecked")
		Class<T> classT = (Class<T>) originalDataset.get(0).getClass();
		T[] minMax = getMinMax(originalDataset, fieldAnno);
		Field[] fields = classT.getDeclaredFields();
		for (T data : originalDataset) {
			T newData = null;
			try {
				newData = classT.newInstance();
				for (Field field : fields) {
					field.setAccessible(true);
					boolean hasNormalizedField = false;
					for (Class<Annotation> c : fieldAnno) {
						if (field.getAnnotation(c) != null) {
							hasNormalizedField = true;
							break;
						}
					}
					if (!hasNormalizedField) {
						continue;
					}
					if (field.get(data).getClass().isArray()) {
						double[] vo = (double[]) field.get(data);
						double[] vn = new double[vo.length];
						double[] min = (double[]) field.get(minMax[0]);
						double[] max = (double[]) field.get(minMax[1]);
						for (int i = 0; i < vo.length; i++) {
							double dis = max[i] - min[i];
							if (dis == 0) {
								vn[i] = 0;
							} else {
								vn[i] = (vo[i] - min[i]) / dis;
							}
						}
						field.set(newData, vn);
					} else {
						double newValue = field.getDouble(minMax[1])
								- field.getDouble(minMax[0]);
						if (newValue != 0)
							field.setDouble(
									newData,
									(field.getDouble(data) - field
											.getDouble(minMax[0])) / newValue);
						else
							field.setDouble(newData, 0.0);
					}
				}

			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			newDataset.add(newData);
		}

		return minMax;

	}

	public static <T> T[] getMinMax(List<T> originalDataset,
			Class<Annotation>[] fieldAnno) {
		@SuppressWarnings("unchecked")
		Class<T> classT = (Class<T>) originalDataset.get(0).getClass();
		@SuppressWarnings("unchecked")
		T[] minMax = (T[]) Array.newInstance(classT, 2);
		try {
			minMax[0] = classT.newInstance();
			minMax[1] = classT.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Field[] fields = classT.getDeclaredFields();
		int index = 0;
		for (T data : originalDataset) {
			for (Field field : fields) {
				field.setAccessible(true);
				boolean hasNormalizedField = false;
				for (Class<Annotation> c : fieldAnno) {
					if (field.getAnnotation(c) != null) {
						hasNormalizedField = true;
						break;
					}
				}
				if (!hasNormalizedField) {
					continue;
				}
				try {
					if (field.get(data).getClass().isArray()) {
						if (index == 0) {
							int length = Array.getLength(field.get(data));
							double[] min = new double[length];
							double[] max = new double[length];
							for (int i = 0; i < length; i++) {
								min[i] = Double.MAX_VALUE;
								max[i] = Double.MIN_VALUE;
							}
							field.set(minMax[0], min);
							field.set(minMax[1], max);
						} else {
							double[] v = (double[]) field.get(data);
							double[] min = (double[]) field.get(minMax[0]);
							double[] max = (double[]) field.get(minMax[1]);
							for (int i = 0; i < v.length; i++) {
								if (v[i] < min[i]) {
									min[i] = v[i];
								}
								if (v[i] > max[i]) {
									max[i] = v[i];
								}
							}

						}

					} else {
						if (index == 0) {
							field.setDouble(minMax[0], Double.MAX_VALUE);
							field.setDouble(minMax[1], Double.MIN_VALUE);
						} else {
							if (field.getDouble(data) < field
									.getDouble(minMax[0])) {
								field.setDouble(minMax[0],
										field.getDouble(data));
							}
							if (field.getDouble(data) > field
									.getDouble(minMax[1])) {
								field.setDouble(minMax[1],
										field.getDouble(data));
							}
						}
					}
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			index=2;
		}
		return minMax;
	}

	public static <T> void normalize(T data, T[] minMax,
			Class<Annotation>[] fieldAno) {

		Field[] fields = data.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			boolean hasNormalizedField = false;
			for (Class<Annotation> c : fieldAno) {
				if (field.getAnnotation(c) != null) {
					hasNormalizedField = true;
					break;
				}
			}
			if (!hasNormalizedField) {
				continue;
			}

			try {
				if (field.get(data).getClass().isArray()) {
					double[] ov = (double[]) field.get(data);
					double[] maxv = (double[]) field.get(minMax[1]);
					double[] minv = (double[]) field.get(minMax[0]);
					for (int i = 0; i < ov.length; i++) {
						double dis = maxv[i] - minv[i];
						if (dis == 0) {
							ov[i] = 0;
						} else {
							ov[i] = (ov[i] - minv[i]) / dis;
						}
					}
					field.set(data, ov);

				} else {
					double dis = field.getDouble(minMax[1])
							- field.getDouble(minMax[0]);
					if (dis != 0) {
						double d = (field.getDouble(data) - field
								.getDouble(minMax[0])) / dis;
						field.setDouble(data, d);
					} else
						field.setDouble(data, 0.0);
				}
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static <T> void unNormalize(T data, T[] minMax,
			Class<Annotation>[] fieldAno) {
		// TrainingData data = new TrainingData();
		Field[] fields = data.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			boolean hasNormalizedField = false;
			for (Class<Annotation> c : fieldAno) {
				if (field.getAnnotation(c) != null) {
					hasNormalizedField = true;
					break;
				}
			}
			if (!hasNormalizedField) {
				continue;
			}
			try {
				if (field.get(data).getClass().isArray()) {
					double[] ov = (double[]) field.get(data);
					double[] maxv = (double[]) field.get(minMax[1]);
					double[] minv = (double[]) field.get(minMax[0]);
					double[] nv = new double[maxv.length];
					for (int i = 0; i < ov.length; i++) {
						double dis = maxv[i] - minv[i];
						nv[i] = ov[i] * dis + minv[i];
					}
					field.set(data, nv);

				} else {
					double differ = field.getDouble(minMax[1])
							- field.getDouble(minMax[0]);
					double d = (field.getDouble(data) * differ + field
							.getDouble(minMax[0]));
					field.setDouble(data, d);
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

	public static <T> Double[][] unNormalize(double[][] normalizedData,
			ArrayList<String> fieldNames, T[] minMax) {
		Double[][] unNormalizedData = new Double[normalizedData.length][normalizedData[0].length];
		for (int i = 0; i < normalizedData.length; i++) {
			int index = 0;
			for (String name : fieldNames) {
				try {
					Field field = minMax[0].getClass().getDeclaredField(name);
					field.setAccessible(true);
					if (field.get(minMax[0]).getClass().isArray()) {
						double[] maxv = (double[]) field.get(minMax[1]);
						double[] minv = (double[]) field.get(minMax[0]);
						for (int j = 0; j < maxv.length; j++) {
							double dis = maxv[j] - minv[j];
							unNormalizedData[i][index] = normalizedData[i][index]
									* dis + minv[j];
							index++;
						}
					} else {
						unNormalizedData[i][index] = normalizedData[i][index]
								* (field.getDouble(minMax[1]) - field
										.getDouble(minMax[0]))
								+ field.getDouble(minMax[0]);
						index++;
					}

				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return unNormalizedData;

	}

}
