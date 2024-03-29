package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;

public class BoundaryKernelClassifier2Test extends BoundaryKernelClassifierTest {

	public BoundaryKernelClassifier2Test(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		BoundaryKernelClassifier kernClass =new BoundaryKernelClassifier();
		kernClass.setUsePriorProbs(true);
		return kernClass;
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

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifier2Test.class);
	  }

	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}

}
