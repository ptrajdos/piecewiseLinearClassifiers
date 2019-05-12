/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.0.0
 */
public class PotentialFunctionLinear implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9026060542026964841L;

	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		return x;
	}

}
