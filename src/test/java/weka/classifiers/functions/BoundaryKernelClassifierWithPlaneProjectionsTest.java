package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;

public class BoundaryKernelClassifierWithPlaneProjectionsTest extends BoundaryKernelClassifierTest {

	public BoundaryKernelClassifierWithPlaneProjectionsTest(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {
		return new BoundaryKernelClassifierWithPlaneProjections();
	}
	
	public void testParamConstructor() {
		BoundaryKernelClassifierWithPlaneProjections classifier = new BoundaryKernelClassifierWithPlaneProjections(new NearestCentroidBoundary());
	}

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifierWithPlaneProjectionsTest.class);
	  }
	

	public static void main(String[] args){
	    junit.textui.TestRunner.run(suite());
	}

}
