package weka.classifiers.functions.explicitboundaries.gemoetry;

import weka.core.Instance;

public interface DotProduct  extends IInstanceNormCalc{
	
	/**
	 * Calculate dot product between instances (vecttors)
	 * @param inst1 -- Instance 1
	 * @param inst2 -- Instance 2
	 * @return Dot product
	 * @throws Exception --  when instances or dataset are incompatible with each other
	 * @since 0.1.0
	 * @version 1.4.0
	 */
	public double dotProduct(Instance inst1, Instance inst2) throws Exception;
	
	/**
	 * Calculates the dot-product-induced norm of the vector
	 * @param vec
	 * @return
	 * @throws Exception -- when instances or dataset are incompatible with each other
	 * @since 0.1.0
	 * @version 1.4.0
	 */
	public double norm(Instance vec) throws Exception;
	
	/**
	 * Calculates the projection of instance 1 (inst1) on instance 2 (inst2). 
	 * The projection uses the above-defined dot product and norm.  
	 * @param inst1 -- Instance 1
	 * @param inst2 -- Instance 2
	 * @return Projection of inst1 on inst2
	 * @throws Exception -- when instances or dataset are incompatible with each other
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public Instance projection(Instance inst1, Instance inst2)throws Exception;

}
