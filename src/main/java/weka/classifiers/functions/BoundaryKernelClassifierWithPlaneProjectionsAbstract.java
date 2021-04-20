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
import weka.core.Instances;
import weka.core.Option;
import weka.core.UtilsPT;
import weka.estimators.MultivariateEstimatorFromInstances;
import weka.estimators.MultivariateEstimatorFromInstancesWrapper;

/**
 * @author pawel trajdoss
 * @since 2.3.0
 * @version 2.3.0
 *
 */
public abstract class BoundaryKernelClassifierWithPlaneProjectionsAbstract extends BoundaryKernelClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 60215392183312537L;
	/**
	 * 
	 */
	
	
	protected MultivariateEstimatorFromInstances planeProjectionEstimatorProto = new MultivariateEstimatorFromInstancesWrapper();
	
	
	

	/**
	 * @param nearestCentroidBoundary
	 */
	public BoundaryKernelClassifierWithPlaneProjectionsAbstract(ClassifierWithBoundaries nearestCentroidBoundary) {
		super(nearestCentroidBoundary);
	}

	/**
	 * 
	 */
	public BoundaryKernelClassifierWithPlaneProjectionsAbstract() {
		super();
	}
	
	

	
	protected Instances getProjectedInstances(Instances data) throws Exception {
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		Instances projectedInstances = plane.projectOnPlane(data);
		
		return projectedInstances;
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
