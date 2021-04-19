/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UtilsPT;

/**
 * @author pawel trajdos
 * @since 2.3.0
 * @version 2.3.0
 * 
 *
 */
public class BoundaryKernelClassifierWithPlaneProjectionsNB extends BoundaryKernelClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1867667124720100644L;
	
	protected Classifier planeProjectionProto = new NaiveBayes();
	protected Classifier planeProjection;


	
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
		this.planeProjection = AbstractClassifier.makeCopy(this.planeProjectionProto);
		Instances projectedInstances = this.getProjectedInstances(data);
		this.planeProjection.buildClassifier(projectedInstances);
		
		
	}

	@Override
	protected double[] preparePreDistribution(Instance instance) throws Exception {
		double[] distribution =  super.preparePreDistribution(instance);
		
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		Instance tmpInstance = plane.projectOnPlane(instance);
		
		
		
		double[] nbDistribution = this.planeProjection.distributionForInstance(tmpInstance);
		for(int c=0;c<nbDistribution.length;c++)
			if(this.numInsancesPerClass[c]>0) {
				distribution[c]*=nbDistribution[c]*this.numInstances/this.numInsancesPerClass[c];
			}
			
		
		
		return distribution;
	}

	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\tPlane projections estimator proto "+
		          "(default: "+ NaiveBayes.class.getCanonicalName() +  ").\n",
			      "PPEP", 1, "-PPEP"));		
		newVector.addAll(Collections.list(super.listOptions()));
	    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		this.setPlaneProjectionProto((Classifier) UtilsPT.parseObjectOptions(options, "PPEP", new NaiveBayes(), Classifier.class));
		super.setOptions(options);
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		options.add("-PPEP");
		options.add(UtilsPT.getClassAndOptions(this.getPlaneProjectionProto() ));
		
		
	
		Collections.addAll(options, super.getOptions());
		return options.toArray(new String[0]);
	}

	/**
	 * @return the nbProto
	 */
	public Classifier getPlaneProjectionProto() {
		return this.planeProjectionProto;
	}

	/**
	 * Set the prototype of the plane projection estimator prototype
	 * @param planeProjectionProto
	 */
	public void setPlaneProjectionProto(Classifier nbProto) {
		this.planeProjectionProto = nbProto;
	}
	
	public String planeProjectionProtoTipText() {
		return "Class posterior distribution estimator";
	}
	
	

}
