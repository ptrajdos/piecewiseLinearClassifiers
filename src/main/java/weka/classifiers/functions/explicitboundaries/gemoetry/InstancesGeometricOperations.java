package weka.classifiers.functions.explicitboundaries.gemoetry;

import weka.core.Instance;
import weka.tools.InstancesTools;

/**
 * Geometric operations for instances
 * @author pawel trajdos
 * @since 1.4.0
 * @version 1.4.0
 *
 */
public class InstancesGeometricOperations {

	/**
	 * Scales the numeric attributes of the instance
	 * @param inst -- instance
	 * @param factor -- scale factor
	 * @return -- rescaled instance
	 * @since 1.4.0
	 * @version 1.4.0
	 */
	public static Instance scale(Instance inst, double factor) {
		double[] rep = inst.toDoubleArray();
		
		int numAttrs = inst.numAttributes();
		int classIdx = inst.classIndex();
		for(int a=0;a<numAttrs;a++) {
			if(a == classIdx)continue;
			if(inst.attribute(a).isNumeric()) {
				rep[a]*=factor;
			}
		}
		Instance result = inst.copy(rep);
		return result;
	}
	
	/**
	 * Addition or subtraction of instances. The method <b>ignores</b> attributes other than <b>numeric</b>.
	 * Non-numeric atributes are taken from instance 1
	 * @param i1 -- first instance
	 * @param i2 -- second instance
	 * @param addFlag -- addition/subtraction flag
	 * @return -- resulting instance
	 * @throws Exception -- when instances are incompatible
	 * 
	 * @since 1.4.0
	 * @version 2.1.0
	 */
	private static Instance addSub(Instance i1, Instance i2, boolean addFlag)throws Exception{
		return addSub(i1, i2, addFlag, true);
	}

	/**
	 * Addition or subtraction of instances. The method <b>ignores</b> attributes other than <b>numeric</b>.
	 * Non-numeric atributes are taken from instance 1
	 * @param i1 -- first instance
	 * @param i2 -- second instance
	 * @param addFlag -- addition/subtraction flag
	 * @param checkCompatibility TODO
	 * @return -- resulting instance
	 * @throws Exception -- when instances are incompatible
	 * 
	 * @since 2.1.0
	 * @version 2.1.0
	 */
	private static Instance addSub(Instance i1, Instance i2, boolean addFlag, boolean checkCompatibility)throws Exception{
		if(checkCompatibility)
			if(!InstancesTools.checkCompatibility(i1, i2))throw new Exception("Instances incompatible");
		
		double[] rep1 = i1.toDoubleArray();
		double[] rep2 = i2.toDoubleArray();
		for(int a=0;a<rep1.length;a++) {
			if(i1.attribute(a).isNumeric()) {
					rep1[a]+= addFlag? rep2[a]:(-rep2[a]);
			}
		}
		Instance result = i1.copy(rep1);
		return result;
	}
	
	/**
	 * Adds two instances. Uses only numeric attributes. The remaining attributes will be taken from i1. 
	 * @param i1 
	 * @param i2
	 * @return
	 * @throws Exception
	 * 
	 * @since 1.4.0
	 * @version 2.1.0
	 */
	public static Instance addInstances(Instance i1, Instance i2)throws Exception{
		return addSub(i1, i2, true, true);
	}
	
	/**
	 * Adds two instances. Uses only numeric attributes. The remaining attributes will be taken from i1. 
	 * @param i1 
	 * @param i2
	 * @param checkCompatibility -- defines whether the compatibility check of the instances is done
	 * @return
	 * @throws Exception
	 * 
	 * @since 2.1.0
	 * @version 2.1.0
	 */
	public static Instance addInstances(Instance i1, Instance i2, boolean checkCompatibility)throws Exception{
		return addSub(i1, i2, true, checkCompatibility);
	}

	/**
	 * Subtracts two instances. Uses only numeric attributes. The remaining attributes will be taken from i1. 
	 * @param i1 
	 * @param i2
	 * @return
	 * @throws Exception
	 * 
	 * @since 1.4.0
	 * @version 2.1.0
	 */
	public static Instance subtractInstances(Instance i1, Instance i2)throws Exception{
		return addSub(i1, i2, false, true);
	}
	/**
	 * Subtracts two instances. Uses only numeric attributes. The remaining attributes will be taken from i1.
	 * @param i1
	 * @param i2
	 * @param checkCompatibility
	 * @return
	 * @throws Exception
	 * 
	 * @since 2.1.0
	 * @version 2.1.0
	 */
	public static Instance subtractInstances(Instance i1, Instance i2,boolean checkCompatibility)throws Exception{
		return addSub(i1, i2, false, checkCompatibility);
	}
	
}
