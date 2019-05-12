/**
 * 
 */
package weka.classifiers.functions;

import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;

/**
 * @author pawel trajdos
 * @since 2.0.0
 * @version 2.0.0
 *
 */
public abstract class SingleClassifierEnhancerBoundary extends SingleClassifierEnhancer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4038874210938229962L;
	/**
	 * Reference stores the same classifier as m_Classifier
	 */
	protected ClassifierWithBoundaries boundClassRef;

	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#setClassifier(weka.classifiers.Classifier)
	 */
	@Override
	public void setClassifier(Classifier newClassifier) {
		super.setClassifier(newClassifier);
		/**
		 * If incompatible type is passed, then the Exception is thrown.
		 */
		try {
			this.boundClassRef = (ClassifierWithBoundaries) newClassifier;
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities caps =super.getCapabilities();
		caps.disableAll();
		caps.enable(Capability.NUMERIC_ATTRIBUTES);
		caps.enable(Capability.BINARY_CLASS);

		return caps;
	}
	
	
}
