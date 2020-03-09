package weka.classifiers.functions.explicitboundaries.gemoetry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;

/**
 * 
 * @author pawel trajdos
 * @since 0.1.0
 * @version 1.4.0
 *
 */

public class DotProductEuclideanTest {

	
	public DotProductEuclidean getDotProductEuc() {
		return new DotProductEuclidean();
	}
	
	@Test
	public void testDotProduct() {
		DotProductTester.testDotProduct(this.getDotProductEuc());
	}
	
	@Test
	public void testProjections(){
		testProjection(getDotProductEuc());
		DotProductEuclidean dotP = getDotProductEuc();
		dotP.setDebug(false);
		testProjection(dotP);
		
	}
	
	 
	public void testProjection(DotProductEuclidean dotP) {
		 ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      atts.add(new Attribute("X3"));
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("Class", valList));
	      Instances dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(3);
	      
	      Instance test1  = new DenseInstance(1, new double[] {1,0,0,1});
	      test1.setDataset(dataset);
	      
	      Instance test2  = new DenseInstance(1, new double[] {1,0,0,2});
	      test2.setDataset(dataset);
	      
	      Instance test3  = new DenseInstance(1, new double[] {0,1,0,1});
	      test3.setDataset(dataset);
	      
	      Instance test4  = new DenseInstance(1, new double[] {0,1,0,2});
	      test4.setDataset(dataset);
	      
	      Instance test5  = new DenseInstance(1, new double[] {6,6,0,2});
	      test5.setDataset(dataset);
	      
	      Instance test6  = new DenseInstance(1, new double[] {6,0,0,2});
	      test6.setDataset(dataset);
	      
	      Instance test7  = new DenseInstance(1, new double[] {0,6,0,2});
	      test7.setDataset(dataset);
	      
	      
	      
	      try {
			Instance res1 =  dotP.projection(test5, test1);//Class taken from test1, but it should be taken from test5
			assertTrue("Check projection 1", InstancesTools.checkEquall(res1, test6,false));
			assertFalse("Check projection 1 -F ", InstancesTools.checkEquall(res1, test7,false));
			res1 =  dotP.projection(test5, test3);
			assertTrue("Check projection 2", InstancesTools.checkEquall(res1, test7,false));
			assertFalse("Check projection 2 -F ", InstancesTools.checkEquall(res1, test6,false));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception has been caught");
		}
	}
	
	@Test
	public void debugTest() {
		DotProductEuclidean dotP = new DotProductEuclidean();
		dotP.setDebug(true);
		assertTrue("Debug true", dotP.isDebug());
		
		dotP.setDebug(false);
		assertFalse("Debug true", dotP.isDebug());
	}
}
