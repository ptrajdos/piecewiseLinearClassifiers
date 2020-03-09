package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.IDecisionBoundary;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionExp;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.UtilsPT;

/**
 * Classifier with boundary-based potential function
 * @author pawel trajdos
 * @since 2.1.0
 * @version 2.1.0
 *
 */

public class BoundaryPotentialClassifier extends SingleClassifierEnhancerBoundary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2474381389913442147L;
	
	protected PotentialFunction potential;
	
	protected boolean normalizeOutput=true;
	
	public BoundaryPotentialClassifier(ClassifierWithBoundaries classifier) {
		this.setClassifier(classifier);
		this.potential = new PotentialFunctionExp();
	}

	public BoundaryPotentialClassifier() {
		this(new NearestCentroidBoundary());
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		this.getCapabilities().testWithFail(data);
		this.boundClassRef.buildClassifier(data);
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#distributionForInstance(weka.core.Instance)
	 */
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		double sign = 0;
		IDecisionBoundary bound = this.boundClassRef.getBoundary();
		sign = Math.signum(bound.getValue(instance));
		double dist = bound.getDistance(instance);
		double potVal = this.potential.getPotentialValue(sign*dist);
		int nClass = instance.dataset().numClasses();
		double[] distr = new double[nClass];
		if(sign>0) {
			distr[0]=Math.abs(potVal);
		}else {
			distr[1]=Math.abs(potVal);
		}
		
		if(this.normalizeOutput) {
			distr = UtilsPT.softMax(distr);
		}
		return distr;
	}

	/**
	 * @return the potential
	 */
	public PotentialFunction getPotential() {
		return this.potential;
	}

	/**
	 * @param potential the potential to set
	 */
	public void setPotential(PotentialFunction potential) {
		this.potential = potential;
	}
	
	public String potentialTipText() {
		return "Potential function to use with the classifier";
	}
 
	/**
	 * @return the normalizeOutput
	 */
	public boolean isNormalizeOutput() {
		return this.normalizeOutput;
	}

	/**
	 * @param normalizeOutput the normalizeOutput to set
	 */
	public void setNormalizeOutput(boolean normalizeOutput) {
		this.normalizeOutput = normalizeOutput;
	}
	
	public String  normalizeOutputTipText(){
		return "Determines whether the normalization is done";
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\tPotential function to use "+
		          "(default: weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionExp).\n",
			      "P", 1, "-P"));
		
		newVector.addElement(new Option(
			      "\tDetermines whether the output normalization if performed"+
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
		
		this.setPotential((PotentialFunction) UtilsPT.parseObjectOptions(options, "P", new PotentialFunctionExp(), PotentialFunction.class));
		this.setNormalizeOutput(Utils.getFlag("N", options));
		super.setOptions(options);
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#getOptions()
	 */
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		options.add("-P");
		options.add(UtilsPT.getClassAndOptions(this.getPotential()));
		
		if(this.isNormalizeOutput())
			options.add("-N");
		
	
		Collections.addAll(options, super.getOptions());
	    
	    return options.toArray(new String[0]);
	}
	
	public String globalInfo() {
		return "Class  that implements algorithm that makes potential field around the decision plane";
	}


}
