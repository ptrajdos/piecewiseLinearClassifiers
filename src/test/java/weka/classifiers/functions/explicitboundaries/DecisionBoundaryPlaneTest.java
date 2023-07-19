/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.tools.data.RandomDataGenerator;
import weka.tools.data.RandomDoubleGenerator;
import weka.tools.data.RandomDoubleGeneratorGaussian;
import weka.tools.tests.DistributionChecker;

/**
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 *
 */
public class DecisionBoundaryPlaneTest extends DecisionBoundaryTest {

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.DecisionBoundaryTest#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		Instances dataset = this.getData();
		int idx1 =0;
		int idx2 =1;
		return new DecisionBoundaryPlane(dataset, idx1, idx2);
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
	
	public Instances getNumericOnlyDataset() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(3);
	      atts.add(new Attribute("X1"));
	      atts.add(new Attribute("X2"));
	      atts.add(new Attribute("X3"));
	      
	      Instances dataset = null;
	      dataset = new Instances("daataset",atts,1);
	      dataset.setClassIndex(2);
	      
	    
		return dataset;
	}
	
	public void testDataHeader() {
		Instances dataset = this.getData();
		DecisionBoundaryPlane decBound;
		try {
			decBound = (DecisionBoundaryPlane) this.getBoundary();
			assertTrue("Data header", dataset.equalHeaders(decBound.getDatasetHeader()));
			assertTrue("idx1", decBound.getClass1Idx()==0);
			assertTrue("idx2", decBound.getClass2Idx()==1);
			
			assertFalse("Debugging", decBound.isDebug());
			decBound.setDebug(true);
			assertTrue("Debugging", decBound.isDebug());
			decBound.setDebug(false);
			assertFalse("Debugging", decBound.isDebug());
		} catch (Exception e) {
			fail("Exception has been caught" + e.toString());
		}
		
	}
	
	public void testClassificationAbility() {
		try {
			DecisionBoundaryPlane pBound = (DecisionBoundaryPlane) this.getBoundary();
			Instances dataset = this.getData();
			Instance testInstance = dataset.get(0);
			
			double classResult = pBound.classify(testInstance);
			assertTrue("Class value", classResult>=0 & classResult<=1);
			
			int index = pBound.getIndex(testInstance);
			assertTrue("Index: ", index==0 | index ==1);
			
			testInstance = dataset.get(2);
			index = pBound.getIndex(testInstance);
			assertTrue("Index: ", index==0 | index ==1);
			
		} catch (Exception e) {
			fail("An exception has been caught" + e.toString());
		}
	}
	
	public void testInvalidIndices() {
		String className;
		try {
			DecisionBoundary decB =this.getBoundary();
			Class[] argsClasses= {Instances.class,int.class,int.class};
			Class classDef = decB.getClass();
			Constructor cons = classDef.getConstructor(argsClasses);
			
			try {
				Object[] args = {this.getData(),3,5};
				DecisionBoundary bnd = (DecisionBoundary) cons.newInstance(args);
				fail("Invalid indices has been accepted");
			}catch(Exception e) {
				
			}
			
			try {
				Object[] args = {this.getData(),-1,0};
				DecisionBoundary bnd = (DecisionBoundary) cons.newInstance(args);
				fail("Invalid indices has been accepted");
			}catch(Exception e) {
				
			}
			
			try {
				Object[] args = {this.getNumericOnlyDataset(),0,1};
				DecisionBoundary bnd = (DecisionBoundary) cons.newInstance(args);
				fail("Invalid indices has been accepted");
			}catch(Exception e) {
				
			}
			
			
		} catch (Exception e) {
			
			fail("Preparation has failed");
		}
	}
	
	public void checkPlane(Instances dataset) throws Exception {
		
		DecisionBoundaryPlane decB = new DecisionBoundaryPlane(dataset, 0, 1);
		
		for(Instance instance : dataset) {
			
			double classResult =  decB.classify(instance);
			
			assertTrue("Class value", classResult>=0 & classResult<=1);
			
			int index = decB.getIndex(instance);
			assertTrue("Index: ", index==0 | index ==1);
			
			double distance = decB.getDistance(instance);
			assertFalse("Distance NaN", Double.isNaN(distance));
			assertTrue("Distance Finite", Double.isFinite(distance));
			assertTrue("Distance greater than zero", Utils.grOrEq(distance, 0.0));
			
			double disFun = decB.getValue(instance);
			assertFalse("Discriminant value NaN", Double.isNaN(disFun));
			assertTrue("Discriminant value", Double.isFinite(disFun));
			
			if(index == 0 ) {
				assertTrue("Index 0 dis", disFun>0);
			}else {
				assertTrue("Index 1 dis", disFun<=0);
			}
			
		}
		
	}
	
	 public void testOnCondensedData() {
		 
		 
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(0);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 RandomDoubleGenerator doubleGen = new RandomDoubleGeneratorGaussian();
		 doubleGen.setDivisor(10000.0);
		 gen.setDoubleGen(doubleGen );
		 
		 Instances dataset = gen.generateData();
		 
		 
		 
		 try {
			 
			this.checkPlane(dataset);
			
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
	 }
	 
	 
	 public void testOnNominalConvertedData() {
		 
		 
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(10);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumNumericAttributes(0);
		 gen.setNumClasses(2);
		 gen.setMaxNumNominalValues(10);
		 gen.setNumObjects(100);
		 		 
		 Instances dataset = gen.generateData();
		 
		 NominalToBinary nom2Bin = new NominalToBinary();
		 
		 
		 
		 try {
			 
			 nom2Bin.setInputFormat(dataset);
			 dataset = Filter.useFilter(dataset, nom2Bin);
			 
			this.checkPlane(dataset);
			
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
	 }
	 
	 public void testOnNominalUnaryData() {
		 
		 
		 RandomDataGenerator gen = new RandomDataGenerator();
		 gen.setNumNominalAttributes(10);
		 gen.setNumStringAttributes(0);
		 gen.setNumDateAttributes(0);
		 gen.setNumNumericAttributes(0);
		 gen.setNumClasses(2);
		 gen.setMaxNumNominalValues(1);
		 gen.setNumObjects(100);
		 gen.setAllowUnary(true);
		 		 
		 Instances dataset = gen.generateData();
		 
		 NominalToBinary nom2Bin = new NominalToBinary();
		 
		 
		 try {
			 
			 nom2Bin.setInputFormat(dataset);
			 dataset = Filter.useFilter(dataset, nom2Bin);
			 
			this.checkPlane(dataset);
			
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.getMessage());
		}
	 }

	

}
