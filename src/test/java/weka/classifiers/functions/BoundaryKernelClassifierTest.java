package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.tools.SerializationTester;
import weka.tools.tests.WekaGOEChecker;

public class BoundaryKernelClassifierTest extends AbstractClassifierTest {

	public BoundaryKernelClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryKernelClassifier();
	}

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifierTest.class);
	  }
	
	public void testSerialization() {
		SerializationTester.checkSerialization(this.getClassifier());
	}
	
	public void testGlobalInfoAndTips() {
		WekaGOEChecker checker = new WekaGOEChecker();
		checker.setObject(this.getClassifier());
		assertTrue("GlobalInfo call", checker.checkCallGlobalInfo());
		assertTrue("TipTexts call", checker.checkToolTipsCall());
	}
	

}
