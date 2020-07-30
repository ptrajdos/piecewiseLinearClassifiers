package weka.classifiers.functions.explicitboundaries;

import org.mockito.Mockito;

import junit.framework.TestCase;
import weka.tools.SerialCopier;

/**
 * Tests DecisionBoundary abstract class
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 */
public class DecisionBoundaryTest extends TestCase {
	
	

	public DecisionBoundary getBoundary() throws Exception {
		return Mockito.mock(DecisionBoundary.class, Mockito.CALLS_REAL_METHODS);
	}
	
	public void testSerialization() {
		try {
			DecisionBoundary bound = (DecisionBoundary) SerialCopier.makeCopy(this.getBoundary());
			String toString = bound.toString();
			assertTrue("To string not null",toString!=null);
			assertTrue("Non-zero lenght", toString.length()>0);
		}catch(Exception e) {
			fail("Serialization has failed");
		}	
	}
	
	
	

}
