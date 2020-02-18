package weka.classifiers.functions.explicitboundaries.combiners;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 *
 */
public class PotentialFunctionSignTest  {

	@Test
	public void testPotential() {
		PotentialTester.testPotential(new PotentialFunctionSign());
		
	}
	

}
