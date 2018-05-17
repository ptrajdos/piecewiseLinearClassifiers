package weka.classifiers.functions.explicitboundaries.combiners;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PotentialFunctionSignTest {

	protected PotentialFunctionSign potFun = null;
	@Before
	public void setUp() throws Exception {
		this.potFun = new PotentialFunctionSign();
	}

	@Test
	public void testGetPotentialValue() {
		PotentialTester.testPotential(potFun);
	}

}
