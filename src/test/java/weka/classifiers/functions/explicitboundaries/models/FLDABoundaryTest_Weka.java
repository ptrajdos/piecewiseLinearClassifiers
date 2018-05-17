/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.FLDATest;


/**
 * @author pawel
 *
 */
public class FLDABoundaryTest_Weka extends FLDATest {

	public FLDABoundaryTest_Weka(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new FLDABoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(FLDABoundaryTest_Weka.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
