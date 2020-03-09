package weka.classifiers.functions;

import static org.junit.Assert.fail;

import org.junit.Test;

import weka.tools.SerialCopier;

public class BoundaryBasedClassifier_M_Test {

	@Test
	public void test() {
		BoundaryBasedClassifier bCl = new BoundaryBasedClassifier();
		try {
			BoundaryBasedClassifier copy = (BoundaryBasedClassifier) SerialCopier.makeCopy(bCl);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception has been caught");
		}
	}

}
