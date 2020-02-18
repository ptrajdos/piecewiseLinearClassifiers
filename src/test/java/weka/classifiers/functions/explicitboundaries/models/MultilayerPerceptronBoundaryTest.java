package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptronTest;

public class MultilayerPerceptronBoundaryTest extends MultilayerPerceptronTest {

	
	public MultilayerPerceptronBoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new MultilayerPerceptronBoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(MultilayerPerceptronBoundaryTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
