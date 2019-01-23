/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import java.io.Serializable;
import java.util.List;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunction;
import weka.core.Instance;
import weka.core.UtilsPT;

/**
 * @author Pawel Trajdos
 * @version 1.4.0
 * Combines the boundaries-specific outputs using Truncated mean rule
 *
 */
public class PotentialCombinerTMean implements PotentialCombiner, Serializable {

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
		
	
		double[] tmpVals = new double[boundNumber];
		
		for(int i=0;i<boundNumber;i++) {
			tmpVals[i] = potFunction.getPotentialValue( boundariesList.get(i).getValue(inst));	
		}
		double result = UtilsPT.truncatedMean(tmpVals);
	
	
		return (result)>0? idx1:idx2;
	}

}
