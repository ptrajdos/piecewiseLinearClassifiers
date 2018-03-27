package weka.classifiers.functions;

import java.util.List;

public interface ClassifierWithBoundaries {
	
	/**
	 * Returns decision boundaries of the classifier
	 * @return Decision boundaries
	 */
	public DecisionBoundaries getBoundaries();

}
