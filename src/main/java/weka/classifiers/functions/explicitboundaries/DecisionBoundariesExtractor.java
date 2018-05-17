/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import java.io.Serializable;
import java.util.ArrayList;

import weka.classifiers.Classifier;

/**
 * @author pawel
 *
 */
public class DecisionBoundariesExtractor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6809264307667740321L;

	public DecisionBoundaries extractBoundaries(Classifier[] classArray)throws Exception{
		ArrayList<DecisionBoundary> boundaries = new ArrayList<DecisionBoundary>();
		for(int c=0;c<classArray.length;c++){
			if(classArray[c] instanceof ClassifierWithBoundaries){
				boundaries.add(   ((ClassifierWithBoundaries)classArray[c]).getBoundary()); 
			}else{
				throw new Exception(""+classArray[c] + " is not an instance of " + ClassifierWithBoundaries.class);
			}
		}
		return new DecisionBoundaries(boundaries);
	}

}
