package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.NearestCentroidClassifierTest;
import weka.tools.SerialCopier;

public class NearestCentroidBoundaryTest extends NearestCentroidClassifierTest{

	public NearestCentroidBoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {
		return new NearestCentroidBoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(NearestCentroidBoundaryTest.class);
		  }
	 
	 

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}


	
	

}
