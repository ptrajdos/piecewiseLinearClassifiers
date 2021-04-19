package weka.classifiers.functions;


import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;

public class BoundaryKernelClassifierWithPlaneProjectionsNBTest extends BoundaryKernelClassifierTest {

	public BoundaryKernelClassifierWithPlaneProjectionsNBTest(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		return new BoundaryKernelClassifierWithPlaneProjectionsNB();
	}
	
	

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifierWithPlaneProjectionsNBTest.class);
	  }
	

	public static void main(String[] args){
	    junit.textui.TestRunner.run(suite());
	}

}
