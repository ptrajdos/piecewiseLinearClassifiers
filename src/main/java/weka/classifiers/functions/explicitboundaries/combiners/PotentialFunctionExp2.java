/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionExp2 implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7439426502047267694L;
	protected double alpha=1;

	/**
	 * 
	 */
	public PotentialFunctionExp2() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		double signum = x==0? 0: (x>0? 1:-1);
		double result = signum*Math.exp(-this.alpha*x*x);
		return result;
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
