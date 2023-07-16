package weka.classifiers.functions.explicitboundaries.combiners;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.4.1
 *
 */

public class PotentialFunctionLinearTest extends PotentialTester {

	@Override
	public PotentialFunction getPotentialFunction() {
		
		return new PotentialFunctionLinear();
	}

	@Override
	public void testInfs() {
		//No test infs for linear function. 
		
	}


	

}
