package weka.classifiers.functions.explicitboundaries.gemoetry;

import weka.core.Instance;

public interface IInstanceNormCalc {
	
	/**
	 * Calculates the norm of the instance.
	 * <b> Only numeric </b> attributes are used.
	 * @author pawel trajdos
	 * @param vec
	 * @return vector/instance norm
	 * @throws Exception
	 * 
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public double norm(Instance vec)throws Exception;
	
	/**
	 * Returns normalized instance. <b>Only numeric </b> attributes are normalized.
	 * If the norm of the vector is 0, then the vector is not normalized.
	 * @author pawel trajdos
	 * @param inst -- instance to normalize
	 * @return
	 * @throws Exception
	 * 
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public Instance normalize(Instance inst) throws Exception;

}
