package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.Classifier;

public class BoundaryKernelClassifier2Test extends BoundaryKernelClassifierTest {

	public BoundaryKernelClassifier2Test(String name) {
		super(name);
	}

	@Override
	public Classifier getClassifier() {
		BoundaryKernelClassifier kernClass =new BoundaryKernelClassifier();
		kernClass.setUsePriorProbs(true);
		return kernClass;
	}

	public static Test suite() {
	    return new TestSuite(BoundaryKernelClassifier2Test.class);
	  }

	 public static void main(String[] args){
		    junit.textui.TestRunner.run(suite());
	}

}
