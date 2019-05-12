/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UtilsPT;

/**
 * Class is a parent class for potential functions with single parametrs
 * @author pawel trajdos
 * @since 2.0.0
 * @version 2.0.0
 * 
 *
 */
public abstract class APotentialFunctionSingleParam implements PotentialFunction, OptionHandler, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1487114064092959806L;
	protected double alpha=1;

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return this.alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public String alphaTipText() {
		return "Alpha parameter for the potential function";
	}

	/* (non-Javadoc)
	 * @see weka.core.OptionHandler#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\t Alpha parameter of the potential function"+
		          "(default: 1).\n",
			      "AL", 1, "-AL"));
		return newVector.elements();
	}

	/* (non-Javadoc)
	 * @see weka.core.OptionHandler#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		this.setAlpha(UtilsPT.parseDoubleOption(options, "AL", 1));
		
	}

	/* (non-Javadoc)
	 * @see weka.core.OptionHandler#getOptions()
	 */
	@Override
	public String[] getOptions() {
    Vector<String> options = new Vector<String>();
		
		options.add("-AL");
		options.add(""+this.alpha);
		
	    
	    return options.toArray(new String[0]);
	}
	
	

}
