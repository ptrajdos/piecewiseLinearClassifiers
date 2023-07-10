package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Instance;
import weka.tools.SerialCopier;
import weka.tools.data.IRandomDoubleGenerator;
import weka.tools.data.InstancesOperator;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;
import weka.tools.tests.WekaGOEChecker;

public class BoundaryAndCentroidsClassifierTest extends AbstractClassifierTest{

	public BoundaryAndCentroidsClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryAndCentroidsClassifier();
	}
	
	 public static Test suite() {
		    return new TestSuite(BoundaryAndCentroidsClassifierTest.class);
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
	 
	 public void testOnSingleClass() {
		 
		 Classifier classifier = this.getClassifier();
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(0);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumClasses(2);
		 
		 Instances dataset = gen.generateData();
		 
		 try {
			 Instances[] splittedData = InstancesOperator.classSpecSplit(dataset);
			classifier.buildClassifier(splittedData[0]);
			for (Instance instance : dataset) {
				double[] distribution = classifier.distributionForInstance(instance);
				assertTrue("Check distribution", DistributionChecker.checkDistribution(distribution));
			}
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
		 
		 
	 }
	 
	 public void testGlobalInfoAndTips() {
			WekaGOEChecker checker = new WekaGOEChecker();
			checker.setObject(this.getClassifier());
			assertTrue("GlobalInfo call", checker.checkCallGlobalInfo());
			assertTrue("TipTexts call", checker.checkToolTipsCall());
		}
	 
	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

	

}
