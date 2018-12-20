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
public class PotentialCombinerMaxMin implements PotentialCombiner, Serializable {

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
		
		double minPositive = Double.POSITIVE_INFINITY;
		double maxNegative = Double.NEGATIVE_INFINITY;
		double tmpVal =0;
		int posCnt=0;
		int negCnt=0;
		for(int i=0;i<boundNumber;i++) {
			tmpVal = boundariesList.get(i).getValue(inst);
			
			if(tmpVal >0 && tmpVal < minPositive) { 
				minPositive = tmpVal;
				posCnt++;
			}
			
			if(tmpVal<0 && tmpVal>maxNegative) { 
				maxNegative = tmpVal;
				negCnt++;
			}
			
			if(negCnt == 0)
				return idx1;
			
			if(posCnt == 0 )
				return idx2;
			
			
			
		}
		
		return (minPositive + maxNegative)>0? idx1:idx2;
	}

}
