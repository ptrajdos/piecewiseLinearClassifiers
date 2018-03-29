package weka.classifiers.functions.explicitboundaries;

import weka.classifiers.Classifier;

public interface ClassifierWithBoundaries extends Classifier {
	
	/**
	 * Returns decision boundaries of the classifier
	 * @return Decision boundaries
	 */
	public DecisionBoundaries getBoundaries();

}
