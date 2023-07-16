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
public class PotentialFunctionExp extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3561789352319040751L;
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		if(Double.isInfinite(x))
			return 0.0;
		return x*Math.exp(-this.alpha*x*x + 0.5)*Math.sqrt(2.0*this.alpha);
	}

}
