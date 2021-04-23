package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptronTest;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;
import weka.tools.tests.NoInstancesChecker;

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
	 
	 public void testBoundaryResponse() {
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumClasses(2);
		 gen.setNumNominalAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumStringAttributes(0);
		 
		 Instances dataset = gen.generateData();
		 BoundaryChecker.checkBoundaries((ClassifierWithBoundaries) this.getClassifier(), dataset);
	 }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
