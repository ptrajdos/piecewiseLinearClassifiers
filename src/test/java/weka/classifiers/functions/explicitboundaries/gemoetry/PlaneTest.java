package weka.classifiers.functions.explicitboundaries.gemoetry;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;

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
	      atts.add(new Attribute("Class", valList));
	      this.dataset = new Instances("daataset",atts,1);
	      this.dataset.setClassIndex(2);
	      
	      this.plane = new Plane(this.dataset);
	}

	@Test
	public void testDistanceToPlane() {
		double offset = -1;
		Instance nV = new DenseInstance(1.0, new double[] {1,0,1});
		nV.setDataset(dataset);
		
		
		Instance test1 = new DenseInstance(1.0, new double[] {2,0,1});
		test1.setDataset(dataset);
		
		Instance test2 = new DenseInstance(1.0, new double[] {-2,0,1});
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
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception has been thrown");
		}
		
		
		
		
	}

	@Test
	public void testSetNormalVector() {
		Instance normVec = null;
		normVec = this.plane.getNormalVector();
		assertNotNull(normVec);
		assertTrue(this.dataset.checkInstance(normVec));
		
		Instance test = new DenseInstance(1.5, new double[] {1.4,-6,1});
		test.setDataset(dataset);
		try {
			this.plane.setNormalVector(test);
			normVec = this.plane.getNormalVector();
			assertTrue(normVec.equalHeaders(test));
			assertEquals(test, normVec);
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception has been thrown");
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
		Instance nV = new DenseInstance(1.0, new double[] {1,0,1});
		nV.setDataset(dataset);
		
		Instance base = new DenseInstance(1.0, new double[] {0,1,1});
		base.setDataset(dataset);
		
		Instance tInst = new DenseInstance(1.0, new double[] {6.5,-3,1});
		tInst.setDataset(dataset);
		
		Instance tInstProj = new DenseInstance(1.0, new double[] {0,-3,1});
		tInstProj.setDataset(dataset);
		
		
		
		Instance[] b1 = null;
		Instance projT = null;
		
		try {
			this.plane.setNormalVector(nV);
			b1 = this.plane.planeBase;
			assertTrue("Check plane base", InstancesTools.checkEquall(base, b1[0], false));
			projT  = this.plane.projectOnPlane(tInst);
			assertTrue("Check plane projection", InstancesTools.checkEquall(projT, tInstProj, false));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception has been caught");
		}
		
		
	}

}
