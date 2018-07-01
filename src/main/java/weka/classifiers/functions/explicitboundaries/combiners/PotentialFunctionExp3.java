/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionExp3 implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3990467400963972621L;
	
	protected double alpha=1.0;

	/**
	 * 
	 */
	public PotentialFunctionExp3() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		double val = x*x*x*Math.exp(-this.alpha*x*x + 1.5)*Math.pow(3.0/(2.0*this.alpha), -1.5) ;
		return val;
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return this.alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	

}
