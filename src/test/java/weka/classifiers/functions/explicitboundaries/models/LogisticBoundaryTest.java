/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LogisticTest;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;
import weka.tools.data.RandomDataGenerator;
import weka.tools.tests.DistributionChecker;
import weka.tools.tests.NoInstancesChecker;


/**
 * @author pawel trajdos
 * @version 2.3.1
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
		 
		 boolean check = BoundaryChecker.checkBoundaries((ClassifierWithBoundaries) this.getClassifier(), dataset);
		 assertTrue("Boundary response check", check);
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
	
	 public void testPlane() {
		 LogisticBoundary fldaB = (LogisticBoundary) this.getClassifier();
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(0);
		 
		 Instances data = gen.generateData();
		 Instance testInst = data.get(0);
		 
		 try {
			fldaB.buildClassifier(data);
			DecisionBoundaryPlane dPlane = (DecisionBoundaryPlane) fldaB.getBoundary();
			Plane plane = dPlane.getDecisionPlane();
			
			double dist2Plane = plane.distanceToPlane(testInst);
			
			assertTrue("Distance finite", Double.isFinite(dist2Plane));
			assertTrue("Distance greater than zero", dist2Plane>0 );
			
			double side = plane.sideOfThePlane(testInst);
			assertTrue("Distance finite", Double.isFinite(side));
			
			Instances projectedInstances = plane.projectOnPlane(data);
			InstancesTools.checkCompatibility(data, projectedInstances.get(0));
			
			Instance projectedInstance = plane.projectOnPlane(testInst);
			InstancesTools.checkCompatibility(testInst, projectedInstance);
			
			int origNumAttibs = data.numAttributes();
			Instances planeBasedInst = plane.planeBasedInstances(data);
			assertTrue("Smaller number of attibutes, " ,planeBasedInst.numAttributes()<origNumAttibs);
			
			Instance planeBasedI = plane.planeBasedInstance(testInst);
			assertTrue("Smaller number of attributes, inst, " ,planeBasedI.numAttributes()<origNumAttibs);
			
			
			
			
		} catch (Exception e) {
			fail("An exception has been caught: " + e.toString());
		}
	 }
	
	 public static Test suite() {
		    return new TestSuite(LogisticBoundaryTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
