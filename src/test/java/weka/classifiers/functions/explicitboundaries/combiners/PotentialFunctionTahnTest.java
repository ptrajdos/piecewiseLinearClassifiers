package weka.classifiers.functions.explicitboundaries.combiners;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PotentialFunctionTahnTest {

	PotentialFunctionTanh potFun = null;
	@Before
	public void setUp() throws Exception {
		this.potFun = new PotentialFunctionTanh();
	}

	@Test
	public void testGetPotentialValue() {
		PotentialTester.testPotential(this.potFun);
	}

	@Test
	public void testGetAlpha() {
		assertEquals(1.0, this.potFun.getAlpha(),1E-6);
	}

	@Test
	public void testSetAlpha() {
		double val =5.5;
		this.potFun.setAlpha(val);
		assertEquals(val, this.potFun.getAlpha(),1E-6);
	}
}
