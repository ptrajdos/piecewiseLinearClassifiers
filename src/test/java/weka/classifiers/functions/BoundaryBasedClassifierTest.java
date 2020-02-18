package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.tools.tests.WekaGOEChecker;

public class BoundaryBasedClassifierTest extends AbstractClassifierTest {

	public BoundaryBasedClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryBasedClassifier();
	}

	
	
	 public static Test suite() {
		    return new TestSuite(BoundaryBasedClassifierTest.class);
		  }
	 
	 public void testForName() {
		 BoundaryBasedClassifier cla = new BoundaryBasedClassifier();
		 String[] options = cla.getOptions();
		 try {
			Classifier x = AbstractClassifier.forName("weka.classifiers.functions.BoundaryBasedClassifier", options);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Classifier forName Exception has been caught");
		}
	 }
	 
	 public void testTipTexts() {
		 WekaGOEChecker goe = new WekaGOEChecker();
		 goe.setObject(this.getClassifier());
		 goe.checkCallGlobalInfo();
		 goe.checkToolTipsCall();
	 }
	

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
