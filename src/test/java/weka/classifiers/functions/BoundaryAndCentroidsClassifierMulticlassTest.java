/**
 * 
 */
package weka.classifiers.functions;

import weka.classifiers.AbstractClassifier;
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

/**
 * 
 */
public class BoundaryAndCentroidsClassifierMulticlassTest extends AbstractClassifierTest {

	public BoundaryAndCentroidsClassifierMulticlassTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryAndCentroidsClassifierMulticlass();
	}
	
	public void testOnCondensedData() {
		 AbstractClassifier classifier = (AbstractClassifier) this.getClassifier();
		 classifier.setDoNotCheckCapabilities(true);
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
	
	public void testSerialization() {
		SerializationTester.checkSerialization(this.getClassifier());
	}
	
	public void testGlobalInfoAndTips() {
		WekaGOEChecker checker = new WekaGOEChecker();
		checker.setObject(this.getClassifier());
		assertTrue("GlobalInfo call", checker.checkCallGlobalInfo());
		assertTrue("TipTexts call", checker.checkToolTipsCall());
	}


}
