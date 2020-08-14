package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.NearestCentroidClassifierTest;
import weka.classifiers.functions.explicitboundaries.gemoetry.DotProduct;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;
import weka.tools.tests.NoInstancesChecker;

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
	 
	 public void testDotProduct() {
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(0);
		 gen.setAllowUnary(false);
		 Instances data = gen.generateData();
		 
		 NearestCentroidBoundary classifier = (NearestCentroidBoundary) this.getClassifier();
		 
		 try {
			classifier.buildClassifier(data);
			DotProduct dp = classifier.getDotProduct();
			classifier.setDotProduct(dp);
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception has been caught");
		}
	 }
	 
	 public void testNoInstances() {
			Classifier classifier = this.getClassifier();
			int[] numInstances2Check= {0,1,2,3};
			RandomDataGenerator gen = new RandomDataGenerator();
			gen.setNumNominalAttributes(0);
			Instances data = gen.generateData();
			try {
				NoInstancesChecker.performCheck(data, classifier, numInstances2Check, 0);
			} catch (Exception e) {
				fail("An exception has been caught: " + e.getLocalizedMessage());
			}
		}
	 
	

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}


	
	

}
