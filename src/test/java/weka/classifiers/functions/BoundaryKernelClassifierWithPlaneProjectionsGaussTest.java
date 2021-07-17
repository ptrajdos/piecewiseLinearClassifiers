package weka.classifiers.functions;

import static org.junit.Assert.*;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;

public class BoundaryKernelClassifierWithPlaneProjectionsGaussTest extends BoundaryKernelClassifierTest {

	public BoundaryKernelClassifierWithPlaneProjectionsGaussTest(String name) {
		super(name);
	}

	
	
	
	@Override
	public Classifier getClassifier() {
		return new BoundaryKernelClassifierWithPlaneProjectionsGauss();
	}

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifierWithPlaneProjectionsGaussTest.class);
	  }
	
	public void testAnotherConstructor() {
		BoundaryKernelClassifierWithPlaneProjectionsGauss obj = new BoundaryKernelClassifierWithPlaneProjectionsGauss(new NearestCentroidBoundary());
	}
	
	
	public static void main(String[] args){
	    junit.textui.TestRunner.run(suite());
}
	

}
