/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.1.3
 *
 */
public class PotentialFunctionSign implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7972722946197996622L;


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		if(Double.isNaN(x))
			return Double.NaN;
		return x>0? 1:-1;
	}

}
