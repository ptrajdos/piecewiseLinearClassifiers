package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;

public class BoundaryBasedClassifier2Test extends AbstractClassifierTest {

	public BoundaryBasedClassifier2Test(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		BoundaryBasedClassifier bc =new BoundaryBasedClassifier();
		bc.setUseCalibrator(true);
		return bc;
	}

	
	
	 public static Test suite() {
		    return new TestSuite(BoundaryBasedClassifier2Test.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
