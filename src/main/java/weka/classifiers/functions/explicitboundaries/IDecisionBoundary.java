package weka.classifiers.functions.explicitboundaries;

import weka.core.Instance;
import weka.core.Instances;

public interface IDecisionBoundary {

	/**
	 * Give the class index for the instance
	 * @param instance
	 * @throws Exception 
	 */
	int getIndex(Instance instance) throws Exception;

	/**
	 * Classify instance
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	double classify(Instance instance) throws Exception;

	/**
	 * Get value of the characteristic function of the decision boundary
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	double getValue(Instance instance) throws Exception;

	/**
	 * Returns the smallest distance between the surface of the decision boundary and the instance
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	double getDistance(Instance instance) throws Exception;

	/**
	 * @return the datasetHeader
	 */
	Instances getDatasetHeader();

	/**
	 * @return the class1Idx
	 */
	int getClass1Idx();

	/**
	 * @return the class2Idx
	 */
	int getClass2Idx();

}