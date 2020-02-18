/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import static org.junit.Assert.*;

import org.junit.Test;

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
