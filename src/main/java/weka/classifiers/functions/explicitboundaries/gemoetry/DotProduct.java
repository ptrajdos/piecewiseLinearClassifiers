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
	 * @throws Exception --  when instances or dataset are incompatible with each other
	 * @since 0.1.0
	 * @version 0.1.0
	 */
	public double dotProduct(Instances dataSet, Instance inst1, Instance inst2) throws Exception;
	
	/**
	 * Calculates the dot-product-induced norm of the vector
	 * @param dataSet
	 * @param vec
	 * @return
	 * @throws Exception -- when instances or dataset are incompatible with each other
	 * @since 0.1.0
	 * @version 0.1.0
	 */
	public double norm(Instances dataSet,Instance vec) throws Exception;
	
	/**
	 * Calculates the projection of instance 1 (inst1) on instance 2 (inst2). 
	 * The projection uses the above-defined dot product and norm.  
	 * @param dataSet -- dataset
	 * @param inst1 -- Instance 1
	 * @param inst2 -- Instance 2
	 * @return Projection of inst1 on inst2
	 * @throws Exception -- when instances or dataset are incompatible with each other
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public Instance projection(Instances dataSet, Instance inst1, Instance inst2)throws Exception;

}
