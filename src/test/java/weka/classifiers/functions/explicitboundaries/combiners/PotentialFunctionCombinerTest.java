package weka.classifiers.functions.explicitboundaries.combiners;

import weka.core.OptionHandlersTest.OptionHandlerTest;

public class PotentialFunctionCombinerTest extends OptionHandlerTest {

	public PotentialFunctionCombinerTest(String name, String classname) {
		super(name, classname);
	}
	public PotentialFunctionCombinerTest(String name) {
		super(name, PotentialFunctionCombiner.class.getCanonicalName());
	}
	
	public void testNoBoundaries() {
		PotentialFunctionCombiner funComb = new PotentialFunctionCombiner();
		
		try {
			
			funComb.getDecision(null);
			fail("No exception has been thrown");
		}catch(Exception e) {
		 assertTrue("An exception has been caught", true);
		 
		}
		
		try {
			
			funComb.getClass(null);
			fail("No exception has been thrown");
		}catch(Exception e) {
		 assertTrue("An exception has been caught", true);
		 
		}
	}


}
