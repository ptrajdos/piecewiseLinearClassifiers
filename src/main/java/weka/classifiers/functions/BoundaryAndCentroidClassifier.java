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
 * @since 2.0.0
 * @version 2.1.2
 *
 */
public class BoundaryAndCentroidClassifier extends SingleClassifierEnhancerBoundary {

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
	
	
	
	

	/**
	 * 
	 */
	public BoundaryAndCentroidClassifier(ClassifierWithBoundaries boundClassifier) {
		super();
		this.setClassifier(boundClassifier);
		this.prototypeProto = new MahalanobisPrototype();
		this.potFunction = new PotentialFunctionExp4();
	}
	
	public BoundaryAndCentroidClassifier() {
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
		double[] classFreqs = InstancesOperator.classFreq(data);
		for(int i =0;i<classFreqs.length;i++) {
			classFreqs[i] = Math.ceil(classFreqs[i]*numIinsts);
		}
		
		this.defaultModel = null;
		if(Utils.smOrEq(classFreqs[0], 1) | Utils.smOrEq(classFreqs[1], 1)) {
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
		
		for(int c =0 ;c<numClasses;c++) {
			this.classProtos[c] = (IClusterPrototype) SerialCopier.makeCopy(this.prototypeProto);
			this.classProtos[c].build(splittedData[c]);
			this.protoPlaneSide[c] = Math.signum(bnd.getValue(this.classProtos[c].getCenterPoint()));
			proto2Bnd[c] = Math.signum(bnd.getValue(this.classProtos[c].getCenterPoint()))*bnd.getDistance(this.classProtos[c].getCenterPoint());
			
			inst2CentDist = new double[splittedData[c].numInstances()];
			
			for(int i=0;i< inst2CentDist.length;i++) {
				tmpInst = splittedData[c].get(i);
				tmpDist = Math.signum(bnd.getValue(tmpInst))*bnd.getDistance(tmpInst);
				inst2CentDist[i] = proto2Bnd[c] - tmpDist;
				
			}
			this.stdDevs[c] = UtilsPT.stdDev(inst2CentDist);	
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
			
			pot1 = this.potFunction.getPotentialValue(centDist);
			pot2 = this.potFunction.getPotentialValue(planeDirDist);
			
			potentials[c] = this.proportion*pot1 + (1-this.proportion)*pot2 + this.eps;
			sum+=potentials[c];
			
		}
		if(this.normalize)
			Utils.normalize(potentials, sum);
		
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
		
		if(this.normalize)
			options.add("-N");
		
		
		Collections.addAll(options, super.getOptions());
	    
	    return options.toArray(new String[0]);
	}
	
	
	

}
