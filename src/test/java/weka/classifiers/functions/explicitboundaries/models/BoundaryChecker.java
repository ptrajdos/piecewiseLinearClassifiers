/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import static org.junit.Assert.*;


/**
 * @author pawel trajdos
 * @since 2.3.0
 * @version 2.5.0
 *
 */
public class BoundaryChecker {

	public static boolean checkBoundaries(ClassifierWithBoundaries classifier, Instances data) {
		
		try {
			classifier.buildClassifier(data);
			int numClasses = data.numClasses();
			for (Instance instance : data) {
				
				DecisionBoundary bnd = classifier.getBoundary();
				double val = bnd.classify(instance);
				double classVal = classifier.classifyInstance(instance);
				double[] distribution = classifier.distributionForInstance(instance);
				
				double funVal = classifier.getBoundary().getValue(instance);
				double phantomClassVal = 1 - classVal*2.0;
				
				int maxIdx = Utils.maxIndex(distribution);
				
				if(!(val >=0 && val <=numClasses))
					fail("Wrong class index");
				
				if(!Utils.eq(val, classVal))
					fail("Inconsistent prediction -- boundary and classifyInstance");
				
				if(!Utils.eq(val, maxIdx))
					fail("Boundary class index is not compatible with highest value in distribution response");
				
				if(funVal*phantomClassVal<0)
					fail("Sign disagreement in boundary discriminant value"); 				
			}
		} catch (Exception e) {
			fail("Exception has been caught: "+ e.getLocalizedMessage());
		}
	
		return true;
	}

}
