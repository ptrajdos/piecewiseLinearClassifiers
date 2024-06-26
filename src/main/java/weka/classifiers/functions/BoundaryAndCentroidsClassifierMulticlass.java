/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;

import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.bayes.NaiveBayes;
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
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.UtilsPT;
import weka.core.Capabilities.Capability;
import weka.tools.SerialCopier;
import weka.tools.arrayFunctions.MeanFunction;
import weka.tools.arrayFunctions.MultivariateFunction;
import weka.tools.data.InstancesOperator;
import weka.tools.data.splitters.DataSplitter;
import weka.tools.data.splitters.CopySplitter;

/**
 * @author pawel trajdos
 * @since 2.6.0
 * @version 2.6.1
 *
 */
public class BoundaryAndCentroidsClassifierMulticlass extends SingleClassifierEnhancer {

	private static final long serialVersionUID = 8010565844371661224L;

	/**
	 * 
	 */

	protected IClusterPrototype prototypeProto; // Prototype to be clonned
	
	protected IClusterPrototype[][] classProtos; // classes x number of class-specific clusters
	
	protected double[][] clusterPotentialArgumentMultipliers; // classes x number of class-specific clusters
	
	protected PotentialFunction potFunction;
	
	/**
	 * Proportion between plane potential and cluster potential
	 */
	protected double proportion=0.5;
	
	protected boolean classesOnly=false;
	
	protected double eps=Double.MIN_VALUE;
	
	
	protected ZeroR defaultModel;
	
	protected double[] classFreqs; 
	
	protected boolean usePriors=false;

	protected ClassSpecificClusterer classSpecClusterer;
	
	protected DataSplitter dataSplitter;
	
	protected double quantile=0.9;
	protected double quantilePotentialVal = 0.1;
	protected double minSearch = 1E-6;
	protected double maxSearch = 5.0;
	protected int nBisectIterations = 1000;
	
	protected MultivariateFunction clusterCombiner;
	
	protected boolean useSoftMax = true;
	
	
	/**
	 * 
	 */
	public BoundaryAndCentroidsClassifierMulticlass(Classifier classifier) {
		super();
		this.setClassifier(classifier);
		this.prototypeProto = new CustomizablePrototype();
		this.potFunction = new PotentialFunctionTanh();
		this.classSpecClusterer = new ClassSpecificClusterer();
		this.dataSplitter = new CopySplitter();
		this.clusterCombiner = new MeanFunction();
		
	}
	
	public BoundaryAndCentroidsClassifierMulticlass() {
		this(new NaiveBayes());
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
		
		
		this.defaultModel = null;
		
		this.dataSplitter.train(data);
		Instances[] splitted = this.dataSplitter.split(data);
		
		//Checking if the default model should be used. 
		for(int i=0;i<splitted.length;i++) {
			Instances tmpInstances = splitted[i];
			int[] classCounts2 = InstancesOperator.uniqObjPerClass(tmpInstances);
			for (int cnt : classCounts2) {
				if( cnt <=1 ) {
					this.defaultModel = new ZeroR();
					this.defaultModel.buildClassifier(data);
					return;
				}
					
			}
		}
		
		this.buildBaseClassifier(splitted[0]);
		this.buildClusters(splitted[1]);
		
		
	}
	
