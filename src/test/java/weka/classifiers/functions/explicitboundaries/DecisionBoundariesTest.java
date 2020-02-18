/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.SerialCopier;

/**
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 *
 */
public class DecisionBoundariesTest extends TestCase {

	List<DecisionBoundary> getBoundariesList() throws Exception{
		ArrayList<DecisionBoundary> bndList = new ArrayList<DecisionBoundary>();
		DecisionBoundaryPlane b1 = new DecisionBoundaryPlane(getData(), 0,1);
		DecisionBoundaryPlane b2 = new DecisionBoundaryPlane(getData(), 0,1);
		bndList.add(b1);
		bndList.add(b2);
		return bndList;
	}
	
	DecisionBoundaries getBoundaries() throws Exception {
		DecisionBoundaries bnds = new DecisionBoundaries(getBoundariesList());
		return bnds;
	}
	
	public void testSerialization() {
		try {
			DecisionBoundaries bnd = (DecisionBoundaries) SerialCopier.makeCopy(this.getBoundaries());
			assertTrue("Not null ", bnd != null);
		}catch(Exception e) {
			fail("An exception has been caught " + e.toString());
		}	
	}
	
	public void testBoundaries() {
		try {
			DecisionBoundaries bnds = this.getBoundaries();
			
			String description = bnds.toString();
			assertTrue("Not null to String", description !=null);
			assertTrue("Description length", description.length()>0);
			
			List<DecisionBoundary> list = bnds.getBoundaries();
			assertTrue("Not null boundaries", list!=null);
			assertTrue("Boundaries length", list.size()==2);
			
		}catch(Exception e) {
			fail("Object initialization failure");
		}
		
	}
	
	
	public Instances getData() {
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
	      
	      Instance nV = new DenseInstance(1.0, new double[] {1,0,0,1,1});
			nV.setDataset(dataset);
			dataset.add(nV);
			
			Instance tInst = new DenseInstance(1.0, new double[] {1,1,0,1,1});
			tInst.setDataset(dataset);
			dataset.add(tInst);
			
			Instance tInstProj = new DenseInstance(1.0, new double[] {0,1,0,1,0});
			tInstProj.setDataset(dataset);
			dataset.add(tInstProj);
	    
		return dataset;
	}

}
