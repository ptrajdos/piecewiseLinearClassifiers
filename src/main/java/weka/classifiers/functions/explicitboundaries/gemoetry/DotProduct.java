package weka.classifiers.functions.explicitboundaries.gemoetry;

import weka.core.Instance;
import weka.core.Instances;

public interface DotProduct {
	
	/**
	 * Calculate dot product between instances (vecttors)
	 * @param dataSet -- dataset
	 * @param inst1 -- Instance 1
	 * @param inst2 -- Instance 2
	 * @return Dot product
	 * @throws Exception 
	 */
	public double dotProduct(Instances dataSet, Instance inst1, Instance inst2) throws Exception;
	
	/**
	 * Calculates the dot-product-induced norm of the vector
	 * @param dataSet
	 * @param vec
	 * @return
	 * @throws Exception 
	 */
	public double norm(Instances dataSet,Instance vec) throws Exception;

}
