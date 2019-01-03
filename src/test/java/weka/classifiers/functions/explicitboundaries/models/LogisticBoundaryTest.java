/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LogisticTest;


/**
 * @author pawel
 *
 */
public class LogisticBoundaryTest extends LogisticTest {

	public LogisticBoundaryTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {

		return new LogisticBoundary();
	}
	
	 public static Test suite() {
		    return new TestSuite(LogisticBoundaryTest.class);
		  }

	public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

}
