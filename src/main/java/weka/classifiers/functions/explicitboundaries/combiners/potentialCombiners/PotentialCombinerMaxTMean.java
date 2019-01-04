/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.core.Instance;
import weka.core.Statistics;
import weka.core.Utils;
import weka.core.UtilsPT;

/**
 * @author Pawel Trajdos
 * @version 1.2.0
 * Combines the boundaries-specific outputs using max-max rule
 *
 */
public class PotentialCombinerMaxTMean implements PotentialCombiner, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7995156761904210074L;


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners.PotentialCombiner#getCombinedBoundaries(weka.core.Instance, weka.classifiers.functions.explicitboundaries.DecisionBoundaries)
	 */
	@Override
	public int getCombinedBoundaries(Instance inst, DecisionBoundaries boundaries, PotentialFunction potFunction) throws Exception {
		List<DecisionBoundary> boundariesList = boundaries.getBoundaries();
		int boundNumber = boundariesList.size();
		if(boundNumber == 0)
			throw new Exception("No boundaries available");
		
		int idx1 = boundariesList.get(0).getClass1Idx();
		int idx2 = boundariesList.get(0).getClass2Idx();
		
		LinkedList<Double> negatives = new LinkedList<Double>();
		LinkedList<Double> positives = new LinkedList<Double>();
		double tmpVal;
		
		for(int i=0;i<boundNumber;i++) {
			tmpVal = potFunction.getPotentialValue( boundariesList.get(i).getValue(inst));
			if(tmpVal <0)
				negatives.add(tmpVal);
			
			if(tmpVal>0)
				positives.add(tmpVal);
			
		}
		if(negatives.size() == 0)
			return idx1;
		
		if(positives.size() == 0)
			return idx2;
		
		int negS = negatives.size();
		double[] negativeArray = new double[negS];
		for(int i=0;i<negS;i++) {
			negativeArray[i]=negatives.get(i);
		}
		
		int posS  = positives.size();
		double[] positiveArray = new double[posS];
		for(int i=0;i<posS;i++) {
			positiveArray[i] = positives.get(i);
		}
		
		double negTMean  = UtilsPT.truncatedMean(negativeArray);
		double posTMean = UtilsPT.truncatedMean(positiveArray);
		
	
	
		return (posTMean - negTMean)>0? idx1:idx2;
	}

}
