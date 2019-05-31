package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.AbstractClassifierTest;
import weka.classifiers.Classifier;
import weka.tools.SerialCopier;

public class BoundaryAndCentroidClassifierTest extends AbstractClassifierTest{

	public BoundaryAndCentroidClassifierTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryAndCentroidClassifier();
	}
	
	 public static Test suite() {
		    return new TestSuite(BoundaryAndCentroidClassifierTest.class);
		  }
	 
	 public void testSerialCopy() {
		  Classifier cla = this.getClassifier();
		  try {
			Classifier copy = (Classifier) SerialCopier.makeCopy(cla);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Copy by serialization has failed.");
		}
	  }
	 
	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}
	

	

}
