/**
 * 
 */
package weka.tools;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel trajdos
 *
 */
public class InstancesTools {

	/**
	 * Count instances belonging to classes
	 * @param dataset
	 * @return
	 */
	public static int[] getClassCounts(Instances dataset) {
		int numClass = dataset.numClasses();
		int numInstances = dataset.numInstances();
		int[] instCount =new int[numClass];
		for(int i=0;i<numInstances;i++) {
			instCount[(int) dataset.get(i).classValue()]++;
		}
		
		return instCount;
	}
	/**
	 * Checks the compatibility of the instance and the dataset
	 * @param dataset
	 * @param inst
	 * @return true if the instance and the dataset are compatible
	 * @throws Exception -- when there is some kind of incompatibility
	 */
	public static boolean checkCompatibility(Instances dataset, Instance inst)throws Exception{
		int numAttribs = dataset.numAttributes();
		if(inst.numAttributes() != numAttribs)
			throw new Exception("Incompatible number of attributes");
		
		if(inst.classIndex() != dataset.classIndex())
			throw new Exception("The class index does not match");
		
		if(!inst.dataset().equals(dataset))
			throw new Exception("Dataset is incompatible!");

		
		Attribute instAttr;
		Attribute setAttr;
		String msg;
		for(int a =0 ;a<numAttribs;a++){
			instAttr = inst.attribute(a);
			setAttr = dataset.attribute(a);
			
			msg  = instAttr.equalsMsg(setAttr);
			if(msg!=null) {
				throw new Exception(msg);
			}
			
		}
		
		return true;
	}
	/**
	 * Checks whether the instances are compatible.
	 * @param inst1
	 * @param inst2
	 * @return true if they are compatible
	 * @throws Exception if the instances are incompatible
	 */
	public static boolean checkCompatibility(Instance inst1, Instance inst2 )throws Exception{
		
		int numAttribs = inst1.numAttributes();
		if(inst2.numAttributes() != numAttribs)
			throw new Exception("Incompatible number of attributes");
		
		if(inst2.classIndex() != inst1.classIndex())
			throw new Exception("The class index does not match");
		
		if(!checkCompatibility(inst1.dataset(), inst2))
			throw new Exception("Datasets does not match");
		
		Attribute inst2Attr;
		Attribute inst1Attr;
		String msg;
		for(int a =0 ;a<numAttribs;a++){
			inst2Attr = inst2.attribute(a);
			inst1Attr = inst1.attribute(a);
			
			msg  = inst2Attr.equalsMsg(inst1Attr);
			if(msg!=null) {
				throw new Exception(msg);
			}
			
		}
		
		return true;
	}
	
	/**
	 * Check whether the instances are equall
	 * @param inst1 -- instance 1
	 * @param inst2 --  instance 2
	 * @param checkClass -- determines if the class attribute is checked
	 * @return -- boolean
	 * @throws Exception if something goes wrong
	 * 
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public static boolean checkEquall(Instance inst1, Instance inst2, boolean checkClass) throws Exception{
		if(!checkCompatibility(inst1, inst2))return false;
		Instances dataset = inst1.dataset();
		
		int numAttrs = dataset.numAttributes();
		
		double[] rep1 = inst1.toDoubleArray();
		double[] rep2 = inst2.toDoubleArray();
		int classIdx = dataset.classIndex();
		for(int a=0; a<numAttrs;a++) {
			if(a == classIdx && !checkClass )continue;
			if(rep1[a] != rep2[a])
				return false;
		}
		
		return true;
	}
	/**
	 * Creates a copy of the instance
	 * @author pawel trajdos
	 * 
	 * @param inst
	 * @return
	 * 
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public static Instance copyInstance(Instance inst) {
		Instance result = inst.copy(inst.toDoubleArray());
		return result;
	}
	

}
