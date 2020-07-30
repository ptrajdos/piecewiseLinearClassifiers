package weka.classifiers.functions.explicitboundaries.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;

public class MajorityPlaneBoundaryModelTest {

	@Test
	public void test() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      LinkedList<String> valList = new LinkedList<String>();
	      valList.add("1");
	      valList.add("2");
	      atts.add(new Attribute("Class", valList));
	      Instances dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(2);
	      
	      Instance nV = new DenseInstance(1.0, new double[] {0,0,1});
			nV.setDataset(dataset);
			
			Instance test = new DenseInstance(1.0, new double[] {1,5,1});
			test.setDataset(dataset);
	      
			Instance proj = null;
	      
	      MajorityPlaneBoundaryModel mpbm = new MajorityPlaneBoundaryModel();
	      DecisionBoundaryPlane decPlane = null;
	      Plane pl = null;
	      try {
			mpbm.buildDefaultModelPlane(dataset);
			decPlane = mpbm.getPlaneModel();
			pl = decPlane.getDecisionPlane();
			
			assertEquals("Offset", -1, pl.getOffset(),1e-6);
			assertTrue("Normal vector is zero", InstancesTools.checkEquall(nV, pl.getNormalVector(), false));
			proj = pl.projectOnPlane(test);
			assertTrue("Projected vector is zero", InstancesTools.checkEquall(nV, proj, false));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception has been caught");
		}
	}
	
	@Test
	public void testDebug() {
		MajorityPlaneBoundaryModel mpbm = new MajorityPlaneBoundaryModel();
		mpbm.setDebug(false);
		assertTrue("debug false", mpbm.isDebug()==false);
		
		mpbm.setDebug(true);
		assertTrue("debug false", mpbm.isDebug()==true);
	}

}
