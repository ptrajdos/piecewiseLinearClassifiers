package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Instance;
import weka.tools.SerialCopier;
import weka.tools.data.IRandomDoubleGenerator;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;

public class BoundaryAndCentroidClassifierTest extends AbstractClassifierTest{

	public BoundaryAndCentroidClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryAndCentroidClassifier();
	}
	
	 public static Test suite() {
		    return new TestSuite(BoundaryAndCentroidClassifierTest.class);
		  }
	 
	 public void testSerialCopy() {
		  Classifier cla = this.getClassifier();
		  try {
			Classifier copy = (Classifier) SerialCopier.makeCopy(cla);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Copy by serialization has failed.");
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
