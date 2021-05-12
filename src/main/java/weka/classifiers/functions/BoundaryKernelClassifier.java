/**
 * 
 */
package weka.classifiers.functions;

import java.util.Arrays;
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
 * @version 2.3.0 
 *
 */
public class BoundaryKernelClassifier extends SingleClassifierEnhancerBoundary implements weka.tools.GlobalInfoHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8651181854725711338L;
	
	protected boolean normalize= true;
	
	protected boolean preNormalize=false;
	
	protected DensityEstimator estimProto = new SilvermanBandwidthSelectionKernel() ;
	protected DensityEstimator[] estims;
	
	protected int[] numInsancesPerClass;
	protected int numInstances=0;
	protected int numClasses=0;
	
	protected boolean usePriorProbs=false;
	
	
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
		this.numClasses = data.numClasses();
		this.estims = new DensityEstimator[this.numClasses];
		for(int i=0;i<this.numClasses;i++)
			this.estims[i] = (DensityEstimator) SerialCopier.makeCopy(this.estimProto);
		
		this.numInstances = data.numInstances();
		Instance tmpInstance = null;
		IDecisionBoundary bnd = this.boundClassRef.getBoundary();
		this.numInsancesPerClass = new int[this.numClasses];
		double predVal=0;
		int classIdx;
		for(int i=0;i<this.numInstances;i++) {
			tmpInstance = data.get(i);
			predVal = bnd.getValue(tmpInstance);
			classIdx = (int) tmpInstance.classValue();
			this.estims[classIdx].addValue(predVal, 1.0);
			this.numInsancesPerClass[classIdx]++;
		}

	}
	
	/**
	 * Prepares preliminary distribution
	 * @param instance
	 * @return
	 * @throws Exception 
	 */
	protected double[] preparePreDistribution(Instance instance) throws Exception {
		double[] distribution = new double[this.estims.length];
		IDecisionBoundary bnd = this.boundClassRef.getBoundary();
		double val = bnd.getValue(instance);
		for(int i=0;i<distribution.length;i++)
			if(this.numInsancesPerClass[i]>0) {
				distribution[i] = this.estims[i].getPDF(val);
				if(this.usePriorProbs)
					distribution[i]*= ((double)this.numInsancesPerClass[i])/((double)this.numInstances);
				else
					distribution[i]*=1.0/this.numClasses;
			}
			else
				distribution[i]=0;
		
		return distribution;
	}
	
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		double[] distribution = this.preparePreDistribution(instance);
		
		if(this.preNormalize)
			distribution = this.prenormalize(distribution);
		
		if(this.normalize)
			distribution = UtilsPT.softMax(distribution);
		
		return distribution;
	}
	
	private double[] prenormalize(double[] distribution) {
		double[] nDistr = Arrays.copyOf(distribution, distribution.length);
		double sum = Utils.sum(nDistr);
		if(Utils.gr(sum, 0))
			Utils.normalize(nDistr, sum);
		
		return nDistr;
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
			      "NORM", 0, "-NORM"));
		
		newVector.addElement(new Option(
			      "\tDetermines if the outpus is pre normalised (linear)"+
		          "(default: true.\n",
			      "NORM", 0, "-NORM"));
		
		newVector.addElement(new Option(
			      "\tDetermines if the prior probabilities are used when potential function is calculated "+
		          "(default: false.\n",
			      "PRIO", 0, "-PRIO"));
	    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		
		this.setEstimProto((DensityEstimator) UtilsPT.parseObjectOptions(options, "KP", new SilvermanBandwidthSelectionKernel(), DensityEstimator.class));
		this.setNormalize(Utils.getFlag("NORM", options));
		this.setUsePriorProbs(Utils.getFlag("PRIO", options));
		this.setPreNormalize(Utils.getFlag("PNORM", options));
		
		super.setOptions(options);
		
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		
		options.add("-KP");
		options.add(UtilsPT.getClassAndOptions(this.getEstimProto()));
		
		if(this.isNormalize())
			options.add("-NORM");
		
		if(this.isPreNormalize())
			options.add("-PNORM");
		
		if(this.isUsePriorProbs())
			options.add("-PRIO");
		
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



	/**
	 * @return the usePriorProbs
	 */
	public boolean isUsePriorProbs() {
		return this.usePriorProbs;
	}



	/**
	 * @param usePriorProbs the usePriorProbs to set
	 */
	public void setUsePriorProbs(boolean usePriorProbs) {
		this.usePriorProbs = usePriorProbs;
	}
	
	public String usePriorProbsTipText() {
		return "Determines whether class prior probs are used";
	}



	/**
	 * @return the preNormalize
	 */
	public boolean isPreNormalize() {
		return this.preNormalize;
	}



	/**
	 * @param preNormalize the preNormalize to set
	 */
	public void setPreNormalize(boolean preNormalize) {
		this.preNormalize = preNormalize;
	}
	
	public String preNormalizeTipText() {
		return "Determines whether pre normalization (linear) is applied";
	}

}
