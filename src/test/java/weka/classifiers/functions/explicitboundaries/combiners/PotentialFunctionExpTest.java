package weka.classifiers.functions.explicitboundaries.combiners;

/**
 * 
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.4.1
 *
 */
public class PotentialFunctionExpTest extends PotentialTester {

	@Override
	public PotentialFunction getPotentialFunction() {
	
		return new PotentialFunctionExp();
	}

	

}
