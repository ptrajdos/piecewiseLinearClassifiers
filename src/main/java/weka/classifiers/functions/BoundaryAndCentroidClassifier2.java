/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.mahout.math.Arrays;

import com.google.gson.Gson;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.IDecisionBoundary;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionExp4;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.functions.nearestCentroid.IClusterPrototype;
import weka.classifiers.functions.nearestCentroid.prototypes.MahalanobisPrototype;
import weka.classifiers.rules.ZeroR;
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
public class BoundaryAndCentroidClassifier2 extends SingleClassifierEnhancerBoundary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4804125977465095197L;
	
	protected IClusterPrototype prototypeProto;
	
	protected IClusterPrototype[] classProtos;
	
	protected double[] protoPlaneSide;
	
	protected double[] stdDevs;
	
	protected double[] proto2Bnd;
	
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
	
	protected double quant  = 0.9;
	protected double quantPotentialValue = 0.1;
	protected double minSearch = 1E-4;
	protected double maxSearch = 7.0;
	protected int numIterations = 1000;
	
	protected double[] classCentroidsMultipliers;
	
	protected double[] boundaryMultipliers;
	
	
	
	

	/**
	 * 
	 */
	public BoundaryAndCentroidClassifier2(ClassifierWithBoundaries boundClassifier) {
		super();
		this.setClassifier(boundClassifier);
		this.prototypeProto = new MahalanobisPrototype();
		this.potFunction = new PotentialFunctionExp4();
	}
	
	public BoundaryAndCentroidClassifier2() {
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
		
		int[] classCounts = InstancesOperator.objPerClass(data);
		
		this.defaultModel = null;
		if(classCounts[0] <=1 | classCounts[1] <=1) {
			this.defaultModel = new ZeroR();
			this.defaultModel.buildClassifier(data);
			return;
		}
				
		
		
		this.boundClassRef.buildClassifier(data);
		
		int numAttrs = data.numAttributes();
		int classIdx = data.classIndex();
		
		if(numAttrs==1 & classIdx>=0) {
			this.classesOnly=true;
			return;
		}
		
		
		
		int numClasses = data.numClasses();
		Instances[] splittedData = InstancesOperator.classSpecSplit(data);
		classProtos = new IClusterPrototype[numClasses];
		protoPlaneSide = new double[numClasses];
		stdDevs = new double[numClasses];
		IDecisionBoundary bnd = this.boundClassRef.getBoundary();
		proto2Bnd = new double[numClasses];
		double[] inst2CentDist;
		Instance tmpInst;
		double tmpDist;
		
		this.boundaryMultipliers = new double[numClasses];
		this.classCentroidsMultipliers = new double[numClasses];
		
		for(int c =0 ;c<numClasses;c++) {
			this.classProtos[c] = (IClusterPrototype) SerialCopier.makeCopy(this.prototypeProto);
			this.classProtos[c].build(splittedData[c]);
			this.protoPlaneSide[c] = Math.signum(bnd.getValue(this.classProtos[c].getCenterPoint()));
			//Distance of class centroid to the decision plane. Along the normal vector of the decision plane
			proto2Bnd[c] = Math.signum(bnd.getValue(this.classProtos[c].getCenterPoint()))*bnd.getDistance(this.classProtos[c].getCenterPoint());
			
			inst2CentDist = new double[splittedData[c].numInstances()];
			double[] centroidDists = new double[splittedData[c].numInstances()];
			
			for(int i=0;i< inst2CentDist.length;i++) {
				tmpInst = splittedData[c].get(i);
				tmpDist = Math.signum(bnd.getValue(tmpInst))*bnd.getDistance(tmpInst);
				//Distance from class centroid to instance. Along normal vector of the plane
				inst2CentDist[i] = proto2Bnd[c] - tmpDist; //Can this be negative?
				
				centroidDists[i] = this.classProtos[c].distance(tmpInst);
				
			}
			this.stdDevs[c] = UtilsPT.stdDev(inst2CentDist);
			
			double[] inst2CentDistAbs = new double[inst2CentDist.length];
			for(int j=0;j<inst2CentDist.length;j++) {
				inst2CentDistAbs[j] = Math.abs(inst2CentDist[j]);
			}
			
			double centroidDistQuantileNorm = UtilsPT.quantile(inst2CentDistAbs, this.quant);
			double centroidDistQuantile = UtilsPT.quantile(centroidDists, this.quant);
			
			BisectionSolver distNormSolver = new BisectionSolver();
			
			this.boundaryMultipliers[c] = distNormSolver.solve(this.numIterations, new UnivariateFunction() {
				
				@Override
				public double value(double x) {
					try {
						return potFunction.getPotentialValue(x * centroidDistQuantileNorm) - quantPotentialValue;
					} catch (Exception e) {
						return 0;
					}
				}
			},
					this.minSearch, this.maxSearch);
			
			BisectionSolver distSolver = new BisectionSolver();
			
			this.classCentroidsMultipliers[c] = distSolver.solve(this.numIterations, new UnivariateFunction() {
				
				@Override
				public double value(double x) {
					
					try {
						return potFunction.getPotentialValue(x * centroidDistQuantile) - quantPotentialValue;
					} catch (Exception e) {
						return 0;
					}
				}
			}, this.minSearch, this.maxSearch);
			
			
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
		
		int numClasses = this.stdDevs.length;
		double[] potentials  = new double[numClasses];
		IDecisionBoundary bnd = this.boundClassRef.getBoundary();
		
		double centDist=0;
		double planeDirDist =0;
		double sum=0;
		double pot1=0;
		double pot2=0;
		
		for(int c=0;c<numClasses;c++) {
			centDist = this.classProtos[c].distance(instance);
			planeDirDist = proto2Bnd[c] - Math.signum(bnd.getValue(instance))*bnd.getDistance(instance);
			if(!Utils.eq(stdDevs[c], 0)) {
				planeDirDist/= stdDevs[c];
			}else {
				planeDirDist = Double.POSITIVE_INFINITY;
			}
			
			pot1 = this.potFunction.getPotentialValue(centDist * this.classCentroidsMultipliers[c]);
			pot2 = this.potFunction.getPotentialValue(planeDirDist * this.boundaryMultipliers[c]);
			
			if(this.getDebug() & Double.isNaN(pot1) ) {
				Gson gson = new Gson();
				String repr = gson.toJson(this);
				System.err.println("Potential 1 is NAN: \n"+ repr);
			}
			
			if(this.getDebug() & Double.isNaN(pot2) ) {
				Gson gson = new Gson();
				String repr = gson.toJson(this);
				System.err.println("Potential 2 is NAN: \n"+ repr);
			}
			
			potentials[c] = this.proportion*pot1 + (1-this.proportion)*pot2 + this.eps;
			if(this.usePriors)
				potentials[c]*=this.classFreqs[c];
			sum+=potentials[c];
			
		}
		if(this.normalize) {
			//TODO sum is NaN for some cases!
			//PotentialExp4 cannot return NaN for nonNan arguments!
			try {
			Utils.normalize(potentials, sum);
			}catch( Exception e) {
				Gson gson = new Gson();
				String repr = gson.toJson(this);
				System.err.println("SUM is NAN: \n"+ repr);
				throw e;
			}
		}
		
		return potentials;
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
		          "(default: weka.classifiers.functions.nearestCentroid.prototypes.MahalanobisPrototype).\n",
			      "P", 1, "-P"));
		
		newVector.addElement(new Option(
			      "\tProportion between Centroid and Plane potentials"+
		          "(default: 0.5).\n",
			      "PR", 1, "-PR"));
		
		newVector.addElement(new Option(
			      "\tThe Potential function to use "+
		          "(default: weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionExp4).\n",
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
		
		newVector.addElement(new Option(
			      "\tQuantile to use"+
		          "(default: 0.9).\n",
			      "QA", 1, "-QA"));
		
		newVector.addElement(new Option(
			      "\tQuantile potential to use"+
		          "(default: 0.1).\n",
			      "QAP", 1, "-QAP"));
		
		newVector.addElement(new Option(
			      "\tMin multiplier value to use"+
		          "(default: 1E-3).\n",
			      "MiM", 1, "-MiM"));
		
		newVector.addElement(new Option(
			      "\tMax multiplier value to use"+
		          "(default: 5.0).\n",
			      "MaM", 1, "-MaM"));
		
		newVector.addElement(new Option(
			      "\tNumber of bisection iterations to use"+
		          "(default: 1000).\n",
			      "BI", 1, "-BI"));
		
		
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
		
		this.setPotFunction((PotentialFunction)
				 UtilsPT.parseObjectOptions(options, "PO", new PotentialFunctionExp4(), PotentialFunction.class));
		
		this.setEps(UtilsPT.parseDoubleOption(options, "EPS", Double.MIN_VALUE));
		
		this.setNormalize(Utils.getFlag("N", options));
		
		this.setUsePriors(Utils.getFlag("UP", options));
		
		this.setQuant(UtilsPT.parseDoubleOption(options, "QA", 0.9));
		
		this.setQuantPotentialValue(UtilsPT.parseDoubleOption(options, "QAP", 0.1));
		
		this.setMinSearch(UtilsPT.parseDoubleOption(options, "MiM", 1E-3));
		
		this.setMaxSearch(UtilsPT.parseDoubleOption(options, "MaM", 5.0));
		
		this.setNumIterations(UtilsPT.parseIntegerOption(options, "BI", 1000));
		
		
		
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
		
		options.add("-QA");
		options.add(""+this.getQuant());
		
		options.add("-QAP");
		options.add(""+this.getQuantPotentialValue());
		
		
		options.add("-MiM");
		options.add(""+this.getMinSearch());
		
		options.add("-MaM");
		options.add(""+this.getMaxSearch());
		
		options.add("-BI");
		options.add(""+this.getNumIterations());
		
		
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
	
	public String quantTipText() {
		return "Quantile to use";
	}

	public double getQuant() {
		return quant;
	}

	public void setQuant(double quant) {
		this.quant = quant;
	}
	
	public String quantPotentialValueTipText() {
		return "Potential value for the distance quantile";
	}

	public double getQuantPotentialValue() {
		return quantPotentialValue;
	}

	public void setQuantPotentialValue(double quantPotentialValue) {
		this.quantPotentialValue = quantPotentialValue;
	}
	
	public String minSearchTipText() {
		return "Min multiplier search";
	}

	public double getMinSearch() {
		return minSearch;
	}

	public void setMinSearch(double minSearch) {
		this.minSearch = minSearch;
	}
	
	public String maxSearchTipText() {
		return "Max multiplier search";
	}

	public double getMaxSearch() {
		return maxSearch;
	}

	public void setMaxSearch(double maxSearch) {
		this.maxSearch = maxSearch;
	}
	
	public String numIterationsTipText() {
		return "Number of bisection iterations ";
	}

	public int getNumIterations() {
		return numIterations;
	}

	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}
	
	
	
	

}
