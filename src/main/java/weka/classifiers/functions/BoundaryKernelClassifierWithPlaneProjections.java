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
import weka.estimators.MultivariateEstimatorFromInstances;
import weka.tools.SerialCopier;
import weka.tools.data.InstancesOperator;

/**
 * @author pawel trajdoss
 * @since 2.3.0
 * @version 2.3.0
 *
 */
public class BoundaryKernelClassifierWithPlaneProjections extends BoundaryKernelClassifierWithPlaneProjectionsAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3083361719248513679L;
	
	
	protected MultivariateEstimatorFromInstances[] planeProjectionsEstimators;
	
	

	/**
	 * @param nearestCentroidBoundary
	 */
	public BoundaryKernelClassifierWithPlaneProjections(ClassifierWithBoundaries nearestCentroidBoundary) {
		super(nearestCentroidBoundary);
	}

	/**
	 * 
	 */
	public BoundaryKernelClassifierWithPlaneProjections() {
		super();
	}
	
	

	@Override
	protected double[] preparePreDistribution(Instance instance) throws Exception {
		double[] distribution = super.preparePreDistribution(instance);
		
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		
		Instance tmpInstance = plane.projectOnPlane(instance);
		double projectionLogPDF = 0;
		//Changing into logs allow avoiding numeric problems (Getting INF)
		for(int c=0;c<this.numClasses;c++) {
			if(this.numInsancesPerClass[c]!=0)
				projectionLogPDF = this.planeProjectionsEstimators[c].logDensity(tmpInstance);
			else
				projectionLogPDF=0;
			distribution[c]= Math.log(distribution[c]+Double.MIN_VALUE) +  projectionLogPDF;
		}
		distribution = UtilsPT.softMax(distribution);
		
		return distribution;
	}
	
	@Override
	public void buildClassifier(Instances data) throws Exception {
		super.buildClassifier(data);
		Instances projectedInstances = this.getProjectedInstances(data);
		Instances[] splittedProjected = InstancesOperator.classSpecSplit(projectedInstances);
		this.planeProjectionsEstimators = new MultivariateEstimatorFromInstances[this.numClasses];
		for(int c=0;c<this.numClasses;c++) {
			this.planeProjectionsEstimators[c] = (MultivariateEstimatorFromInstances) SerialCopier.makeCopy(this.planeProjectionEstimatorProto);
			this.planeProjectionsEstimators[c].estimate(splittedProjected[c]);
		}
		
	}


}
