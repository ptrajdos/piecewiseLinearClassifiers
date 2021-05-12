/**
 * 
 */
package weka.classifiers.functions;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UtilsPT;
import weka.estimators.MultivariateEstimatorFromInstancesWrapper;
import weka.estimators.MultivariateGaussianEstimator;

/**
 * @author pawel trajdos
 * @since 2.3.0
 * @version 2.3.0
 *
 */
public class BoundaryKernelClassifierWithPlaneProjectionsGauss extends BoundaryKernelClassifier {
	
	class MyMvarEstim extends MultivariateEstimatorFromInstancesWrapper{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -5722118179266382401L;

		public MultivariateGaussianEstimator getGaussEstim() {
			return (MultivariateGaussianEstimator) this.mEstimator;
		}
		
	}
	
	
	protected MyMvarEstim mestimator;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1755806147645660368L;

	/**
	 * @param nearestCentroidBoundary
	 */
	public BoundaryKernelClassifierWithPlaneProjectionsGauss(ClassifierWithBoundaries nearestCentroidBoundary) {
		super(nearestCentroidBoundary);
		this.mestimator = new MyMvarEstim();
		this.mestimator.setmEstimator(new MultivariateGaussianEstimator());
	}
	
	

	/**
	 * 
	 */
	public BoundaryKernelClassifierWithPlaneProjectionsGauss() {
		super();
		this.mestimator = new MyMvarEstim();
		this.mestimator.setmEstimator(new MultivariateGaussianEstimator());
	}



	@Override
	public void buildClassifier(Instances data) throws Exception {
		super.buildClassifier(data);
		Instances projectedData = this.getProjectedInstances(data);
		this.mestimator.estimate(projectedData);
		
	}
	
	protected Instances getProjectedInstances(Instances data) throws Exception {
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		Instances projectedInstances = plane.planeBasedInstances(data);
		
		return projectedInstances;
	}



	@Override
	protected double[] preparePreDistribution(Instance instance) throws Exception {
		double[] distribution =super.preparePreDistribution(instance);
		
		if(this.numInstances==0)
			return distribution;
		
		double[] meanVec = this.mestimator.getGaussEstim().getMean();
		double logGlobalPdfMax = this.mestimator.getGaussEstim().logDensity(meanVec);
		
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		
		Instance tmpInstance = plane.planeBasedInstance(instance);
		
		double logInstanceGlobalPdf = this.mestimator.logDensity(tmpInstance);
		
		double powCoeff = this.generatePowCoefficient(logGlobalPdfMax, logInstanceGlobalPdf);
		
		for(int i=0;i<distribution.length;i++)
			distribution[i] = Math.pow(distribution[i], powCoeff);
		
		return distribution;
	}
	
	protected double generatePowCoefficient(double maxVal, double currVal) {
		double[] dist = UtilsPT.softMax(new double[] {maxVal,currVal});
		double val = dist[1]*2;
		return val;
	}

}
