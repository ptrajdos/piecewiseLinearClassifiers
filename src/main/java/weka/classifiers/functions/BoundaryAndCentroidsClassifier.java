/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.IDecisionBoundary;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionExp4;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionTanh;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.functions.nearestCentroid.IClusterPrototype;
import weka.classifiers.functions.nearestCentroid.prototypes.CustomizablePrototype;
import weka.classifiers.functions.nearestCentroid.prototypes.MahalanobisPrototype;
import weka.classifiers.rules.ZeroR;
import weka.clusterers.ClassSpecificClusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.UtilsPT;
import weka.tools.SerialCopier;
import weka.tools.data.InstancesOperator;

/**
 * @author pawel trajdos
 * @since 2.4.1
 * @version 2.4.1
 *
 */
public class BoundaryAndCentroidsClassifier extends SingleClassifierEnhancerBoundary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4804125977465095197L;
	
	protected IClusterPrototype prototypeProto; // Prototype to be clonned
	
	protected IClusterPrototype[][] classProtos; // classes x number of class-specific clusters
	
	
	protected PotentialFunction potFunction;
	
	/**
	 * Proportion between Centroid potential and plane potential
	 */
	protected double proportion=0.5;
	
	protected boolean classesOnly=false;
	
	protected double eps=Double.MIN_VALUE;
	
	protected boolean normalize=true;
	
	protected ZeroR defaultModel;
	
	double[] classFreqs; 
	
	boolean usePriors=false;

	protected ClassSpecificClusterer classSpecClusterer;
	
	
	/**
	 * 
	 */
	public BoundaryAndCentroidsClassifier(ClassifierWithBoundaries boundClassifier) {
		super();
		this.setClassifier(boundClassifier);
		this.prototypeProto = new CustomizablePrototype();
		this.potFunction = new PotentialFunctionTanh();
		this.classSpecClusterer = new ClassSpecificClusterer();
	}
	
	public BoundaryAndCentroidsClassifier() {
		this(new NearestCentroidBoundary());
	}
	

	/* (non-Javadoc)
	 * @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		if(!this.m_DoNotCheckCapabilities)
			this.getCapabilities().testWithFail(data);
		
		int numIinsts = data.numInstances();
		this.classFreqs = InstancesOperator.classFreq(data);
		for(int i =0;i<classFreqs.length;i++) {
			classFreqs[i] = Math.ceil(classFreqs[i]*numIinsts);
		}
		
		this.defaultModel = null;
		if(Utils.smOrEq(classFreqs[0], 1) | Utils.smOrEq(classFreqs[1], 1)) {
			this.defaultModel = new ZeroR();
			this.defaultModel.buildClassifier(data);
			return;
		}

		this.classSpecClusterer.buildClusterer(data);
		
		this.boundClassRef.buildClassifier(data);
		
		int numAttrs = data.numAttributes();
		int classIdx = data.classIndex();

		this.classesOnly = false;
		if(numAttrs==1 & classIdx>=0) {
			this.classesOnly = true;
			return;
		}
		
		int numClasses = data.numClasses(); // This should be 2
		int[] classSpecClusterNumber = this.classSpecClusterer.numberOfClassSpecificClusters();
		
		Instances[] classSplittedData = InstancesOperator.classSpecSplit(data);
		Instances[][] classClusterSplittedData = new Instances[numClasses][];
		
		this.classProtos = new IClusterPrototype[numClasses][];
		
		//Initialise classClusterSplittedData and cluster prototypes
		for(int c=0; c<numClasses; c++) {
			int nClusters = classSpecClusterNumber[c];
			
			classClusterSplittedData[c] = new Instances[nClusters];
			this.classProtos[c] = new IClusterPrototype[nClusters];
			
			for (int i =0; i<nClusters; i++) {
				classClusterSplittedData[c][i] = new Instances(classSplittedData[c],0);
				this.classProtos[c][c] = (IClusterPrototype) SerialCopier.makeCopy(this.prototypeProto);
			}
		}
		
		//Fill classClusterSplittedData
		for(int c = 0; c < numClasses; c++) {
			
			int nInstances = classSplittedData[c].numInstances();
			
			for(int i=0; i<nInstances; i++) {
				Instance tmpInstance = classSplittedData[c].get(i);
				double[][] classClusterResponse = this.classSpecClusterer.classSpecificDistributionForInstance(tmpInstance);
				int bestFitClusterIdx = Utils.maxIndex(classClusterResponse[c]);
				
				classClusterSplittedData[c][bestFitClusterIdx].add(tmpInstance);
			}	
		}
		
		//Train class-cluster-specific prototypes
		
		for(int c =0; c< numClasses; c++) {
			int nClusters = classSpecClusterNumber[c];
			
			for (int i =0; i<nClusters; i++) {
				this.classProtos[c][i].build(classClusterSplittedData[c][i]);
			}
			
		}
		
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#distributionForInstance(weka.core.Instance)
	 */
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		
		if(this.defaultModel != null)
			return this.defaultModel.distributionForInstance(instance);
		
		if (this.classesOnly)
			return this.boundClassRef.distributionForInstance(instance);
		
		return this.boundClassRef.distributionForInstance(instance);
	}
	
	public String globalInfo() {
		return "Class  that implements algorithm that combines centroid and linear based classifiers";
	}

	/**
	 * @return the prototypeProto
	 */
	public IClusterPrototype getPrototypeProto() {
		return this.prototypeProto;
	}

	/**
	 * @param prototypeProto the prototypeProto to set
	 */
	public void setPrototypeProto(IClusterPrototype prototypeProto) {
		this.prototypeProto = prototypeProto;
	}
	
	public String prototypeProtoTipText() {
		return "Prototype for Cluster prototype object";
	}

	/**
	 * @return the potFunction
	 */
	public PotentialFunction getPotFunction() {
		return this.potFunction;
	}

	/**
	 * @param potFunction the potFunction to set
	 */
	public void setPotFunction(PotentialFunction potFunction) {
		this.potFunction = potFunction;
	}
	
	public String potFunctionTipText() {
		return "Potential function to use"; 
	}

	/**
	 * @return the proportion
	 */
	public double getProportion() {
		return this.proportion;
	}

	/**
	 * @param proportion the proportion to set
	 */
	public void setProportion(double proportion) {
		if(proportion>1) {
			this.proportion =1.0;
			return;
		}
		if(proportion<0) {
			this.proportion=0;
			return;
		}
		this.proportion = proportion;
	}
	
	public String proportionTipText() {
		return "Proportion between Centroid and Plane potentials";
	}
	
	

	/**
	 * @return the eps
	 */
	public double getEps() {
		return this.eps;
	}

	/**
	 * @param eps the eps to set
	 */
	public void setEps(double eps) {
		this.eps = eps;
	}
	
	public String epsTipText() {
		return "Epsilon factor for the algorithm";
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
		return "Determines whether potential values should be normalized";
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\tThe cluster prototype to use "+
		          "(default: weka.classifiers.functions.nearestCentroid.prototypes.CustomizablePrototype).\n",
			      "P", 1, "-P"));

		newVector.addElement(new Option(
				"\tThe class-specific clusterer to use "+
				"(default: weka.clusterers.ClassSpecificClusterer).\n",
				"CSP", 1, "-CSP"));
		
		newVector.addElement(new Option(
			      "\tProportion between Centroid and Plane potentials"+
		          "(default: 0.5).\n",
			      "PR", 1, "-PR"));
		
		newVector.addElement(new Option(
			      "\tThe Potential function to use "+
		          "(default: weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionTanh).\n",
			      "PO", 1, "-PO"));
		
		newVector.addElement(new Option(
			      "\tEpsilon factor"+
		          "(default: Double.MIN_VALUE).\n",
			      "EPS", 1, "-EPS"));
		
		newVector.addElement(new Option(
			      "\tNormalization flag"+
		          "(default: TRUE).\n",
			      "N", 0, "-N"));
		
		newVector.addElement(new Option(
			      "\tDetermines whether the prior class probabilities are used"+
		          "(default: FALSE).\n",
			      "UP", 0, "-UP"));
		 
		
		
		newVector.addAll(Collections.list(super.listOptions()));
	    
		return newVector.elements();
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		
		this.setProportion(UtilsPT.parseDoubleOption(options, "PR", 0.5));
		
		
		this.setPrototypeProto((IClusterPrototype)
				UtilsPT.parseObjectOptions(options, "P", new MahalanobisPrototype(), IClusterPrototype.class));

		this.setClassSpecificClusterer((ClassSpecificClusterer)
							UtilsPT.parseObjectOptions(options, "CSP", new ClassSpecificClusterer(), ClassSpecificClusterer.class)
		);
		
		this.setPotFunction((PotentialFunction)
				 UtilsPT.parseObjectOptions(options, "PO", new PotentialFunctionExp4(), PotentialFunction.class));
		
		this.setEps(UtilsPT.parseDoubleOption(options, "EPS", Double.MIN_VALUE));
		
		this.setNormalize(Utils.getFlag("N", options));
		
		this.setUsePriors(Utils.getFlag("UP", options));
		
		
		super.setOptions(options);
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#getOptions()
	 */
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		options.add("-P");
		options.add(UtilsPT.getClassAndOptions(prototypeProto));

		options.add("-CSP");
		options.add(UtilsPT.getClassAndOptions(this.getClassSpecificClusterer()));
		
		options.add("-PR");
		options.add(""+this.proportion);
		
		options.add("-PO");
		options.add(UtilsPT.getClassAndOptions(potFunction));
		
		options.add("-EPS");
		options.add(""+this.getEps());
		
		if(this.isNormalize())
			options.add("-N");
		
		if(this.isUsePriors())
			options.add("-UP");
		
		
		Collections.addAll(options, super.getOptions());
	    
	    return options.toArray(new String[0]);
	}

	/**
	 * @return the usePriors
	 */
	public boolean isUsePriors() {
		return this.usePriors;
	}

	/**
	 * @param usePriors the usePriors to set
	 */
	public void setUsePriors(boolean usePriors) {
		this.usePriors = usePriors;
	}
	
	
	public String usePriorsTipText() {
		return "Determines whether prior class probabilities are used.";
	}

	public ClassSpecificClusterer getClassSpecificClusterer(){
		return this.classSpecClusterer;
	}

	public void setClassSpecificClusterer(ClassSpecificClusterer classSpecificClusterer){
		this.classSpecClusterer = classSpecificClusterer;
	}

	public String classSpecificClustererTipText(){
		return "Class-specific clusterer to use";
	}
	
	

}
