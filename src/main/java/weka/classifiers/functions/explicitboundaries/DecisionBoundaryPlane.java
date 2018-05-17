package weka.classifiers.functions.explicitboundaries;

import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionBoundaryPlane extends DecisionBoundary {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3454683549175862055L;
	
	protected Plane decisionPlane = null;

	public DecisionBoundaryPlane(Instances data, int class1Idx, int class2Idx, Plane plane) throws Exception {
		super(data, class1Idx, class2Idx);
		this.decisionPlane = plane;
	}
	
	public DecisionBoundaryPlane(Instances data, int class1Idx, int class2Idx)throws Exception{
		this(data, class1Idx, class2Idx, new Plane(data));
	}

	@Override
	public int getIndex(Instance instance) throws Exception {
		return this.decisionPlane.sideOfThePlane(instance)>0? this.class1Idx:this.class2Idx;
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
		return this.decisionPlane.sideOfThePlane(instance);
	}

	@Override
	public double getDistance(Instance instance) throws Exception {
		return this.getDecisionPlane().distanceToPlane(instance);
	}
	
	

}
