package weka.classifiers.functions.explicitboundaries.gemoetry;

import static org.junit.Assert.assertEquals;
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

public class GrammShmidtOrthonormalTest {

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
	      
	      Instance test1  = new DenseInstance(1, new double[] {1,1,1});
	      test1.setDataset(dataset);
	      
	      Instance test2  = new DenseInstance(1, new double[] {1,-0.5,1});
	      test2.setDataset(dataset);
	      
	      Instance[] base = new Instance[] {test1, test2};
	      
	      GrammShmidtOrthonormal gs = new GrammShmidtOrthonormal();
	      DotProduct dp = gs.getDotProd();
	      
	      double dotp=0;
	      try {
			Instance[] nBase = gs.createOrthonormalBase(base);
			assertEquals("Base size", nBase.length, base.length);
			for(int i=0;i<nBase.length;i++) {
				assertEquals("Length",  1.0, dp.norm(nBase[i]), 1E-6);
			}
			for(int i=0;i<nBase.length;i++) {
				for(int j=i+1;j<nBase.length;j++)
					assertEquals("Orthogonality", 0, dp.dotProduct(nBase[i], nBase[j]),1E-6);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception has been caught");
		}
	      
	      
	}
	
	@Test
	public void debugTest() {
		GrammShmidtOrthonormal gs = new GrammShmidtOrthonormal();
		gs.setDebug(true);
		assertTrue("Get debug true", gs.isDebug());
		
		gs.setDebug(false);
		assertFalse("Get debug false", gs.isDebug());
	}

}
