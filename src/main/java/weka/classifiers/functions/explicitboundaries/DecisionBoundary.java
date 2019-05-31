/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import java.io.Serializable;

import weka.core.Attribute;
import weka.core.Instances;

/**
 * @author pawel
 *
 */
public abstract class DecisionBoundary implements Serializable, IDecisionBoundary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2027243933463044232L;
	
	/*
	 * Use plane and abstract class for non-plane decision boundaries
	 */
	
	protected Instances datasetHeader = null;
	protected int class1Idx = 0;
	protected int class2Idx = 0;
	
	/**
	 * @throws Exception 
	 * 
	 */
	public DecisionBoundary(Instances data, int class1Idx, int class2Idx) throws Exception {
		this.datasetHeader = new Instances(data,0);
		this.class1Idx = class1Idx;
		this.class2Idx = class2Idx;
		
		Attribute classAttribute = this.datasetHeader.attribute(this.datasetHeader.classIndex());
		if ( !(classAttribute.isNominal() | classAttribute.isString()))
			throw new Exception("The class attribute must be Nominal or String");
		
		int values = classAttribute.numValues();
		if(this.class1Idx< 0 || this.class2Idx<0)
			throw new Exception("Indices cannot be negative");
		
		if(this.class1Idx>= values || this.class2Idx >= values)
			throw new Exception("Incorrect class index");	
	}
	
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.IDecisionBoundary#getDatasetHeader()
	 */
	@Override
	public Instances getDatasetHeader() {
		return this.datasetHeader;
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.IDecisionBoundary#getClass1Idx()
	 */
	@Override
	public int getClass1Idx() {
		return this.class1Idx;
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.IDecisionBoundary#getClass2Idx()
	 */
	@Override
	public int getClass2Idx() {
		return this.class2Idx;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		if(this.datasetHeader == null){
			return "Empty Object";
		}
		
		Attribute classAttrib = this.datasetHeader.attribute(this.datasetHeader.classIndex());
		
		StringBuffer strBuff = new StringBuffer();
		
		strBuff.append("Decision Boundary:\n");
		strBuff.append("Classes: " + classAttrib.value(this.class1Idx) + "; " + classAttrib.value(this.class2Idx)+"\n");
		return strBuff.toString();
	}
	
	

}