	/**
	 * Builds class-specific clusters and creates cluster prototypes
	 * @param data
	 * @throws Exception
	 */
	protected void buildClusters(Instances data) throws Exception {
		this.classSpecClusterer.buildClusterer(data);
		
		
		int numAttrs = data.numAttributes();
		int classIdx = data.classIndex();

		this.classesOnly = false;
		if(numAttrs==1 & classIdx>=0) {
			this.classesOnly = true;
			return;
		}
		
		int numClasses = data.numClasses(); // This should be 2
		int[] classSpecClusterNumber = this.classSpecClusterer.numberOfClassSpecificClusters();//separate number for each class
		
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
				this.classProtos[c][i] = (IClusterPrototype) SerialCopier.makeCopy(this.prototypeProto);
			}
		}
		
		//Fill classClusterSplittedData
		for(int c = 0; c < numClasses; c++) {
			
			int nInstances = classSplittedData[c].numInstances();
			
			for(int i=0; i<nInstances; i++) {
				Instance tmpInstance = classSplittedData[c].get(i);
				double[][] classClusterResponse = this.classSpecClusterer.classSpecificDistributionForInstance(tmpInstance);//class x cluster
				int bestFitClusterIdx = Utils.maxIndex(classClusterResponse[c]);//TODO if all responses equal, then always select first!
				
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
		
		//Multipliers
		this.clusterPotentialArgumentMultipliers = new double[numClasses][];
		for(int c =0; c< numClasses; c++) {
			int nClusters = classSpecClusterNumber[c];
			this.clusterPotentialArgumentMultipliers[c] = new double[nClusters];
			
			for(int i =0; i<nClusters;i++) {
				
				
				double[] distances = new double[classClusterSplittedData[c][i].numInstances()];//Zero length!
				
				for(int d=0; d<distances.length;d++) {
					distances[d] = this.classProtos[c][i].distance(classClusterSplittedData[c][i].get(d));
				}
				
				double qVal = UtilsPT.quantile(distances, this.quantile);
				
				BisectionSolver solver = new BisectionSolver();
				double multiplier =  solver.solve(this.nBisectIterations, new UnivariateFunction() {
					
					@Override
					public double value(double x) {
						
						try {
							return 1.0 - potFunction.getPotentialValue(qVal * x) - quantilePotentialVal;
						} catch (Exception e) {
							return 0;
						}
					}
				}, this.minSearch, this.maxSearch);
				this.clusterPotentialArgumentMultipliers[c][i] = multiplier;
			}
		}
		
		
	}
	
	protected void buildBaseClassifier(Instances instances) throws Exception {
		
		this.m_Classifier.buildClassifier(instances);
		
	}
	
	
	protected double[] getresponse(Instance instance) throws Exception {
		
		double[] clusterDistribution = new double[this.classFreqs.length];
		
		
		for(int c =0 ;c<this.classFreqs.length; c++) {
			int nProtos = this.classProtos[c].length;
			double[] tmpClusterDistribution = new double[nProtos];
			
			for(int p =0; p< nProtos; p++) {
				tmpClusterDistribution[p] += 1 - this.potFunction.getPotentialValue(this.clusterPotentialArgumentMultipliers[c][p] * this.classProtos[c][p].distance(instance));
			}
			clusterDistribution[c] = this.clusterCombiner.value(tmpClusterDistribution);
					
		}
		
		if(this.useSoftMax) {
			clusterDistribution =  UtilsPT.softMax(clusterDistribution);
		}else {
			double sum = Utils.sum(clusterDistribution);
			if(sum> 5.0 * this.eps) {
				Utils.normalize(clusterDistribution, sum);
			}else {
				clusterDistribution =  UtilsPT.softMax(clusterDistribution);
			}
		}
		
		double[] distribution = this.m_Classifier.distributionForInstance(instance);
		
		for(int i = 0; i<this.classFreqs.length; i++) {
			distribution[i] = this.proportion * distribution[i] + (1.0 - this.proportion) * clusterDistribution[i];
		}
		
		
	
		return distribution;
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#distributionForInstance(weka.core.Instance)
	 */
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		
		if(this.defaultModel != null)
			return this.defaultModel.distributionForInstance(instance);
		
		if (this.classesOnly)
			return this.m_Classifier.distributionForInstance(instance);
		
		
		return this.getresponse(instance);
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
		return "Proportion between Plane and cluster potentials";
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
			      "\tDetermines whether the prior class probabilities are used"+
		          "(default: FALSE).\n",
			      "UP", 0, "-UP"));
		
		newVector.addElement(new Option(
			      "\tThe DataSplitter to use "+
		          "(default:" +  CopySplitter.class.getCanonicalName()+ ".\n",
			      "DS", 1, "-DS"));
		
		newVector.addElement(new Option(
			      "\tQuantile of points to use"+
		          "(default: 0.9).\n",
			      "QA", 1, "-QA"));
		
		newVector.addElement(new Option(
			      "\tPotential Value for given quantile"+
		          "(default: 0.1).\n",
			      "PQA", 1, "-PQA"));
		
		
		newVector.addElement(new Option(
			      "\tMin Multiplier Value"+
		          "(default: 1E-3).\n",
			      "MiPV", 1, "-MiPV"));
		
		newVector.addElement(new Option(
			      "\tMax Multiplier Value"+
		          "(default: 5.0).\n",
			      "MaPV", 1, "-MaPV"));
		
		newVector.addElement(new Option(
			      "\tBisection Iterations"+
		          "(default: 1000).\n",
			      "BI", 1, "-BI"));
		
		newVector.addElement(new Option(
			      "\tThe Cluster Combiner to use "+
		          "(default:" +  MeanFunction.class.getCanonicalName()+ ".\n",
			      "CC", 1, "-CC"));
		
		
		
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
		
		this.setDataSplitter((DataSplitter)
						UtilsPT.parseObjectOptions(options, "DS", new CopySplitter(), DataSplitter.class) );
		
		this.setPotFunction((PotentialFunction)
				 UtilsPT.parseObjectOptions(options, "PO", new PotentialFunctionExp4(), PotentialFunction.class));
		
		this.setEps(UtilsPT.parseDoubleOption(options, "EPS", Double.MIN_VALUE));
		
		
		this.setUsePriors(Utils.getFlag("UP", options));
		
		this.setQuantile(UtilsPT.parseDoubleOption(options, "QA", 0.9));
		
		this.setQuantilePotentialVal(UtilsPT.parseDoubleOption(options, "PQA", 0.1));
		
		this.setMinSearch(UtilsPT.parseDoubleOption(options, "MiPV", 1E-3));
		
		this.setMaxSearch(UtilsPT.parseDoubleOption(options, "MaPV", 5.0));
		
		this.setnBisectIterations(UtilsPT.parseIntegerOption(options, "BI", 1000));
		
		this.setClusterCombiner((MultivariateFunction) UtilsPT.parseObjectOptions(options, "CC", new MeanFunction(), MultivariateFunction.class ));
		
		this.setUseSoftMax(Utils.getFlag("USM", options));
		
		
		super.setOptions(options);
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#getOptions()
	 */
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		options.add("-P");
		options.add(UtilsPT.getClassAndOptions(this.getPrototypeProto()));

		options.add("-CSP");
		options.add(UtilsPT.getClassAndOptions(this.getClassSpecificClusterer()));
		
		options.add("-PR");
		options.add(""+this.getProportion());
		
		options.add("-DS");
		options.add(UtilsPT.getClassAndOptions(this.getDataSplitter()));
		
		options.add("-PO");
		options.add(UtilsPT.getClassAndOptions(this.getPotFunction()));
		
		options.add("-EPS");
		options.add(""+this.getEps());
		
		
		if(this.isUsePriors())
			options.add("-UP");
		
		options.add("-QA");
		options.add(""+this.getQuantile());
		
		options.add("-PQA");
		options.add(""+this.getQuantilePotentialVal());
		
		options.add("-MiPV");
		options.add(""+this.getMinSearch());
		
		options.add("-MaPV");
		options.add(""+this.getMaxSearch());
		
		options.add("-BI");
		options.add(""+this.getnBisectIterations());
		
		options.add("-CC");
		options.add(UtilsPT.getClassAndOptions(this.getClusterCombiner()));
		
		if(this.isUseSoftMax())
			options.add("-USM");
		
		
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

	public DataSplitter getDataSplitter() {
		return dataSplitter;
	}

	public void setDataSplitter(DataSplitter dataSplitter) {
		this.dataSplitter = dataSplitter;
	}
	
	public String dataSplitterTipText() {
		return "Data splitter to use";
	}
	
	public String quantileTipText() {
		return "Distance quantile to use";
	}

	public double getQuantile() {
		return quantile;
	}

	public void setQuantile(double quantile) {
		this.quantile = quantile;
	}

	public String quantilePotentialValTipText() {
		return "Potential function value for given quantile";
	}
	
	public double getQuantilePotentialVal() {
		return quantilePotentialVal;
	}

	public void setQuantilePotentialVal(double quantilePotentialVal) {
		this.quantilePotentialVal = quantilePotentialVal;
	}

	public String minSearchTipText() {
		return "Min Multiplier value to use";
	}
	
	public double getMinSearch() {
		return minSearch;
	}

	public void setMinSearch(double minSearch) {
		this.minSearch = minSearch;
	}
	
	public String maxSearchTipText() {
		return "Max Multiplier value to use";
	}

	public double getMaxSearch() {
		return maxSearch;
	}

	public void setMaxSearch(double maxSearch) {
		this.maxSearch = maxSearch;
	}
	
	public String nBisectIterationsTipText() {
		return "Max number of bisection operations to perform";
	}

	public int getnBisectIterations() {
		return nBisectIterations;
	}

	public void setnBisectIterations(int nBisectIterations) {
		this.nBisectIterations = nBisectIterations;
	}

	public MultivariateFunction getClusterCombiner() {
		return clusterCombiner;
	}

	public void setClusterCombiner(MultivariateFunction clusterCombiner) {
		this.clusterCombiner = clusterCombiner;
	}
	
	
	public String clusterCombinerTipText() {
		return "Method of combining cluster responses";
	}
	
	
	public String useSoftMaxTipText() {
		return "Determines whether softmax cluster potential normalization is applied.";
	}
	
	public boolean isUseSoftMax() {
		return useSoftMax;
	}

	public void setUseSoftMax(boolean useSoftMax) {
		this.useSoftMax = useSoftMax;
	}

	public Capabilities getCapabilities() {
		Capabilities caps =super.getCapabilities();
		caps.disableAll();
		caps.enable(Capability.NUMERIC_ATTRIBUTES);
		caps.enable(Capability.BINARY_CLASS);
		caps.enable(Capability.NOMINAL_CLASS);
		caps.enable(Capability.EMPTY_NOMINAL_CLASS);

		return caps;
	}
	

}

