/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.core.Instance;

/**
 * @author Pawel Trajdos
 * @version 1.1.0 
 * The interface for classes that combine the outputs of multiple potential functions
 */
public interface PotentialCombiner {
	
	/**
	 * 
	 * @param inst -- instance to combine boundaries for
	 * @param boundaries -- boundaries to combine
	 * @return class IDX
	 * @throws Exception when something goes wrong
	 */
	public int getCombinedBoundaries(Instance inst, DecisionBoundaries boundaries) throws Exception;

}
