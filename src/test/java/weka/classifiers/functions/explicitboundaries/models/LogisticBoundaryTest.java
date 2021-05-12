/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LogisticTest;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;
import weka.tools.tests.DistributionChecker;
import weka.tools.tests.NoInstancesChecker;


/**
 * @author pawel
 *
 */
public class LogisticBoundaryTest extends LogisticTest {

	public LogisticBoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new LogisticBoundary();
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
	
	public void testOneAttribute() {
		 Classifier classifier = this.getClassifier();
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumClasses(2);
		 gen.setNumNominalAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumStringAttributes(0);
		 gen.setNumNumericAttributes(1);
		 Instances data = gen.generateData();
		 Instance  testInstance = data.get(0);
		 
		 try {
			classifier.buildClassifier(data);
			double[] distribution = classifier.distributionForInstance(testInstance);
			assertTrue("Checking distribution, one attribute",DistributionChecker.checkDistribution(distribution));
		} catch (Exception e) {
			fail("An exception has been caught" + e.getMessage());
		}
		 
	 }
	
	 public static Test suite() {
		    return new TestSuite(LogisticBoundaryTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
