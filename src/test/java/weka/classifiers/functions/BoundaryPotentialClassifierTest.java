package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;

public class BoundaryPotentialClassifierTest extends AbstractClassifierTest{

	public BoundaryPotentialClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryPotentialClassifier();
	}
	public static Test suite() {
	    return new TestSuite(BoundaryPotentialClassifierTest.class);
	  }
 
 public static void main(String[] args){
	    junit.textui.TestRunner.run(suite());
}


}
