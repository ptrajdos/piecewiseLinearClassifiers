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
public class PotentialFunctionSqrt extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4625002209576962151L;

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		double val = (x/Math.sqrt(1+ this.alpha*x*x))*Math.sqrt(1+this.alpha);
		return val;
	}

}
