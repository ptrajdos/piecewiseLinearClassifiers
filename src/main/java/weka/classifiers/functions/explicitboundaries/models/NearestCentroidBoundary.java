/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.NearestCentroidClassifier;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.DotProduct;
import weka.classifiers.functions.explicitboundaries.gemoetry.DotProductEuclidean;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Nearest Centroind Classifier with explicit boundaries
 * Only binary classifiers.
 * @author Pawel Trajdos
 *
 */
public class NearestCentroidBoundary extends NearestCentroidClassifier implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5904651566938429421L;
	
	protected DotProduct dotProduct;
	
	protected MajorityPlaneBoundaryModel defaultModel = null;

	/**
	 * 
	 */
	public NearestCentroidBoundary() {
		this.dotProduct = new DotProductEuclidean();
		this.defaultModel = new MajorityPlaneBoundaryModel();
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.NearestCentroidClassifier#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		super.buildClassifier(data);
		this.defaultModel.buildDefaultModelPlane(data);
		
	}



	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundaries()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultModel.isUseDefault()) {
			return this.defaultModel.planeModel;
		}
		
		Instance normalVec = new DenseInstance(this.centroids[0]);
		normalVec.setDataset(this.centroids[0].dataset());
		Instance middleVec = new DenseInstance(this.centroids[0]);
		middleVec.setDataset(this.centroids[0].dataset());
		
		int classAttrib = normalVec.classIndex();
		double[] cent0D = this.centroids[0].toDoubleArray();
		double[] cent1D = this.centroids[1].toDoubleArray();
		
		for(int a=0;a<classAttrib;a++){
			if(a == classAttrib){
				normalVec.setClassMissing();
				middleVec.setClassMissing();
			}
			normalVec.setValue(a, cent0D[a] - cent1D[a]);
			middleVec.setValue(a, 0.5*(cent0D[a] + cent1D[a]));
		}
		
		double offset = - this.dotProduct.dotProduct(normalVec.dataset(), normalVec, middleVec);
		
		DecisionBoundaryPlane boundary = new DecisionBoundaryPlane(normalVec.dataset(),0, 1);
		boundary.getDecisionPlane().setNormalVector(normalVec);
		boundary.getDecisionPlane().setOffset(offset);
		
		return boundary;
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.NearestCentroidClassifier#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities base = super.getCapabilities();
		base.disable(Capability.NOMINAL_CLASS);
		base.enable(Capability.BINARY_CLASS);
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
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new NearestCentroidBoundary(), args);

	}

}
