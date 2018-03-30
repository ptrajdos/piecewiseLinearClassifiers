/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.FLDA;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Capabilities.Capability;

/**
 * @author pawel
 *
 */
public class FLDABoundary extends FLDA implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8290678589313422047L;

	/**
	 * 
	 */
	public FLDABoundary() {
		super();
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
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
		base.enable(Capability.BINARY_CLASS);
		return base;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new FLDABoundary(), args);

	}

}
