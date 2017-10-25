package manu.scheduling.training;

enum JacobianMethod {
	// / <summary>
	// / Computes the Jacobian using approximation by finite differences. This
	// / method is slow in comparison with back-propagation and should be used
	// / only for debugging or comparison purposes.
	// / </summary>
	// /
	ByFiniteDifferences,
	// / <summary>
	// / Computes the Jacobian using back-propagation for the chain rule of
	// / calculus. This is the preferred way of computing the Jacobian.
	// / </summary>
	// /
	ByBackpropagation,
}

public class LevenbergMarquardtLearning {
	private double lambdaMax = 1e25;
	// network to teach
	// private ActivationNetwork network;
	// Bayesian Regularization variables
	private boolean useBayesianRegularization;
	// Bayesian Regularization Hyperparameters
	private double gamma;
	private double alpha;
	private double beta = 1;
	// Levenberg-Marquardt variables
	private float[][] jacobian;
	private float[][] hessian;
	private float[] diagonal;
	private float[] gradient;
	private float[] weights;
	private float[] deltas;
	private double[] errors;
	private JacobianMethod method;
	// Levenberg damping factor
	private double lambda = 0.1;
	// The amount the damping factor is adjusted
	// when searching the minimum error surface
	private double v = 10.0;
	// Total of weights in the network
	private int numberOfParameters;
	private int blocks = 1;
	private int outputCount;
	public double LearningRate;
	public double Adjustment;
	public int NumberOfParameters;
	public double EffectiveParameters;
	public double Alpha;
	public double Beta;
	public boolean UseRegularization;
	public int Blocks;

//	private double[] derivativeStepSize;
//	private double derivativeStep = 1e-2;
//	private double[][] differentialCoefficients;

	private float[][][] weightDerivatives;
	private float[][] thresholdsDerivatives;

	public LevenbergMarquardtLearning(boolean useRegularization,
			JacobianMethod method) {
		// this.network = network;
		// this.numberOfParameters = getNumberOfParameters(network);
		// this.outputCount = network.Layers[network.Layers.Length -
		// 1].Neurons.Length;
		this.useBayesianRegularization = useRegularization;
		this.method = method;
		this.weights = new float[numberOfParameters];
		this.hessian = new float[numberOfParameters][];
		for (int i = 0; i < hessian.length; i++)
			hessian[i] = new float[numberOfParameters];
		this.diagonal = new float[numberOfParameters];
		this.gradient = new float[numberOfParameters];
		this.jacobian = new float[numberOfParameters][];
		// Will use backpropagation method for Jacobian computation
		if (method == JacobianMethod.ByBackpropagation) {
			// create weight derivatives arrays
			this.weightDerivatives = new float[8][8][8];
			this.thresholdsDerivatives = new float[8][8];
		} else
		// Will use finite difference method for Jacobian computation
		{
//			// create differential coefficient arrays
//			//this.differentialCoefficients = createCoefficients(3);
//			this.derivativeStepSize = new double[numberOfParameters];
//			// initialize arrays
//			for (int i = 0; i < numberOfParameters; i++)
//				this.derivativeStepSize[i] = derivativeStep;
		}
	}

}

	