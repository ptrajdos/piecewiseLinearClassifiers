/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.0.0
 */
public class PotentialFunctionTanh extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2851294706086303366L;


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		return Math.tanh(this.alpha*x);
	}


}
