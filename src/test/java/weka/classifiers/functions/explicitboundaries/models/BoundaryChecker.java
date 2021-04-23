/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * @author pawel trajdos
 * @since 2.3.0
 * @version 2.3.0
 *
 */
public class BoundaryChecker {

	public static boolean checkBoundaries(ClassifierWithBoundaries classifier, Instances data) {
		
		try {
			classifier.buildClassifier(data);
			int numClasses = data.numClasses();
			for (Instance instance : data) {
				double val = classifier.getBoundary().classify(instance);
				double classVal = classifier.classifyInstance(instance);
				if(val >=0 && val <=numClasses)
					return false;
				
				if(!Utils.eq(val, classVal))
					return false;
				
			}
		} catch (Exception e) {
			return false;
		}
	
		return true;
	}

}
