package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.classifiers.functions.explicitboundaries.models.FLDABoundary;
import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;

public class BoundaryBasedClassifier2Test extends AbstractClassifierTest {

	public BoundaryBasedClassifier2Test(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		BoundaryBasedClassifier bc =new BoundaryBasedClassifier(new FLDABoundary());
		bc.setUseCalibrator(true);
		return bc;
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
	
	public void testMultiClassClassifier() {
		 Classifier classifier = this.getClassifier();
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(0);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumClasses(5);
		 RandomDoubleGenerator doubleGen = new RandomDoubleGeneratorGaussian();
		 doubleGen.setDivisor(1000.0);
		 gen.setDoubleGen(doubleGen );
		 
		 Instances dataset = gen.generateData();
		 
		 MultiClassClassifier mClassifier = new MultiClassClassifier();
		 SelectedTag newMethod = new SelectedTag(MultiClassClassifier.METHOD_1_AGAINST_1, MultiClassClassifier.TAGS_METHOD);
		 mClassifier.setMethod(newMethod );
		 
		 try {
			mClassifier.buildClassifier(dataset);
		} catch (Exception e) {
			fail("An exception has been caught: " + e.getMessage());
		}
		 
	 }

	
	
	 public static Test suite() {
		    return new TestSuite(BoundaryBasedClassifier2Test.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
