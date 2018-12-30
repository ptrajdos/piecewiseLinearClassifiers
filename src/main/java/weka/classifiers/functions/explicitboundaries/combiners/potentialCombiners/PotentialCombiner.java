/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.core.Instance;

/**
 * @author Pawel Trajdos
 * @version 1.2.0 
 * The interface for classes that combine the outputs of multiple potential functions
 */
public interface PotentialCombiner {
	
	/**
	 * 
	 * @param inst -- instance to combine boundaries for
	 * @param boundaries -- boundaries to combine
	 * @param potFunction -- potential function to use
	 * @return class IDX
	 * @throws Exception when something goes wrong
	 */
	public int getCombinedBoundaries(Instance inst, DecisionBoundaries boundaries, PotentialFunction potFunction) throws Exception;

}
