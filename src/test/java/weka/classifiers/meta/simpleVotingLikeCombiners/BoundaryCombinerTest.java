package weka.classifiers.meta.simpleVotingLikeCombiners;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.OptionHandler;
import weka.core.OptionHandlersTest;

public class BoundaryCombinerTest extends OptionHandlersTest {

	public BoundaryCombinerTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	OptionHandler getOptionHandler() {
		return new BoundaryCombiner();
	}
	
	public static Test suite() {
	    return new TestSuite(BoundaryCombinerTest.class);
	  }

public static void main(String[] args){
	    junit.textui.TestRunner.run(suite());
}
	

}
