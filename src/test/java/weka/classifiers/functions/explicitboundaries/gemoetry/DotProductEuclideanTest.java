package weka.classifiers.functions.explicitboundaries.gemoetry;

import org.junit.Before;
import org.junit.Test;

public class DotProductEuclideanTest {

	DotProduct dotP = null;
	@Before
	public void setUp() throws Exception {
		this.dotP = new DotProductEuclidean();
	}

	@Test
	public void testDotProduct() {
		DotProductTester.testDotProduct(this.dotP);
	}

}
