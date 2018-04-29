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
import weka.core.Instance;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * @author pawel
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
	protected PotentialFunction potential = new PotentialFunctionSign();
	
	protected DecisionBoundaries boundaries = null;
	
	

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
		//value >0 -> index1
		if(this.boundaries == null)
			throw new Exception("No boundaries have been set");
		
			
		double combinedValue =0;
		List<DecisionBoundary> boundariesList = this.boundaries.getBoundaries();
		int numBoundaries = boundariesList.size();
		if(numBoundaries <= 0 )
			throw new Exception("Set contains no boundaries");
		
		int idx1 = boundariesList.get(0).getClass1Idx();
		int idx2 = boundariesList.get(0).getClass2Idx();
		
		double signDist = 0;
		for(int i=0;i<numBoundaries;i++){
			signDist = boundariesList.get(i).getValue(inst);
			combinedValue += this.potential.getPotentialValue(signDist);
		}
		combinedValue/=numBoundaries;
		
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
		    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		String potentialString = Utils.getOption("BC", options);
	    if(potentialString.length() != 0) {
	      String combinerClassSpec[] = Utils.splitOptions(potentialString);
	      if(combinerClassSpec.length == 0) { 
	        throw new Exception("Invalid Class combiner."); 
	      }
	      String className = combinerClassSpec[0];
	      combinerClassSpec[0] = "";

	      this.setPotential((PotentialFunction) 
	                    Utils.forName( PotentialFunction.class, 
	                                 className, 
	                                 combinerClassSpec)
	                                        );
	    }
	    else 
	      this.setPotential(new PotentialFunctionSign()); 
		
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
	    options.add("-PF");
	    String combinerOptions = (this.potential instanceof OptionHandler)? Utils.joinOptions(((OptionHandler)this.potential).getOptions()):"";
	    options.add(this.potential.getClass().getName()+" "+combinerOptions); 
	    
	    return options.toArray(new String[0]);
	}
	

}