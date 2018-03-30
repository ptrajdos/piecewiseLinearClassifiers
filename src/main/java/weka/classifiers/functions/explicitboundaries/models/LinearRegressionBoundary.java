/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;

/**
 * @author pawel
 *
 */
public class LinearRegressionBoundary extends LinearRegression implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 172220668813926018L;

	/**
	 * 
	 */
	public LinearRegressionBoundary() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities base = super.getCapabilities();
		base.disable(Capability.NOMINAL_CLASS);
		base.enable(Capability.BINARY_CLASS);
		return base;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new LinearRegressionBoundary(), args);
	}

}
