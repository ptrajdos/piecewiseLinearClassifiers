/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import java.io.Serializable;
import java.util.List;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.core.Instance;

/**
 * @author Pawel Trajdos
 * @version 1.1.0
 * Combines the boundaries-specific outputs using max-max rule
 *
 */
public class PotentialCombinerMaxMax implements PotentialCombiner, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7995156761904210074L;


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners.PotentialCombiner#getCombinedBoundaries(weka.core.Instance, weka.classifiers.functions.explicitboundaries.DecisionBoundaries)
	 */
	@Override
	public int getCombinedBoundaries(Instance inst, DecisionBoundaries boundaries) throws Exception {
		List<DecisionBoundary> boundariesList = boundaries.getBoundaries();
		int boundNumber = boundariesList.size();
		if(boundNumber == 0)
			throw new Exception("No boundaries available");
		
		int idx1 = boundariesList.get(0).getClass1Idx();
		int idx2 = boundariesList.get(0).getClass2Idx();
		
		
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
		int posCnt=0;
		int negCnt=0;
		double tmpVal =0;
		for(int i=0;i<boundNumber;i++) {
			tmpVal = boundariesList.get(i).getValue(inst);
			if( tmpVal >0 && tmpVal >max) {
				max = tmpVal;
				posCnt++;
			}
			
			if(tmpVal <0 && tmpVal <min) {
				min = tmpVal;
				negCnt++;
			}
			
		}
		
		if(posCnt == 0)
			return idx2;
		
		if(negCnt == 0)
			return idx1;
		
		return (max+min)>0? idx1:idx2;

	}

}
