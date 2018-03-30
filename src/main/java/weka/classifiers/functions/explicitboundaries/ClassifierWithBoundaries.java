package weka.classifiers.functions.explicitboundaries;

import weka.classifiers.Classifier;

public interface ClassifierWithBoundaries extends Classifier {
	
	/**
	 * Returns decision boundaries of the classifier
	 * @return Decision boundaries
	 * @throws Exception 
	 */
	public DecisionBoundary getBoundary() throws Exception;

}
