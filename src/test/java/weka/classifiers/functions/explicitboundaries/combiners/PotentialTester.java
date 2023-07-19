/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.combiners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.junit.Test;

import weka.core.OptionHandler;
import weka.tools.tests.OptionHandlerChecker;



/**
 * @author pawel trajdos
 * @since 1.0.0
 * @version 2.4.1
 *
 */
public abstract class PotentialTester {

	public abstract PotentialFunction getPotentialFunction();
	
	@Test
	public void testPotential() {
		PotentialFunction potential = getPotentialFunction();
		PotentialTester.testPotential(potential);
	}
	
	@Test
	public void testOptions() {
		
		PotentialFunction potential  = getPotentialFunction();
		if (potential instanceof OptionHandler) {

			OptionHandlerChecker.checkOptions((OptionHandler) potential);
		}
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
		values.add(Double.MAX_VALUE);
		values.add(-Double.MAX_VALUE);
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
	
	@Test
	public void testInfs() {
		PotentialFunction potential = this.getPotentialFunction();
		
		LinkedList<Double> values = new LinkedList<Double>();
		values.add(Double.POSITIVE_INFINITY);
		values.add(Double.NEGATIVE_INFINITY);
		
		for (Double value : values) {
			try {
				double response = potential.getPotentialValue(value);
				assertFalse("Infinite response for: "+value +" ",Double.isInfinite(response));
				assertFalse("NaN response for: "+value+" ",Double.isNaN(response));
			} catch (Exception e) {
				e.printStackTrace();
				fail("An Exception has ben caught: " + e.toString());
			}
		}
		
	}
	
	@Test
	public void testNans() {
		PotentialFunction potential = this.getPotentialFunction();
		
		LinkedList<Double> values = new LinkedList<Double>();
		values.add(Double.NaN);
		
		for (Double value : values) {
			try {
				double response = potential.getPotentialValue(value);
				assertTrue("Not NaN response for: "+value+" ",Double.isNaN(response));
			} catch (Exception e) {
				e.printStackTrace();
				fail("An Exception has ben caught: " + e.toString());
			}
		}
		
	}
	
	

}
