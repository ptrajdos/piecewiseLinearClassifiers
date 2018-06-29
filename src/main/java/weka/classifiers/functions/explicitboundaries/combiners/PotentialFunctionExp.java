/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionExp implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3561789352319040751L;
	
	protected double alpha=1;

	/**
	 * 
	 */
	public PotentialFunctionExp() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		return x*Math.exp(-this.alpha*x*x + 0.5)*Math.sqrt(2.0*this.alpha);
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
