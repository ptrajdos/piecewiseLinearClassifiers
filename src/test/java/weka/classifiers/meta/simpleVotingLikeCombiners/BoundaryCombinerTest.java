package weka.classifiers.meta.simpleVotingLikeCombiners;

import weka.classifiers.Classifier;
import weka.classifiers.IteratedSingleClassifierEnhancer;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandlersTest.OptionHandlerTest;
import weka.core.RevisionHandler;
import weka.core.Utils;
import weka.tools.data.RandomDataGenerator;
import weka.tools.tests.WekaGOEChecker;

public class BoundaryCombinerTest extends OptionHandlerTest {

	public BoundaryCombinerTest(String name, String classname) {
		super(name, classname);
	}

	public BoundaryCombinerTest(String name) {
		super(name, BoundaryCombiner.class.getCanonicalName());
	}
	
	public void testTipTexts() {
		WekaGOEChecker checker = new WekaGOEChecker();
		checker.setObject(this.getOptionHandler());
		checker.checkCallGlobalInfo();
		checker.checkToolTipsCall();
	}
	public void testRevision() {
		try {
			RevisionHandler revH = (RevisionHandler) this.getOptionHandler();
			String revision = revH.getRevision();
			assertTrue("Not null", revision!=null);
			assertTrue("Non zero string length", revision.length()>0);
		}catch(Exception e) {
			fail("Revision handler check, exception: " + e.toString());
		}
	}
	
	public void testIteratedClass() {
		IteratedSingleClassifierEnhancer iterated = new Bagging();
		iterated.setClassifier(new NearestCentroidBoundary());
		
		RandomDataGenerator datGen = new RandomDataGenerator();
		datGen.setNumNominalAttributes(0);
		Instances data = datGen.generateData();
		
		BoundaryCombiner comb = (BoundaryCombiner) this.getOptionHandler();
		
		try {
			iterated.buildClassifier(data);
			Instance testInstance = data.get(0);
			
			double classVal = comb.getClass(iterated, testInstance);
			int numClasses = data.numClasses();
			assertTrue("Class Val range", classVal<  numClasses & classVal>=0);
			double[] distribution = comb.getDistributionForInstance(iterated, testInstance);
			assertTrue("Distribution length",distribution.length == numClasses);
			assertTrue("Distribution properties", this.validateDistribution(distribution));
			
			
		} catch (Exception e) {
			fail("Exception caught: "+ e.toString());
		}
	}
	
	public void testIteratedFail() {
		IteratedSingleClassifierEnhancer iterated = new Bagging();
		iterated.setClassifier(new J48());
		
		RandomDataGenerator datGen = new RandomDataGenerator();
		datGen.setNumNominalAttributes(0);
		Instances data = datGen.generateData();
		
		BoundaryCombiner comb = (BoundaryCombiner) this.getOptionHandler();
		
		try {
			iterated.buildClassifier(data);
			Instance testInstance = data.get(0);
			
			double classVal = comb.getClass(iterated, testInstance);
			fail("No exception has been caught. Invalid classifier");
			
		} catch (Exception e) {
			//
		}
	}
	
	public void testMultipleFail() {
		MultipleClassifiersCombiner multiple = new Vote();
		multiple.setClassifiers(new Classifier[] {new J48(),new J48()});
		
		
		RandomDataGenerator datGen = new RandomDataGenerator();
		datGen.setNumNominalAttributes(0);
		Instances data = datGen.generateData();
		
		BoundaryCombiner comb = (BoundaryCombiner) this.getOptionHandler();
		
		try {
			multiple.buildClassifier(data);
			Instance testInstance = data.get(0);
			
			double classVal = comb.getClass(multiple, testInstance);
			fail("No exception has been caught. Invalid classifier");
			
		} catch (Exception e) {
			//
		}
	}
	
	
	public void testMultiple() {
		MultipleClassifiersCombiner multiple = new Vote();
		multiple.setClassifiers(new Classifier[] {new NearestCentroidBoundary(),new NearestCentroidBoundary()});
		
		
		RandomDataGenerator datGen = new RandomDataGenerator();
		datGen.setNumNominalAttributes(0);
		Instances data = datGen.generateData();
		
		BoundaryCombiner comb = (BoundaryCombiner) this.getOptionHandler();
		
		try {
			multiple.buildClassifier(data);
			Instance testInstance = data.get(0);
			
			double classVal = comb.getClass(multiple, testInstance);
			int numClasses = data.numClasses();
			assertTrue("Class Val range", classVal<  numClasses & classVal>=0);
			double[] distribution = comb.getDistributionForInstance(multiple, testInstance);
			assertTrue("Distribution length",distribution.length == numClasses);
			assertTrue("Distribution properties", this.validateDistribution(distribution));
			
			
			
			
		} catch (Exception e) {
			fail("Exception caught: "+ e.toString());
		}
	}
	
	
	
	public boolean validateDistribution(double[] distrib) {
		double sum=0;
		for(int i=0;i<distrib.length;i++) {
			sum+=distrib[i];
			if(distrib[i] >1 | distrib[i]<0)
				return false;
		}
		
		if(!Utils.eq(sum, 1.0))
			return false;
		
		return true;
	}
	
	

}
