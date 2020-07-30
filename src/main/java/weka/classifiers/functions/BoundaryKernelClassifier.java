/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.IDecisionBoundary;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.UtilsPT;
import weka.estimators.density.DensityEstimator;
import weka.estimators.density.bandwidthFinders.SilvermanBandwidthSelectionKernel;
import weka.tools.SerialCopier;

/**
 * @author pawel trajdos
 * @since 2.2.0
 * @version 2.2.0 
 *
 */
public class BoundaryKernelClassifier extends SingleClassifierEnhancerBoundary implements weka.tools.GlobalInfoHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8651181854725711338L;
	
	protected boolean normalize= true;
	
	protected DensityEstimator estimProto = new SilvermanBandwidthSelectionKernel() ;
	protected DensityEstimator[] estims;
	
	
	public BoundaryKernelClassifier(ClassifierWithBoundaries nearestCentroidBoundary) {
		this.setClassifier(nearestCentroidBoundary);
	}
	
	

	/**
	 * 
	 */
	public BoundaryKernelClassifier() {
		this(new NearestCentroidBoundary());
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		if(!this.m_DoNotCheckCapabilities)
			this.getCapabilities().testWithFail(data);
		this.boundClassRef.buildClassifier(data);
		int numClasses = data.numClasses();
		this.estims = new DensityEstimator[numClasses];
		for(int i=0;i<numClasses;i++)
			this.estims[i] = (DensityEstimator) SerialCopier.makeCopy(this.estimProto);
		
		int numInstances = data.numInstances();
		Instance tmpInstance = null;
		IDecisionBoundary bnd = this.boundClassRef.getBoundary();
		double predVal=0;
		for(int i=0;i<numInstances;i++) {
			tmpInstance = data.get(i);
			predVal = bnd.getValue(tmpInstance);
			this.estims[(int) tmpInstance.classValue()].addValue(predVal, 1.0);
		}
		

	}
	
	
	
	

	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\tKernel prototype to use "+
		          "(default: weka.estimators.density.bandwidthFinders.SilvermanBandwidthSelectionKernel).\n",
			      "KP", 1, "-KP"));
		
		newVector.addElement(new Option(
			      "\tDetermines if the outpus is normalised "+
		          "(default: true.\n",
			      "NORM", 1, "-NORM"));
		
	    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		
		this.setEstimProto((DensityEstimator) UtilsPT.parseObjectOptions(options, "KP", new SilvermanBandwidthSelectionKernel(), DensityEstimator.class));
		this.setNormalize(Utils.getFlag("NORM", options));
		
		super.setOptions(options);
		
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		
		options.add("-KP");
		options.add(UtilsPT.getClassAndOptions(this.getEstimProto()));
		
		if(this.isNormalize())
			options.add("-NORM");
		
		Collections.addAll(options, super.getOptions());
		
	    return options.toArray(new String[0]);
	    
	}

	/**
	 * @return the estimProto
	 */
	public DensityEstimator getEstimProto() {
		return this.estimProto;
	}

	/**
	 * @param estimProto the estimProto to set
	 */
	public void setEstimProto(DensityEstimator estimProto) {
		this.estimProto = estimProto;
	}
	
	public String estimProtoTipText() {
		return "Estimator prototype";
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		double[] distribution = new double[this.estims.length];
		IDecisionBoundary bnd = this.boundClassRef.getBoundary();
		double val = bnd.getValue(instance);
		for(int i=0;i<distribution.length;i++)
			distribution[i] = this.estims[i].getPDF(val);
		
		if(this.normalize)
			distribution = UtilsPT.softMax(distribution);
		
		return distribution;
	}

	@Override
	public String globalInfo() {
		return "Kernel-based potential classifier" ;
	}



	/**
	 * @return the normalize
	 */
	public boolean isNormalize() {
		return this.normalize;
	}



	/**
	 * @param normalize the normalize to set
	 */
	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}
	
	public String normalizeTipText() {
		return "Determines if the classifier output is normalised";
	}
	

}
