/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.FLDA;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Capabilities.Capability;

/**
 * @author pawel
 *
 */
//TODO There is an issue with incompatible instances produced by this classifiers.
//It bothers the following datasets Faults, optdigits and glass
//TODO fix it!!!
public class FLDABoundary extends FLDA implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8290678589313422047L;
	
	/**
	 * Default model
	 */
	protected MajorityPlaneBoundaryModel defaultModel = null;
	
	protected ZeroR  alternativeModel = null;

	/**
	 * 
	 */
	public FLDABoundary() {
		super();
		this.defaultModel = new MajorityPlaneBoundaryModel();
		this.alternativeModel = new ZeroR();
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultModel.isUseDefault()) {
			return this.defaultModel.getPlaneModel();
		}
		
		double offset = -this.m_Threshold;
		Instance normalVec = new DenseInstance(this.m_Data.numAttributes());
		normalVec.setDataset(this.m_Data);
		int numAttrs = this.m_Data.numAttributes();
		int classIxd = this.m_Data.classIndex();
		int attCounter =0;
		for(int a = 0;a<numAttrs;a++ ){
			if(a!= classIxd){
				normalVec.setValue(a, this.m_Weights.get(attCounter++));
			}
		}
		
		Plane tmpPlane = new Plane(this.m_Data);
		tmpPlane.setNormalVector(normalVec);
		tmpPlane.setOffset(offset);
		
		DecisionBoundaryPlane boundary = new DecisionBoundaryPlane(this.m_Data, 0, 1, tmpPlane); 
		
		
		return boundary;
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities base = super.getCapabilities();
		base.disable(Capability.NOMINAL_CLASS);
		base.disable(Capability.NUMERIC_CLASS);
		base.enable(Capability.BINARY_CLASS);
		return base;
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.FLDA#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances insts) throws Exception {
		super.buildClassifier(insts);
		this.defaultModel.buildDefaultModelPlane(insts);
		if(this.defaultModel.isUseDefault())
			this.alternativeModel.buildClassifier(insts);
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.FLDA#distributionForInstance(weka.core.Instance)
	 */
	@Override
	public double[] distributionForInstance(Instance inst) throws Exception {
		double[] distribution = null;
		if(this.defaultModel.isUseDefault()) {
			distribution = this.alternativeModel.distributionForInstance(inst);
		}else {
			distribution = super.distributionForInstance(inst); 
		}
		
		return distribution;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new FLDABoundary(), args);

	}

}
