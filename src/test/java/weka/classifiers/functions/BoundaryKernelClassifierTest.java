package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.SerializationTester;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;
import weka.tools.tests.WekaGOEChecker;

public class BoundaryKernelClassifierTest extends AbstractClassifierTest {

	public BoundaryKernelClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryKernelClassifier();
	}

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifierTest.class);
	  }
	
	public void testSerialization() {
		SerializationTester.checkSerialization(this.getClassifier());
	}
	
	public void testGlobalInfoAndTips() {
		WekaGOEChecker checker = new WekaGOEChecker();
		checker.setObject(this.getClassifier());
		assertTrue("GlobalInfo call", checker.checkCallGlobalInfo());
		assertTrue("TipTexts call", checker.checkToolTipsCall());
	}
	
	public void testNoInstances() {
		RandomDataGenerator gen = new RandomDataGenerator();
		gen.setNumNominalAttributes(0);
		gen.setNumObjects(0);
		
		Instances dataset = gen.generateData();
		
		Classifier cla = this.getClassifier();
		  
		gen.setNumObjects(10);
		Instances data2 = gen.generateData();
		Instance testInstance = data2.get(0);
		
		try {
			cla.buildClassifier(dataset);
			
			double[] distribution = cla.distributionForInstance(testInstance);
			assertTrue("Intance test: ", DistributionChecker.checkDistribution(distribution));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("An Exception has been caught: " + e.getLocalizedMessage());
		}
	}
	
	public void testOnCondensedData() {
		 Classifier classifier = this.getClassifier();
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(0);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 RandomDoubleGenerator doubleGen = new RandomDoubleGeneratorGaussian();
		 doubleGen.setDivisor(10000.0);
		 gen.setDoubleGen(doubleGen );
		 
		 Instances dataset = gen.generateData();
		 try {
			classifier.buildClassifier(dataset);
			for (Instance instance : dataset) {
				double[] distribution = classifier.distributionForInstance(instance);
				assertTrue("Check distribution", DistributionChecker.checkDistribution(distribution));
			}
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
	 }
	
	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}

}
