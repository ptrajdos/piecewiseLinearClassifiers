/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UtilsPT;
import weka.estimators.MultivariateEstimatorFromInstances;
import weka.estimators.MultivariateEstimatorFromInstancesWrapper;
import weka.tools.SerialCopier;
import weka.tools.data.InstancesOperator;

/**
 * @author pawel trajdoss
 * @since 2.3.0
 * @version 2.3.0
 *
 */
public class BoundaryKernelClassifierWithPlaneProjections extends BoundaryKernelClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3083361719248513679L;
	
	protected MultivariateEstimatorFromInstances planeProjectionEstimatorProto = new MultivariateEstimatorFromInstancesWrapper();
	
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
		double projectionPDF = 0;
		
		for(int c=0;c<this.numClasses;c++) {
			projectionPDF = this.planeProjectionsEstimators[c].density(tmpInstance);
			distribution[c]*= projectionPDF;
		}
		
		return distribution;
	}
	
	protected Instances getProjectedInstances(Instances data) throws Exception {
		Instances projectedInstances = new Instances(data, 0);
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		
		for(int i=0;i<this.numInstances;i++) {
			projectedInstances.add(plane.projectOnPlane(data.get(i)));
		}
		return projectedInstances;
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

	/**
	 * @return the planeProjectionEstimatorProto
	 */
	public MultivariateEstimatorFromInstances getPlaneProjectionEstimatorProto() {
		return this.planeProjectionEstimatorProto;
	}

	/**
	 * @param planeProjectionEstimatorProto the planeProjectionEstimatorProto to set
	 */
	public void setPlaneProjectionEstimatorProto(MultivariateEstimatorFromInstances planeProjectionEstimatorProto) {
		this.planeProjectionEstimatorProto = planeProjectionEstimatorProto;
	}
	
	public String planeProjectionEstimatorProtoTipText() {
		return "Plane projection estimator to use";
	}

	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\tPlane projections estimator proto "+
		          "(default: "+ MultivariateEstimatorFromInstancesWrapper.class.getCanonicalName() +  ").\n",
			      "PPEP", 1, "-PPEP"));		
		newVector.addAll(Collections.list(super.listOptions()));
	    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		
		this.setPlaneProjectionEstimatorProto((MultivariateEstimatorFromInstances) UtilsPT.parseObjectOptions(options, "PPEP", new MultivariateEstimatorFromInstancesWrapper(), MultivariateEstimatorFromInstances.class));
		super.setOptions(options);
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		options.add("-PPEP");
		options.add(UtilsPT.getClassAndOptions(this.getPlaneProjectionEstimatorProto()));
		
		
	
		Collections.addAll(options, super.getOptions());
		return options.toArray(new String[0]);
	}
	
	
	
	

}
