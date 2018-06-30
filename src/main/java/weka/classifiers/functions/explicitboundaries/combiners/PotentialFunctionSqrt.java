/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionSqrt implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4625002209576962151L;
	protected double alpha=1.0;

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

	/**
	 * 
	 */
	public PotentialFunctionSqrt() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		double val = (x/Math.sqrt(1+ this.alpha*x*x))*Math.sqrt(1+this.alpha);
		return val;
	}

}
