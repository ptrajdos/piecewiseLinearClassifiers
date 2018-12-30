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

/**
 * @author pawel trajdos
 * @version 1.2.0
 * Combines the boundaries-specific outputs using max-sum rule
 *
 */
public class PotentialCombinerSum implements PotentialCombiner, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5032036774446628203L;

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners.PotentialCombiner#getCombinedBoundaries(weka.core.Instance, weka.classifiers.functions.explicitboundaries.DecisionBoundaries)
	 */
	@Override
	public int getCombinedBoundaries(Instance inst, DecisionBoundaries boundaries, PotentialFunction potFunction) throws Exception {
		List<DecisionBoundary> boundariesList = boundaries.getBoundaries();
		int boundNumber = boundariesList.size();
		if(boundNumber == 0)
			throw new Exception("No boundaries available");
		
		double tmpVal=0;
		double sum=0;
		for(int i=0;i<boundNumber;i++) {
			tmpVal = potFunction.getPotentialValue( boundariesList.get(i).getValue(inst));
			sum+=tmpVal;
		}
		int idx1 = boundariesList.get(0).getClass1Idx();
		int idx2 = boundariesList.get(0).getClass2Idx();
		
		return sum>0?idx1:idx2;
	}

}
