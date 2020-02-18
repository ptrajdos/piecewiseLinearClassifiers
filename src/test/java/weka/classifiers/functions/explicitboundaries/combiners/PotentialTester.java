/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import java.util.LinkedList;

import weka.core.OptionHandlersTest.OptionHandlerTest;

/**
 * @author pawel trajdos
 * @since 1.0.0
 * @version 2.1.3
 *
 */
public class PotentialTester extends OptionHandlerTest {

	public PotentialTester(String name, String classname) {
		super(name, classname);
	}
	
	public void testPotential() {
		PotentialFunction potential = (PotentialFunction) this.getOptionHandler();
		PotentialTester.testPotential(potential);
	}

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
		//values.add(Double.MAX_VALUE);
		//values.add(-Double.MAX_VALUE);
		values.add(Double.MIN_NORMAL);
		values.add(-Double.MIN_NORMAL);
		values.add(Double.MIN_VALUE);
		values.add(-Double.MIN_VALUE);
		for (Double value : values) {
			try {
				response = fun.getPotentialValue(value);
				assertFalse("Infinite response for: "+value +" ",Double.isInfinite(response));
				assertFalse("NaN response for: "+value+" ",Double.isNaN(response));
			} catch (Exception e) {
				e.printStackTrace();
				fail("An Exception has ben caught: " + e.toString());
			}
		}
		
		
	}

}
