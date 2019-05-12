/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.0.0
 *
 */
public class PotentialFunctionExp4 extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3561789352319040751L;


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		return Math.exp(-this.alpha*x*x);
	}

}
