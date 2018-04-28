package weka.classifiers.functions.explicitboundaries.combiners;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PotentialFunctionLinearTest {

	PotentialFunctionLinear potFun = null;
	@Before
	public void setUp() throws Exception {
		this.potFun = new PotentialFunctionLinear();
	}

	@Test
	public void testPotentialFunctionLinear() {
		PotentialTester.testPotential(potFun);
	}

}
