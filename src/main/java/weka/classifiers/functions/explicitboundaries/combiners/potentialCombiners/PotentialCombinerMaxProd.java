/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners;

import java.io.Serializable;
import java.util.List;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.core.Instance;
import weka.core.Utils;

/**
 * @author Pawel Trajdos
 * @version 1.1.0
 * Combines the boundaries-specific outputs using max-max rule
 *
 */
public class PotentialCombinerMaxProd implements PotentialCombiner, Serializable {

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
		
		double posProduct=1;
		double negProduct=1;
		int posCnt=0;
		int negCnt=0;
		double tmpVal;
		
		for(int i=0;i<boundNumber;i++) {
			tmpVal = boundariesList.get(i).getValue(inst);
			
			if(Utils.gr(tmpVal, 0)) {
				posProduct*=tmpVal;
				posCnt++;
			}
			
			if(Utils.gr(0,tmpVal)) {
				negProduct*=-tmpVal;
				negCnt++;
			}
			
		}
		
		if(posCnt ==0 )
			return idx2;
		
		if(negCnt == 0 )
			return idx1;
		
		posProduct = Math.pow(posProduct, 1.0/posCnt);
		negProduct = Math.pow(negProduct,1.0/negCnt);
		
		
		
		return (posProduct - negProduct)>=0? idx1:idx2;

	}

}
