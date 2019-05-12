package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;

public class BoundaryAndCentroidClassifierTest extends AbstractClassifierTest{

	public BoundaryAndCentroidClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryAndCentroidClassifier();
	}
	
	 public static Test suite() {
		    return new TestSuite(BoundaryAndCentroidClassifierTest.class);
		  }
	 
	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

	

}
