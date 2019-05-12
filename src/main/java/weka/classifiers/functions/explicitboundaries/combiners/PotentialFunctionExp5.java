package weka.classifiers.functions.explicitboundaries.combiners;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Option;
import weka.core.UtilsPT;

public class PotentialFunctionExp5 extends APotentialFunctionSingleParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2154236635713418568L;
	
	double beta = 2.0;


	@Override
	public double getPotentialValue(double x) throws Exception {
		return Math.exp(-this.alpha*Math.pow(x, this.beta));
	}


	/**
	 * @return the beta
	 */
	public double getBeta() {
		return this.beta;
	}


	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	public String betaTipText() {
		return  "Power factor of the potential function";
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.APotentialFunctionSingleParam#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\t The power factor to use "+
		          "(default: 2).\n",
			      "B", 1, "-B"));
	
		newVector.addAll(Collections.list(super.listOptions()));
	    
		return newVector.elements();
	}


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.APotentialFunctionSingleParam#getOptions()
	 */
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		options.add("-B");
		options.add(""+ this.getBeta());
	
		Collections.addAll(options, super.getOptions());
		
		return options.toArray(new String[0]);	    
	}


	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.combiners.APotentialFunctionSingleParam#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		this.setBeta(UtilsPT.parseDoubleOption(options, "B", 2));
		super.setOptions(options);
	}
	
	
	

}
