package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOTest;

public class SMOLinearBoundaryTest extends SMOTest {

	public SMOLinearBoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new FLDABoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(SMOLinearBoundaryTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}

	

}
