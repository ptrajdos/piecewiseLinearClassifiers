/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.LinkedList;

/**
 * @author pawel
 *
 */
public class PotentialTester {

	public static void testPotential(PotentialFunction fun) {
		double minVal=-100;
		double maxVal= 100;
		double  increment =1;
		double val=minVal;
		double response =0;
		LinkedList<Double> values = new LinkedList<Double>();
		
		while(val<=maxVal) {
			values.add(val);
			val+=increment;	
		}
		values.add(Double.MAX_VALUE);
		values.add(-Double.MAX_VALUE);
		values.add(Double.MIN_NORMAL);
		values.add(-Double.MIN_NORMAL);
		values.add(Double.MIN_VALUE);
		values.add(-Double.MIN_VALUE);
		for (Double value : values) {
			try {
				response = fun.getPotentialValue(value);
				assertFalse("Infinite response",Double.isInfinite(response));
				assertFalse("NaN response",Double.isNaN(response));
			} catch (Exception e) {
				e.printStackTrace();
				fail("An Exception has ben caught");
			}
		}
		
		
	}

}
