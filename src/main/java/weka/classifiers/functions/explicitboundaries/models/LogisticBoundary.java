/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DebugSetter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel trajdos
 * @since 1.3.0
 * @version 2.1.0
 *
 */
public class LogisticBoundary extends Logistic implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = -763786573937960677L;
	
	
	protected MajorityPlaneBoundaryModel defaultModel = null;
	
	/**
	 * Header of the dataset
	 */
	protected Instances dataHeader = null;
	
	protected DecisionBoundaryPlane boundary;

	/**
	 * 
	 */
	public LogisticBoundary() {
		super();
		
		this.defaultModel = new MajorityPlaneBoundaryModel();
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.NearestCentroidClassifier#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		if(!this.getDoNotCheckCapabilities())
			this.getCapabilities().testWithFail(data);
		super.buildClassifier(data);
		this.defaultModel.buildDefaultModelPlane(data);
		this.dataHeader = new Instances(data, 0);
		
		this.calculateBoundary();
		
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultModel.isUseDefault()) {
			return this.defaultModel.planeModel;
		}
		
		return this.boundary;
	}
	
	protected void calculateBoundary()throws Exception{
		double[][] params = this.coefficients();
		//The model is assumed to be a binary classifier -- only one intercept/offset term is present
		double offset = params[0][0];
		
		Instance normalVec = new DenseInstance(this.dataHeader.numAttributes());
		normalVec.setDataset(this.dataHeader);
		int numAttrs = this.dataHeader.numAttributes();
		int classIxd = this.dataHeader.classIndex();
		
		
		int attIdx=1;
		for(int a=0;a<numAttrs;a++) {
			if(a != classIxd) {
				normalVec.setValue(a, params[attIdx++][0]);
			}
		}
		
		Plane tmpPlane = new Plane(this.dataHeader);
		tmpPlane.setNormalVector(normalVec);
		tmpPlane.setOffset(offset);
		
		this.boundary = new DecisionBoundaryPlane(this.dataHeader, 0, 1, tmpPlane);
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.NearestCentroidClassifier#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities base = super.getCapabilities();
		base.disableAll();
		base.enable(Capability.NUMERIC_ATTRIBUTES);
		base.enable(Capability.BINARY_CLASS);
		return base;
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.Logistic#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
		DebugSetter.setDebug(this.boundary, debug);
		DebugSetter.setDebug(this.defaultModel, debug);
	}

	/*
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new LogisticBoundary(), args);

	}


}
