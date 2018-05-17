/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionLinear implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9026060542026964841L;

	/**
	 * 
	 */
	public PotentialFunctionLinear() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		return x;
	}

}
