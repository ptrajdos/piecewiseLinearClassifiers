/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import java.lang.reflect.Field;

import weka.classifiers.functions.Logistic;
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
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * @author pawel trajdos
 * @since 1.3.0
 * @version 2.3.1
 *
 */
public class LogisticBoundary extends Logistic implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = -763786573937960677L;
	
	
	protected MajorityPlaneBoundaryModel defaultPlaneModel = null;
	
	/**
	 * Header of the dataset
	 */
	protected Instances dataHeader = null;
	
	protected DecisionBoundaryPlane boundary;
	
	protected ZeroR defaultModel;
	
	/**
	 * Accessible fields from the parent class
	 */
	
	 /** An attribute filter */
	  protected RemoveUseless m_AttFilter;

	  /** The filter used to make attributes numeric. */
	 protected NominalToBinary m_NominalToBinary;

	  /** The filter used to get rid of missing values. */
	  protected ReplaceMissingValues m_ReplaceMissingValues;

	/**
	 * 
	 */
	public LogisticBoundary() {
		super();
		
		this.defaultPlaneModel = new MajorityPlaneBoundaryModel();
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.NearestCentroidClassifier#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		if(!this.getDoNotCheckCapabilities())
			this.getCapabilities().testWithFail(data);
		
		this.defaultPlaneModel.buildDefaultModelPlane(data);
		
		if(this.defaultPlaneModel.isUseDefault()) {
			this.defaultModel = new ZeroR();
			this.defaultModel.buildClassifier(data);
		}else {
			super.buildClassifier(data);
			this.getParentFields();
			this.dataHeader = new Instances(this.filterInstances(data), 0);
			this.calculateBoundary();
		}
		
	}
	
	protected void getParentFields() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		Field repMissingVals = this.getClass().getSuperclass().getDeclaredField("m_ReplaceMissingValues");
		repMissingVals.setAccessible(true);
		this.m_ReplaceMissingValues = (ReplaceMissingValues) repMissingVals.get(this);
		
		Field mNom2Bin = this.getClass().getSuperclass().getDeclaredField("m_NominalToBinary");
		mNom2Bin.setAccessible(true);
		this.m_NominalToBinary = (NominalToBinary) mNom2Bin.get(this);
		
		
		Field mAttFilter = this.getClass().getSuperclass().getDeclaredField("m_AttFilter");
		mAttFilter.setAccessible(true);
		this.m_AttFilter = (RemoveUseless) mAttFilter.get(this);
		
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultPlaneModel.isUseDefault()) {
			return this.defaultPlaneModel.planeModel;
		}
		
		return this.boundary;
	}
	
	protected void calculateBoundary()throws Exception{
		if(this.defaultPlaneModel.useDefault)
			return;
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
				normalVec.setValue(a, params[attIdx++][0]);//TODO this is failing for leddigiths, dermatology, glass...
				//For BoundPotClassifier
				//Array Index out of Bound Exception, index 1!
			}
		}
		
		Plane tmpPlane = new LogisticBoundaryPlane(this.dataHeader);
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
		base.setMinimumNumberInstances(0);
		return base;
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.Logistic#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
		DebugSetter.setDebug(this.boundary, debug);
		DebugSetter.setDebug(this.defaultPlaneModel, debug);
	}

	/*
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new LogisticBoundary(), args);

	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		if(this.defaultPlaneModel.isUseDefault())
			return this.defaultModel.distributionForInstance(instance);
		
		double[] distribution;
		distribution  = new double[2];
		distribution[0] = 0.5*(this.boundary.getValue(instance) + 1.0);
		distribution[1] = 1 - distribution[0];
		
		
		return distribution;
	}

	protected Instance filterInstance(Instance instance) {
		    m_ReplaceMissingValues.input(instance);
		    instance = m_ReplaceMissingValues.output();
		    m_AttFilter.input(instance);
		    instance = m_AttFilter.output();
		    m_NominalToBinary.input(instance);
		    instance = m_NominalToBinary.output();
		return instance;
	}
	
	protected Instances filterInstances(Instances instances) throws Exception {
		Instances result = Filter.useFilter(instances, m_ReplaceMissingValues);
		result = Filter.useFilter(instances, m_AttFilter);
		result = Filter.useFilter(result, m_NominalToBinary);
		return result;
		
	}
	
	protected class LogisticBoundaryPlane extends Plane{

		public LogisticBoundaryPlane(Instances dataSpace) {
			super(dataSpace);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -1970067357108885170L;

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
			return super.projectOnPlane(filterInstances(instances));
		}

		@Override
		public Instances planeBasedInstances(Instances instances) throws Exception {
			return super.planeBasedInstances(filterInstances(instances));
		}

		@Override
		public Instance planeBasedInstance(Instance instance) throws Exception {
			return super.planeBasedInstance(filterInstance(instance));
		}
		
		
		
	}
	

}
