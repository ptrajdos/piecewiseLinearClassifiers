package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionCombiner;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionLinear;
import weka.classifiers.functions.explicitboundaries.models.FLDABoundary;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.meta.CustomizableBaggingClassifier;
import weka.classifiers.meta.simpleVotingLikeCombiners.BoundaryCombiner;
import weka.classifiers.meta.simpleVotingLikeCombiners.OutputCombiner;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.data.IRandomDoubleGenerator;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.data.WellSeparatedSquares;
import weka.tools.tests.DistributionChecker;

/**
 * General class for testing potential combiners
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 *
 */

public abstract class PotentialCombinerGeneralTest extends TestCase {

	/**
	 * Returns cmobiner to be tested
	 * @return
	 */
	public abstract PotentialCombiner getCombiner();
	
	public void testCombiner() {
		Instances data = this.generateTestData();
		Instance testInstance = data.get(0);
		PotentialFunction pot = new PotentialFunctionLinear();
		PotentialCombiner comb = this.getCombiner();
		try {
			for(int i=1;i<10;i++) {
				
				DecisionBoundaries bounds = new DecisionBoundaries(getBoundariesList(i));
				int idx =  comb.getCombinedBoundaries(testInstance, bounds, pot);
				assertTrue("Index", idx==0 |idx==1);
				
				idx =  comb.getCombinedBoundaries(data.get(i), bounds, pot);
				assertTrue("Index", idx==0 |idx==1);
			}
			
		} catch (Exception e) {
			fail("An exception has been caught: " + e.toString());
		}
		
	}
	
	public void testNoBounds() {
		Instances data = this.generateTestData();
		Instance testInstance = data.get(0);
		PotentialFunction pot = new PotentialFunctionLinear();
		PotentialCombiner comb = this.getCombiner();
		try {
		
				
			DecisionBoundaries bounds = new DecisionBoundaries(getBoundariesList(0));
			int idx =  comb.getCombinedBoundaries(testInstance, bounds, pot);
			fail("No exception has been raised for empty boundaries");
		
			
		} catch (Exception e) {
			//It should throw an exception
		}
	}
	
	//TODO add test generated for real classifiers
	
	public Instances generateTestData() {
		RandomDataGenerator gen = new RandomDataGenerator();
		gen.setNumNominalAttributes(0);
		gen.setNumStringAttributes(0);
		gen.setNumDateAttributes(0);
		gen.setNumObjects(100);
		RandomDoubleGenerator doubleGen = new RandomDoubleGeneratorGaussian();
		doubleGen.setDivisor(0.1);
		gen.setDoubleGen(doubleGen);
		
		return gen.generateData();
	}
	
	List<DecisionBoundary> getBoundariesList(int numBounds) throws Exception{
		ArrayList<DecisionBoundary> bndList = new ArrayList<DecisionBoundary>();
		for(int i=0;i<numBounds;i++) {
			bndList.add(new DecisionBoundaryPlane(generateTestData(), 0,1));
		}
		return bndList;
	}
	
	public void testRealClassifier() {
		Instances testData = this.generateTestData();
		
		CustomizableBaggingClassifier custBag = new CustomizableBaggingClassifier();
		custBag.setClassifier(new NearestCentroidBoundary());
		BoundaryCombiner outCombiner =  new BoundaryCombiner();
		PotentialFunctionCombiner boundaryCombiner = new PotentialFunctionCombiner();
		boundaryCombiner.setPotCombiner(getCombiner());
		boundaryCombiner.setPotential(new PotentialFunctionLinear());
		outCombiner.setBoundaryCombiner(boundaryCombiner);
		
		custBag.setOutCombiner(outCombiner );
		custBag.setBagSizePercent(80);
		
		try {
			custBag.buildClassifier(testData);
			for (Instance instance : testData) {
				double[] distribution = custBag.distributionForInstance(instance);
				assertTrue("Correct distribution", DistributionChecker.checkDistribution(distribution));
			}
		} catch (Exception e) {
			fail("An exception has been caught: " + e.getMessage());
		}
	}
	
	public void testWellSeparated() {
		WellSeparatedSquares gen = new WellSeparatedSquares();
		Instances testData = gen.generateData();
		
		CustomizableBaggingClassifier custBag = new CustomizableBaggingClassifier();
		custBag.setClassifier(new FLDABoundary());
		BoundaryCombiner outCombiner =  new BoundaryCombiner();
		PotentialFunctionCombiner boundaryCombiner = new PotentialFunctionCombiner();
		boundaryCombiner.setPotCombiner(getCombiner());
		boundaryCombiner.setPotential(new PotentialFunctionLinear());
		outCombiner.setBoundaryCombiner(boundaryCombiner);
		custBag.setOutCombiner(outCombiner );
		custBag.setBagSizePercent(80);
		
		try {
			custBag.buildClassifier(testData);
			for (Instance instance : testData) {
				double[] distribution = custBag.distributionForInstance(instance);
				assertTrue("Correct distribution", DistributionChecker.checkDistribution(distribution));
			}
		} catch (Exception e) {
			fail("An exception has been caught: " + e.getMessage());
		}
	}
	
	

}
