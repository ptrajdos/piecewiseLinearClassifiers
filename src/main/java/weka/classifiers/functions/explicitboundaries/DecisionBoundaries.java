/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author pawel
 *
 */
public class DecisionBoundaries implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4967310316434866060L;
	
	protected List<DecisionBoundary> boundaries = null;

	/**
	 * 
	 */
	public DecisionBoundaries(List<DecisionBoundary> boundaries) {
		this.boundaries = boundaries;
	}

	/**
	 * @return the boundaries
	 */
	public List<DecisionBoundary> getBoundaries() {
		return Collections.unmodifiableList(this.boundaries) ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DecisionBoundaries [boundaries=" + this.boundaries + "]";
	}
	
	
	
	

}
