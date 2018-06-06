package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;

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

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
