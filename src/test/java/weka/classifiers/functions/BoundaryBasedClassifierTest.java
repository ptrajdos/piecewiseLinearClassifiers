package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;
import weka.tools.tests.WekaGOEChecker;

public class BoundaryBasedClassifierTest extends AbstractClassifierTest {

	public BoundaryBasedClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryBasedClassifier();
	}

	
	
	 public static Test suite() {
		    return new TestSuite(BoundaryBasedClassifierTest.class);
		  }
	 
	 public void testForName() {
		 BoundaryBasedClassifier cla = new BoundaryBasedClassifier();
		 String[] options = cla.getOptions();
		 try {
			Classifier x = AbstractClassifier.forName("weka.classifiers.functions.BoundaryBasedClassifier", options);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Classifier forName Exception has been caught");
		}
	 }
	 
	 public void testTipTexts() {
		 WekaGOEChecker goe = new WekaGOEChecker();
		 goe.setObject(this.getClassifier());
		 goe.checkCallGlobalInfo();
		 goe.checkToolTipsCall();
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
