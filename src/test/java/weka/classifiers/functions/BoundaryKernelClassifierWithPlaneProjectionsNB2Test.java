package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;
import weka.classifiers.functions.explicitboundaries.models.FLDABoundary;

public class BoundaryKernelClassifierWithPlaneProjectionsNB2Test extends BoundaryKernelClassifierTest {


	public BoundaryKernelClassifierWithPlaneProjectionsNB2Test(String name) {
		super(name);
	}
	
	@Override
	public Classifier getClassifier() {
		BoundaryKernelClassifierWithPlaneProjectionsNB2 nb = new BoundaryKernelClassifierWithPlaneProjectionsNB2(new FLDABoundary());
		nb.setUseKernel(true);
		return nb;
	}
	
	public void testUseKernel() {
		BoundaryKernelClassifierWithPlaneProjectionsNB2 nb = (BoundaryKernelClassifierWithPlaneProjectionsNB2) this.getClassifier();
		assertTrue("Use kernel", nb.isUseKernel());
		nb.getOptions();
		nb.setUseKernel(false);
		assertFalse("Use kernel", nb.isUseKernel());
	}

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifierWithPlaneProjectionsNB2Test.class);
	 }

	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}

}
