/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;
import java.util.List;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner;
import weka.core.Instance;

/**
 * @author pawel
 *
 */
public class PotentialFunctionCombiner implements DecisionBoundaryCombiner, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2573402040603897535L;
	/**
	 * Decision boundaries
	 */
	protected PotentialFunction potential = new PotentialFunctionSign();
	
	protected DecisionBoundaries boundaries;
	
	

	/**
	 * 
	 */
	public PotentialFunctionCombiner() {
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner#getDecision(weka.core.Instance)
	 */
	@Override
	public int getDecision(Instance inst) throws Exception {
		// TODO Auto-generated method stub
		//value >0 -> index1
		double combinedValue =0;
		List<DecisionBoundary> boundariesList = this.boundaries.getBoundaries();
		int numBoundaries = boundariesList.size();
		int idx1 = boundariesList.get(0).getClass1Idx();
		int idx2 = boundariesList.get(0).getClass2Idx();
		
		double signDist = 0;
		for(int i=0;i<numBoundaries;i++){
			signDist = boundariesList.get(i).getValue(inst);
			combinedValue += this.potential.getPotentialValue(signDist);
		}
		
		return combinedValue>0? idx1:idx2;
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner#setBoundaries(weka.classifiers.functions.explicitboundaries.DecisionBoundaries)
	 */
	@Override
	public void setBoundaries(DecisionBoundaries boundaries) throws Exception {
		List<DecisionBoundary> boundariesList = boundaries.getBoundaries();
		int idx1 = boundariesList.get(0).getClass1Idx();
		int idx2 = boundariesList.get(0).getClass2Idx();
		int listLen = boundariesList.size();
		for(int i=0;i<listLen;i++){
			if(idx1 != boundariesList.get(i).getClass1Idx())
				throw new Exception("Incompatible boundaries");
			
			if(idx2 != boundariesList.get(i).getClass2Idx())
				throw new Exception("Incompatible boundaries");
		}
		
		this.boundaries = boundaries;

	}

	/**
	 * @return the potential
	 */
	public PotentialFunction getPotential() {
		return this.potential;
	}

	/**
	 * @param potential the potential to set
	 */
	public void setPotential(PotentialFunction potential) {
		this.potential = potential;
	}
	

}
