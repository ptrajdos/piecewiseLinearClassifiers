/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;

/**
 * @author pawel
 *
 */
public class PotentialFunctionAsymetric implements PotentialFunction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3467311843369730695L;
	
	protected PotentialFunction potential1 = new PotentialFunctionSign();
	
	protected PotentialFunction potential2 = new PotentialFunctionSign();
	
	

	/**
	 * 
	 */
	public PotentialFunctionAsymetric() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction#getPotentialValue(double)
	 */
	@Override
	public double getPotentialValue(double x) throws Exception {
		double potential=0;
		if(x<0) {
			potential = this.potential1.getPotentialValue(x);
		}else {
			potential = this.potential2.getPotentialValue(x);
		}
		return potential;
	}

	/**
	 * @return the potential1
	 */
	public PotentialFunction getPotential1() {
		return this.potential1;
	}

	/**
	 * @param potential1 the potential1 to set
	 */
	public void setPotential1(PotentialFunction potential1) {
		this.potential1 = potential1;
	}

	/**
	 * @return the potential2
	 */
	public PotentialFunction getPotential2() {
		return this.potential2;
	}

	/**
	 * @param potential2 the potential2 to set
	 */
	public void setPotential2(PotentialFunction potential2) {
		this.potential2 = potential2;
	}

	
	
}
