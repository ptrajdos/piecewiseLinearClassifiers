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
public class PotentialFunctionExp3 extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3990467400963972621L;
	


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		if(Double.isInfinite(x))
			return 0;
		
		double powX = x*x*x;
		if(Double.isInfinite(powX))
			return 0.0;
		
		double val = powX*Math.exp(-this.alpha*x*x + 1.5)*Math.pow(3.0/(2.0*this.alpha), -1.5) ;
		return val;
	}	

}
