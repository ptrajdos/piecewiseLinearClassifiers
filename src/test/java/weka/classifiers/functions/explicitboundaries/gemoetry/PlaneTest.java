package weka.classifiers.functions.explicitboundaries.gemoetry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;
import weka.tools.SerialCopier;
import weka.tools.data.RandomDataGenerator;

public class PlaneTest {

	Plane plane = null;
	Instances dataset = null;
	@Before
	public void setUp() throws Exception {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("Attrib", valList));
	      atts.add(new Attribute("Class", valList));
	      
	      this.dataset = new Instances("daataset",atts,1);
	      this.dataset.setClassIndex(3);
	      
	      this.plane = new Plane(this.dataset);
	}
	
	
	@Test 
	public void testDebug() {
		assertFalse("Debug false by default", this.plane.isDebug());
		this.plane.setDebug(true);
		assertTrue("Debug set to true", this.plane.isDebug());
		this.plane.setDebug(false);
	}
	
	@Test
	public void testSerialization() {
		try {
			Plane pl = (Plane) SerialCopier.makeCopy(plane);
			assertTrue("Not null", pl!=null);
		}catch(Exception e) {
			fail("An exception has been caught" + e.toString());
		}
	}
	
	@Test
	public void testSetNormalVector() {
		Instance normVec = null;
		normVec = this.plane.getNormalVector();
		assertNotNull(normVec);
		assertTrue(this.dataset.checkInstance(normVec));
		
		Instance test = new DenseInstance(1.5, new double[] {1.4,-6,1,1});
		assertTrue("Instance compatibility", this.dataset.checkInstance(test));
		test.setDataset(this.dataset);
		
		try {
			this.plane.setNormalVector(test);
			normVec = this.plane.getNormalVector();
			assertTrue(normVec.equalHeaders(test));
			assertEquals(test, normVec);
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception has been thrown " + e.toString());
		}
		
	}
	
	@Test 
	public void testSetInvalidNormalVector() {
		Instance normVec = null;
		normVec = this.plane.getNormalVector();
		assertNotNull(normVec);
		assertTrue(this.dataset.checkInstance(normVec));
		
		Instance test = new DenseInstance(1.5, new double[] {1.4,-6,1,1,9});
		
		try {
			this.plane.setNormalVector(test);
			fail("No exception has been thrown. Invalid normal vector has been set");
		} catch (Exception e) {
			assertTrue("Invalid normal vector",true);
		}
	}
	
	@Test
	public void testSetOffset() {
		double offset =0;
		offset = this.plane.getOffset();
		assertFalse("NaN", Double.isNaN(offset));
		assertFalse("Infinity", Double.isInfinite(offset));
		
		offset = 6;
		this.plane.setOffset(offset);
		assertEquals(offset, this.plane.getOffset(),1E-6);
		
	}

	@Test
	public void testDistanceToPlane() {
		double offset = -1;
		Instance nV = new DenseInstance(1.0, new double[] {1,0,1,1});
		nV.setDataset(dataset);
		
		
		Instance test1 = new DenseInstance(1.0, new double[] {2,0,1,1});
		test1.setDataset(dataset);
		
		Instance test2 = new DenseInstance(1.0, new double[] {-2,0,1,1});
		test2.setDataset(dataset);
		
		
		try {
			this.plane.setNormalVector(nV);
			this.plane.setOffset(offset);
			this.plane.setNormalizeDistance(false);
			double val =1;
			
			double dist = this.plane.distanceToPlane(test1);
			
			assertEquals("Plane dist 1, t1",val, dist,1E-6);
			assertEquals(3, this.plane.distanceToPlane(test2),1e-6);
			assertTrue(this.plane.sideOfThePlane(test1)>0);
			assertTrue(this.plane.sideOfThePlane(test2)<0);
			assertEquals("Plane dist 1, t2",val, this.plane.distanceToPlane(test1),1e-6);
			
			//Test after normalization
			assertFalse("No normalization by default", this.plane.isNormalizeDistance());
			this.plane.setNormalizeDistance(true);
			assertTrue("Normalization is set now.", this.plane.isNormalizeDistance());
			
			double normFactor =Math.sqrt(2.0); 
			val/=normFactor;
			dist = this.plane.distanceToPlane(test1);
			
			assertEquals("Plane dist 1, t1",val, dist,1E-6);
			assertEquals(3.0/normFactor, this.plane.distanceToPlane(test2),1e-6);
			assertTrue(this.plane.sideOfThePlane(test1)>0);
			assertTrue(this.plane.sideOfThePlane(test2)<0);
			assertEquals("Plane dist 1, t2",val, this.plane.distanceToPlane(test1),1e-6);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception has been thrown: " + e.toString());
		}
		
		
		
		
	}

	

	

	@Test
	public void testGetDotProduct() {
		DotProduct dp = this.plane.getDotProduct();
		assertNotNull(dp);
		assertTrue(dp instanceof DotProductEuclidean);
		DotProductEuclidean dpe = new DotProductEuclidean();
		plane.setDotProduct(dpe);
		assertEquals(dpe, plane.getDotProduct());
	}

	@Test
	public void testGetDataHeader() {
		assertEquals(this.dataset, this.plane.getDataHeader());
		
	}
	
	@Test
	public void testPlaneBase() {
		Instance nV = new DenseInstance(1.0, new double[] {1,0,1,1});
		nV.setDataset(dataset);
		
		Instance base = new DenseInstance(1.0, new double[] {0,1,0,1});
		base.setDataset(dataset);
		
		Instance tInst = new DenseInstance(1.0, new double[] {6.5,-3,1,1});
		tInst.setDataset(dataset);
		
		Instance tInstProj = new DenseInstance(1.0, new double[] {0,-3,1,1});
		tInstProj.setDataset(dataset);
		
		
		Instances testInstances = new Instances(this.dataset,0);
		testInstances.add(nV);
		testInstances.add(base);
		testInstances.add(tInst);
		testInstances.add(tInstProj);
		
		Instance[] b1 = null;
		Instance projT = null;
		
		try {
			this.plane.setNormalVector(nV);
			b1 = this.plane.planeBase;
			assertTrue("Check plane base", InstancesTools.checkEquall(base, b1[0], false));
			projT  = this.plane.projectOnPlane(tInst);
			assertTrue("Check plane projection", InstancesTools.checkEquall(projT, tInstProj, false));
			String toString = this.plane.toString();
			assertTrue("String length", toString.length()>0);
			
			Instances projectedInstances = this.plane.projectOnPlane(testInstances);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception has been caught" + e.toString());
		}
		
		
	}
	
	public Instances create3DData() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      atts.add(new Attribute("X3"));
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("Attrib", valList));
	      atts.add(new Attribute("Class", valList));
	      Instances dataset = null;
	      dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(3);
	    
		return dataset;
	}
	
	@Test 
	public void testPlane3D() {
		Instances dataset = this.create3DData();
		Plane plane = new Plane(dataset);
		
		Instance nV = new DenseInstance(1.0, new double[] {1,0,0,1,1});
		nV.setDataset(dataset);
		
		Instance tInst = new DenseInstance(1.0, new double[] {1,1,0,1,1});
		tInst.setDataset(dataset);
		
		Instance tInstProj = new DenseInstance(1.0, new double[] {0,1,0,1,1});
		tInstProj.setDataset(dataset);
		
		try {
			plane.setNormalVector(nV);
			
			Instance getNv = plane.getNormalVector();
			assertTrue("Proper normal vector for 3D space", InstancesTools.checkEquall(getNv, nV, false));
			
			Instance[] base = plane.planeBase;
			assertTrue("Plane base size", base.length ==2);
			
			Instance projT = plane.projectOnPlane(tInst);
			assertTrue("Check plane projection", InstancesTools.checkEquall(projT, tInstProj, false));
		} catch (Exception e) {

			fail("Exception has ben caught: " + e.toString());
		}
	}
	
	@Test
	public void testPlaneBased() {
		RandomDataGenerator gen = new RandomDataGenerator();
		gen.setNumNominalAttributes(0);
		gen.setNumStringAttributes(0);
		gen.setNumDateAttributes(0);
		
		Instances data = gen.generateData();
		Instance testInstance = data.get(0);
		
		Plane plane = new Plane(data);
		
		try {
			
			Instances planeBasedInstances = plane.planeBasedInstances(data);
			assertTrue("Not null planeBased instances", planeBasedInstances!=null);
			assertTrue("Num Attribs", planeBasedInstances.numAttributes() == data.numAttributes()-1);
			
			Instance planeBasedInst = plane.planeBasedInstance(testInstance);
			assertTrue("Not null planeBased instance", planeBasedInst!=null);
		} catch (Exception e) {
			fail("AnException has been caught: " + e.getMessage());
		}
	}
	

}
