package weka.classifiers.functions.explicitboundaries.gemoetry;

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

public class InstancesGeometricOperationsTest {

	@Test
	public void testInstOperator() {
		Instances data = this.getData1();
		
		try {
			Instance test1  = new DenseInstance(1, new double[] {0,0,1,1});
			test1.setDataset(data);
			Instance res = InstancesGeometricOperations.subtractInstances(data.get(0), data.get(0));
			assertTrue("Zeroed Instances: ", InstancesTools.checkEquall(res, test1, false));
			
			res = InstancesGeometricOperations.subtractInstances(data.get(0), data.get(0),false);
			assertTrue("Zeroed Instances: ", InstancesTools.checkEquall(res, test1, false));
			
			Instance test2 = new DenseInstance(1, new double[] {2,0,1});
			test2.setDataset(data);
			
			res = InstancesGeometricOperations.addInstances(data.get(0), data.get(0));
			assertTrue("Instances sum:",InstancesTools.checkEquall(res, test2, false)); 
			
			res = InstancesGeometricOperations.addInstances(data.get(0), data.get(0),false);
			assertTrue("Instances sum:",InstancesTools.checkEquall(res, test2, false));
			
			
			//Test nominal Attribs
			
			Instance test3  = new DenseInstance(1, new double[] {2,0,1,1});
			test3.setDataset(data);
			
			Instance test4  = new DenseInstance(1, new double[] {-2,0,0,1});
			test4.setDataset(data);
			
			Instance test5  = new DenseInstance(1, new double[] {0,0,1,1});
			test5.setDataset(data);
			
			Instance test6  = new DenseInstance(1, new double[] {0,0,0,1});
			test6.setDataset(data);
			
			res = InstancesGeometricOperations.addInstances(test3, test4);
			assertTrue("Addition with different nominal attributes (1,0)", InstancesTools.checkEquall(res, test5, false));
			
			res = InstancesGeometricOperations.addInstances(test4, test3);
			assertTrue("Addition with different nominal attributes (0,1)", InstancesTools.checkEquall(res, test6, false));
			
			
			
		}catch(Exception e) {
			fail("Exception has been caught: " + e.toString() );
		}
		
		
		
	}
	
	@Test 
	public void testIncompatible() {
		Instances data1 = this.getData1();
		Instances data2 = this.getData2();
		
		try {
			InstancesGeometricOperations.addInstances(data1.get(0), data2.get(0),true);
			fail("No exception has been caught");
		}catch(Exception e) {
			assertTrue("Exception has been caught", true);
		}
		
		try {
			InstancesGeometricOperations.addInstances(data1.get(0), data2.get(0),false);
		}catch(Exception e) {
			fail("An exception has been caught " + e.toString());
		}
	}
	
	@Test
	public void testScalingNominal() {
		Instances data = this.getData2();
		
		Instance inst1 = data.get(0);
		Instance scaled = InstancesGeometricOperations.scale(inst1, 3.0);
		try {
		assertTrue("No scaling has been done!", InstancesTools.checkEquall(scaled, inst1, false));
		}catch(Exception e) {
			fail("Exception has been caught: " + e.toString());
		}
	}
	
	public Instances getData1() {
		 ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("AN1", valList));
	      atts.add(new Attribute("Class", valList));
	      Instances dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(3);
	      
	      Instance test1  = new DenseInstance(1, new double[] {1,0,1,1});
	      test1.setDataset(dataset);
	      dataset.add(test1);
	      
	      Instance test2  = new DenseInstance(1, new double[] {1,0,1,2});
	      test2.setDataset(dataset);
	      dataset.add(test2);
	      
	      Instance test3  = new DenseInstance(1, new double[] {0,1,1,1});
	      test3.setDataset(dataset);
	      dataset.add(test3);
	      
	      Instance test4  = new DenseInstance(1, new double[] {0,1,1,2});
	      test4.setDataset(dataset);
	      dataset.add(test4);
	      
	      Instance test5  = new DenseInstance(1, new double[] {6,6,1,2});
	      test5.setDataset(dataset);
	      dataset.add(test5);
	      
	      Instance test6  = new DenseInstance(1, new double[] {6,0,1,2});
	      test6.setDataset(dataset);
	      dataset.add(test6);
	      
	      Instance test7  = new DenseInstance(1, new double[] {0,6,1,2});
	      test7.setDataset(dataset);
	      
	      dataset.add(test7);
	      return dataset;
	}
	
	public Instances getData2() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("A1", valList));
	      atts.add(new Attribute("A2", valList));
	      atts.add(new Attribute("Class", valList));
	      Instances dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(2);
	      Instance test1  = new DenseInstance(1, new double[] {1,0,1});
	      test1.setDataset(dataset);
	      dataset.add(test1);
	      
	      return dataset;
	}
	
	@Test
	public void makeInstance() {
		try {
			InstancesGeometricOperations op = new InstancesGeometricOperations();
		}catch(Exception e) {
			fail("An exception has been caught: " + e.toString());
		}
	}

}
