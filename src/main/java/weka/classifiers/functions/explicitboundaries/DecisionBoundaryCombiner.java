/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import weka.core.Instance;

/**
 * @author pawel
 *
 */
public interface DecisionBoundaryCombiner {
	
	/**
	 * Get combined decision of the boundaries
	 * @param inst
	 * @return
	 * @throws Exception
	 */
	public int getDecision(Instance inst)throws Exception;
	
	/**
	 * Sets the boundaries to combine
	 * @param boundaries
	 * @throws Exception
	 */
	public void setBoundaries(DecisionBoundaries boundaries)throws Exception;

}
