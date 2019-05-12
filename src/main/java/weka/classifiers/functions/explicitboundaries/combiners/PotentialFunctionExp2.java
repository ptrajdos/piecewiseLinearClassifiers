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
public class PotentialFunctionExp2 extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7439426502047267694L;




	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		double signum = x==0? 0: (x>0? 1:-1);
		double result = signum* (1- Math.exp(-this.alpha*x*x));
		return result;
	}
}
