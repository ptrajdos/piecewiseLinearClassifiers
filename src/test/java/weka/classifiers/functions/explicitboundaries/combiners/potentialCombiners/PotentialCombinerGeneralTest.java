package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionLinear;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;

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
		
		return gen.generateData();
	}
	
	List<DecisionBoundary> getBoundariesList(int numBounds) throws Exception{
		ArrayList<DecisionBoundary> bndList = new ArrayList<DecisionBoundary>();
		for(int i=0;i<numBounds;i++) {
			bndList.add(new DecisionBoundaryPlane(generateTestData(), 0,1));
		}
		return bndList;
	}

}
