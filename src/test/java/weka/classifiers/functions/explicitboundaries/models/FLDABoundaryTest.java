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
public class FLDABoundaryTest extends FLDATest {

	public FLDABoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new FLDABoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(FLDABoundaryTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
