package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.core.Instance;
import weka.tools.SerialCopier;
import weka.tools.data.IRandomDoubleGenerator;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;

public class BoundaryAndCentroidClassifier2Test extends AbstractClassifierTest{

	public BoundaryAndCentroidClassifier2Test(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryAndCentroidClassifier2();
	}
	
	 public static Test suite() {
		    return new TestSuite(BoundaryAndCentroidClassifier2Test.class);
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
	 
	 public void testOnNominalConvertedData() {
		 Classifier classifier = this.getClassifier();
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(10);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumNumericAttributes(0);
		 gen.setNumClasses(2);
		 gen.setMaxNumNominalValues(10);
		 gen.setNumObjects(100);
		 		 
		 Instances dataset = gen.generateData();
		 
		 NominalToBinary nom2Bin = new NominalToBinary();
		 
		 try {
			 nom2Bin.setInputFormat(dataset);
			 dataset = Filter.useFilter(dataset, nom2Bin);
			classifier.buildClassifier(dataset);
			for (Instance instance : dataset) {
				double[] distribution = classifier.distributionForInstance(instance);
				assertTrue("Check distribution", DistributionChecker.checkDistribution(distribution));
			}
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
		 
	 }
	 
	 public void testOnNominalUnaryConvertedData() {
		 Classifier classifier = this.getClassifier();
		 
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(10);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumNumericAttributes(0);
		 gen.setNumClasses(2);
		 gen.setMaxNumNominalValues(1);
		 gen.setNumObjects(100);
		 gen.setAllowUnary(true);
		 		 
		 Instances dataset = gen.generateData();
		 
		 NominalToBinary nom2Bin = new NominalToBinary();
		 
		 try {
			 nom2Bin.setInputFormat(dataset);
			 dataset = Filter.useFilter(dataset, nom2Bin);
			classifier.buildClassifier(dataset);
			for (Instance instance : dataset) {
				double[] distribution = classifier.distributionForInstance(instance);
				assertTrue("Check distribution", DistributionChecker.checkDistribution(distribution));
			}
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
		 
	 }
	

	

}
