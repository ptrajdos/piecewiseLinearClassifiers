package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptronTest;

public class MultilayerPerceptronBoundaryTest_Weka extends MultilayerPerceptronTest {

	
	public MultilayerPerceptronBoundaryTest_Weka(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new MultilayerPerceptronBoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(MultilayerPerceptronTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
