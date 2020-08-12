/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;


import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.functions.NearestCentroidClassifier;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.DotProduct;
import weka.classifiers.functions.explicitboundaries.gemoetry.DotProductEuclidean;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DebugSetter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UtilsPT;

/**
 * Nearest Centroind Classifier with explicit boundaries
 * Only binary classifiers.
 * @author Pawel Trajdos
 * @since 0.1.0
 * @version 2.2.1
 *
 */
public class NearestCentroidBoundary extends NearestCentroidClassifier implements ClassifierWithBoundaries {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5904651566938429421L;
	
	protected DotProduct dotProduct;
	
	protected MajorityPlaneBoundaryModel defaultModel = null;
	
	protected DecisionBoundaryPlane boundary;

	/**
	 * 
	 */
	public NearestCentroidBoundary() {
		super();
		this.dotProduct = new DotProductEuclidean();
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
		this.calculateBoundary(data);	
	}



	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundaries()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultModel.isUseDefault() | this.boundary==null) {
			return this.defaultModel.planeModel;
		}
		return this.boundary;
		
	}
	
	protected void calculateBoundary(Instances data)throws Exception {
		this.defaultModel.buildDefaultModelPlane(data);
		if(this.defaultModel.useDefault)
			return;
		Instance normalVec = new DenseInstance(this.getCentroids()[0]);
		normalVec.setDataset(this.getCentroids()[0].dataset());
		Instance middleVec = new DenseInstance(this.getCentroids()[0]);
		middleVec.setDataset(this.getCentroids()[0].dataset());
		
		int classAttrib = normalVec.classIndex();
		double[] cent0D = this.getCentroids()[0].toDoubleArray();
		double[] cent1D = this.getCentroids()[1].toDoubleArray();
		
		int numAttribs = data.numAttributes();
		//classAttrib
		for(int a=0;a<numAttribs;a++){
			if(a == classAttrib){
				normalVec.setClassMissing();
				middleVec.setClassMissing();
				continue;
			}
			normalVec.setValue(a, cent0D[a] - cent1D[a]);
			middleVec.setValue(a, 0.5*(cent0D[a] + cent1D[a]));
		}
		
		double offset = - this.dotProduct.dotProduct(normalVec, middleVec);
		
		this.boundary = new DecisionBoundaryPlane(normalVec.dataset(),0, 1);
		this.boundary.getDecisionPlane().setNormalVector(normalVec);
		this.boundary.getDecisionPlane().setOffset(offset);
		this.boundary.getDecisionPlane().setDotProduct(this.dotProduct);
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

	
	/**
	 * @return the dotProduct
	 */
	public DotProduct getDotProduct() {
		return this.dotProduct;
	}



	/**
	 * @param dotProduct the dotProduct to set
	 */
	public void setDotProduct(DotProduct dotProduct) {
		this.dotProduct = dotProduct;
		if(this.boundary != null)
			this.boundary.getDecisionPlane().setDotProduct(dotProduct);
	}

	@SuppressWarnings("static-method")
	public String dotProductTipText() {
		return "Set object to calculate the dot product";
	}


	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
		DebugSetter.setDebug(this.boundary, debug);
		DebugSetter.setDebug(this.defaultModel, debug);
		DebugSetter.setDebug(this.dotProduct, debug);
	}
	


	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		 newVector.addElement(new Option(
			      "\tThe dot product to use "+
		          "(default: weka.classifiers.functions.explicitboundaries.gemoetry.DotProductEuclidean).\n",
			      "DP", 1, "-DP"));
		 newVector.addAll(Collections.list(super.listOptions()));
		    
		return newVector.elements();
	}



	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
	    
	    
	    
	    options.add("-DP");
	    options.add(UtilsPT.getClassAndOptions(this.getDotProduct()));
	    Collections.addAll(options, super.getOptions());
	    
	    return options.toArray(new String[0]);
	}



	@Override
	public void setOptions(String[] options) throws Exception {
		this.setDotProduct((DotProduct) UtilsPT.parseObjectOptions(options, "DP", new DotProductEuclidean(), DotProduct.class));
		
		super.setOptions(options);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new NearestCentroidBoundary(), args);

	}



}
