/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

/**
 * Test class
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 */
public class PotentialCombinerMaxMedTest extends PotentialCombinerGeneralTest {

	@Override
	public PotentialCombiner getCombiner() {
		return new PotentialCombinerMaxMed();
	}

	

}
