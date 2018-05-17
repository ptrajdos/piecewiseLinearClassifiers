/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

/**
 * @author pawel
 *
 */
public interface PotentialFunction {
	
	/**
	 * Calculates the potential value for x
	 * @param x
	 * @return
	 * @throws Exception
	 */
	public double getPotentialValue(double x)throws Exception;

}
