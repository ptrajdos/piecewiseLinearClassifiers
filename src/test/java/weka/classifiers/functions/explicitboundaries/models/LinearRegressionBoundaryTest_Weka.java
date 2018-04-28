package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegressionTest;

public class LinearRegressionBoundaryTest_Weka extends LinearRegressionTest{

	public LinearRegressionBoundaryTest_Weka(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new FLDABoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(LinearRegressionBoundaryTest_Weka.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	
	


}
