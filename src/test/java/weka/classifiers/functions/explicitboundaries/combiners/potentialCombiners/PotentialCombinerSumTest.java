/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

/**
 * @author pawel
 *
 */
public class PotentialCombinerSumTest extends PotentialCombinerGeneralTest {

	@Override
	public PotentialCombiner getCombiner() {
		return new PotentialCombinerSum();
	}

	
}
