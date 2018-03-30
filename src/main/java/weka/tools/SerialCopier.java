/**
 * 
 */
package weka.tools;

import weka.core.SerializedObject;

/**
 * Copy object using Serialization
 * 
 * @author Pawel Trajdos
 *
 */
public class SerialCopier {

	/**
	 * Copies object using serialization
	 * @param obj -- object implementing Serializable interface
	 * @return
	 * @throws Exception
	 */
	public static Object makeCopy(Object obj) throws Exception{
		
		return new SerializedObject(obj).getObject();
	}
	
}
