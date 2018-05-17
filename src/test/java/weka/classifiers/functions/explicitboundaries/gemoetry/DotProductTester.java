package weka.classifiers.functions.explicitboundaries.gemoetry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class DotProductTester {

	public static void testDotProduct(DotProduct dotP) {
		/**
		 * Tests basic properties of dot products
		 */
		
		// Produce dataset
		
		  ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("Class", valList));
	      Instances dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(2);
	      
	      Instance test1  = new DenseInstance(1, new double[] {1,1,1});
	      test1.setDataset(dataset);
	      
	      Instance test2  = new DenseInstance(1, new double[] {0.5,0.5,2});
	      test2.setDataset(dataset);
	      
	      Instance test3  = new DenseInstance(1, new double[] {0.5,0.5,1});
	      test3.setDataset(dataset);
	      
	      
	      
	      try {
	    	//assume that the value does not depend on class variable
	    	  
	    	  double d1 = dotP.dotProduct(dataset, test1, test2);
	    	  double d2 = dotP.dotProduct(dataset, test1, test3);
	    	  assertEquals(d1, d2,1E-6);
	    	  
	    	  //commutative
	    	  d1 =dotP.dotProduct(dataset, test1, test2);
	    	  d2 =dotP.dotProduct(dataset, test2, test1);
	    	  assertEquals(d1, d2,1E-6);
	    	  
	    	  //scalar multiplication
	    	  double s1=2;
	    	  double s2=3;
	    	  
	    	  Instance test4 = new DenseInstance(1, new double[] {s1,s1,1});
	    	  test4.setDataset(dataset);
	    	  
	    	  Instance test5 = new DenseInstance(1, new double[] {s2,s2,1});
	    	  test5.setDataset(dataset);
	    	  //scalar
	    	  d1 = dotP.dotProduct(dataset, test1, test1);
	    	  d2 = dotP.dotProduct(dataset, test4, test5);
	    	  assertEquals(d1*s1*s2, d2,1e-16);
	    	  //orthogonal
	    	  
	    	  Instance test6 = new DenseInstance(1, new double[] {1,0,1});
	    	  test6.setDataset(dataset);
	    	  
	    	  Instance test7 = new DenseInstance(1, new double[] {0,1,1});
	    	  test7.setDataset(dataset);
	    	  
	    	  d1 = dotP.dotProduct(dataset, test6, test7);
	    	  assertEquals(d1, 0,1E-6);
	    	  
	    	  //distributive over vector addition
	    	  //t1(t4 + t5) = t1t4 + t1t5
	    	  
	    	  Instance test8 = new DenseInstance(1, new double[] {s1+s2,s1+s2,1});
	    	  test8.setDataset(dataset);
	    	  
	    	  d1 = dotP.dotProduct(dataset, test1, test8);
	    	  d2 = dotP.dotProduct(dataset, test1, test4) + dotP.dotProduct(dataset, test1, test5);
	    	  assertEquals(d1, d2,1E-6);
	    	  
	    	  
	    	  
	    			  
	    	  
	      }catch(Exception e) {
	    	  e.printStackTrace();
	    	  fail("An exception has been thrown");
	      }
	      
	      
	      
	}

}
