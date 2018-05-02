/**
 * 
 */
package weka.tools;

import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel
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
	
	

}
