package weka.classifiers.functions.explicitboundaries;

import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.DebugSetter;
import weka.core.Debuggable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class DecisionBoundaryPlane extends DecisionBoundary implements Debuggable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3454683549175862055L;
	
	protected boolean debug;
	
	protected double normFactor=1;
	
	
	protected Plane decisionPlane = null;

	public DecisionBoundaryPlane(Instances data, int class1Idx, int class2Idx, Plane plane) throws Exception {
		super(data, class1Idx, class2Idx);
		this.decisionPlane = plane;
		this.getNormalizingFactor(data);
	}
	
	public DecisionBoundaryPlane(Instances data, int class1Idx, int class2Idx)throws Exception{
		this(data, class1Idx, class2Idx, new Plane(data));
		this.getNormalizingFactor(data);
	}

	@Override
	public int getIndex(Instance instance) throws Exception {
		return this.decisionPlane.sideOfThePlane(instance)>0? this.class1Idx:this.class2Idx;
	}
	
	private void getNormalizingFactor(Instances data) throws Exception {
		int numInstances = data.numInstances();
		double max=0;
		double val=0;
		for(int i=0;i<numInstances;i++) {
			Instance tmpInstance = data.get(i);
			val = this.decisionPlane.sideOfThePlane(tmpInstance);
			val = val>=0? val:-val;
			
			if(val>max) {
				max=val;
			}
		}
		if(Utils.eq(max, 0))
			this.normFactor=1.0;
		else
			this.normFactor = max;
			
		
	}

	/**
	 * @return the decisionPlane
	 */
	public Plane getDecisionPlane() {
		return this.decisionPlane;
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.DecisionBoundary#toString()
	 */
	@Override
	public String toString() {
		StringBuffer description = new StringBuffer();
		description.append(super.toString());
		description.append(this.decisionPlane.toString());
		return description.toString();
	}

	@Override
	public double classify(Instance instance) throws Exception {
		return this.getIndex(instance);
	}

	@Override
	public double getValue(Instance instance) throws Exception {
		double value = this.decisionPlane.sideOfThePlane(instance);
		value/=this.normFactor;
		
		if(value<-1)
			value=-1;
		
		if(value>1)
			value=1;
		
		return value;
	}

	@Override
	public double getDistance(Instance instance) throws Exception {
		return this.getDecisionPlane().distanceToPlane(instance);
	}
	
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		DebugSetter.setDebug(this.decisionPlane, debug);
	}


}
