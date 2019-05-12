package weka.classifiers.functions.explicitboundaries.combiners;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.OptionHandlersTest.OptionHandlerTest;

public class PotentialFunctionExp_BTest extends OptionHandlerTest {

	public PotentialFunctionExp_BTest(String name, String classname) {
		super(name, classname);
	}
	
	public PotentialFunctionExp_BTest(String name) {
		this(name, PotentialFunctionExp.class.getName());
	}
	
	public static Test suite() {
	    return new TestSuite(PotentialFunctionExp_BTest.class);
	  }

	public static void main(String[] args){
	    junit.textui.TestRunner.run(suite());
	  }

	

}
