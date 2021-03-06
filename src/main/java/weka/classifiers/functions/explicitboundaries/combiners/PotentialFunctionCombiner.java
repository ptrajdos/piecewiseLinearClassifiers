/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner;
import weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners.PotentialCombiner;
import weka.classifiers.functions.explicitboundaries.combiners.potentialCombiners.PotentialCombinerSum;
import weka.core.Instance;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UtilsPT;

/**
 * @author pawel trajdos
 * @since 1.1.1
 * @version 2.1.3
 *
 */
public class PotentialFunctionCombiner implements DecisionBoundaryCombiner, Serializable,OptionHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2573402040603897535L;
	/**
	 * Decision boundaries
	 */
	protected PotentialFunction potential;
	
	protected DecisionBoundaries boundaries = null;
	
	protected PotentialCombiner potCombiner =  null;
	
	
	

	/**
	 * 
	 */
	public PotentialFunctionCombiner() {
		this.potential = new PotentialFunctionSign();
		this.potCombiner = new PotentialCombinerSum();
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner#getDecision(weka.core.Instance)
	 */
	@Override
	public int getDecision(Instance inst) throws Exception {
		//value >0 -> index1
		if(this.boundaries == null)
			throw new Exception("No boundaries have been set");
		
		int classIdx;
		classIdx = this.potCombiner.getCombinedBoundaries(inst, this.boundaries, this.potential);
		
		return classIdx;
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

	@Override
	public double getClass(Instance inst) throws Exception {
		int index = this.getDecision(inst);
		return index;
	}
	
	

	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		 newVector.addElement(new Option(
			      "\t Potenitial function to use "+
		          "(default:"+PotentialFunctionSign.class.toGenericString()  +" ).\n",
			      "PF", 0, "-PF"));
		 
		 newVector.addElement(new Option(
			      "\t Potenitial combiner to use "+
		          "(default:"+PotentialCombinerSum.class.toGenericString()  +" ).\n",
			      "PC", 0, "-PC"));
		    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		
		this.setPotential((PotentialFunction) UtilsPT.parseObjectOptions(options, "PF", new PotentialFunctionSign(), PotentialFunction.class));
		
		this.setPotCombiner((PotentialCombiner) UtilsPT.parseObjectOptions(options, "PC", new PotentialCombinerSum(), PotentialCombiner.class));
		
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
	    options.add("-PF");
	    options.add(UtilsPT.getClassAndOptions(this.getPotential()));
	    
	    options.add("-PC");
	    options.add(UtilsPT.getClassAndOptions(this.getPotCombiner()));
	   
	    
	    return options.toArray(new String[0]);
	}

	/**
	 * @return the potCombiner
	 */
	public PotentialCombiner getPotCombiner() {
		return this.potCombiner;
	}

	/**
	 * @param potCombiner the potCombiner to set
	 */
	public void setPotCombiner(PotentialCombiner potCombiner) {
		this.potCombiner = potCombiner;
	}
	
	
	

}
