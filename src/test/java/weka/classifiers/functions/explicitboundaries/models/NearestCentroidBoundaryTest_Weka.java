package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.NearestCentroidClassifier;
import weka.classifiers.functions.NearestCentroidClassifierTest;

public class NearestCentroidBoundaryTest_Weka extends NearestCentroidClassifierTest{

	public NearestCentroidBoundaryTest_Weka(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new NearestCentroidClassifier();
	}
	
	 public static Test suite() {
		    return new TestSuite(NearestCentroidClassifierTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}


	
	

}
