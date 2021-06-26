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
import weka.core.Capabilities.Capability;
import weka.core.DebugSetter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.3.1
 */

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
	
	protected DecisionBoundaryPlane boundary;

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
		return this.boundary;
	}
	
	protected void calculateBoundary()throws Exception {
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
		
		Plane tmpPlane = new FLDAPlane(this.m_Data);
		tmpPlane.setNormalVector(normalVec);
		tmpPlane.setOffset(offset);
		
		this.boundary = new DecisionBoundaryPlane(this.m_Data, 0, 1, tmpPlane); 	
	}
	
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#getCapabilities()
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
	 * @see weka.classifiers.functions.FLDA#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances insts) throws Exception {
		if(!this.getDoNotCheckCapabilities())
			this.getCapabilities().testWithFail(insts);
		//TODO this method changes the input set
		super.buildClassifier(insts);
		this.defaultModel.buildDefaultModelPlane(insts);
		if(this.defaultModel.isUseDefault())
			this.alternativeModel.buildClassifier(insts);
		this.calculateBoundary();
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
			
			distribution  = new double[2];
			distribution[0] = 0.5*(this.boundary.getValue(inst) + 1.0);
			distribution[1] = 1 - distribution[0];
		}
		
		return distribution;
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
		DebugSetter.setDebug(this.boundary, debug);
		DebugSetter.setDebug(this.defaultModel, debug);
		this.alternativeModel.setDebug(debug);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new FLDABoundary(), args);

	}
	
	protected Instance filterInstance(Instance inst) {
		Instance tmpInst;
		m_RemoveUseless.input(inst);
	    tmpInst = m_RemoveUseless.output();
		return tmpInst;
		
	}
	
	private class FLDAPlane extends Plane {

		public FLDAPlane(Instances dataSpace) {
			super(dataSpace);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -4232940652337367592L;

		@Override
		public double distanceToPlane(Instance vec) throws Exception {
			return super.distanceToPlane(filterInstance(vec));
		}

		@Override
		public double sideOfThePlane(Instance vec) throws Exception {
			return super.sideOfThePlane(filterInstance(vec));
		}

		@Override
		public Instance projectOnPlane(Instance inst) throws Exception {
			return super.projectOnPlane(filterInstance(inst));
		}

		@Override
		public Instances projectOnPlane(Instances instances) throws Exception {
			return super.projectOnPlane(Filter.useFilter(instances, m_RemoveUseless));
		}

		@Override
		public Instances planeBasedInstances(Instances instances) throws Exception {
			return super.planeBasedInstances(Filter.useFilter(instances, m_RemoveUseless));
		}

		@Override
		public Instance planeBasedInstance(Instance instance) throws Exception {
			return super.planeBasedInstance(filterInstance(instance));
		}
		
	}

}
