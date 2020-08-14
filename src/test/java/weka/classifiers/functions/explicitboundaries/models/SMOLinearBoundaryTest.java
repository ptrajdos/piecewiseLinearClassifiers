package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOTest;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;
import weka.tools.tests.NoInstancesChecker;

public class SMOLinearBoundaryTest extends SMOTest {

	public SMOLinearBoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new SMOLinearBoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(SMOLinearBoundaryTest.class);
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
