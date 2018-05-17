/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionTahn implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2851294706086303366L;
	
	protected double alpha=1;

	/**
	 * 
	 */
	public PotentialFunctionTahn() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		return Math.tanh(this.alpha*x);
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
