/**
 * 
 */
package weka.classifiers.functions.explicitboundaries;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import weka.classifiers.Classifier;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.trees.J48;

/**
 * @author pawel trajdos
 * @since 2.1.3
 * @version 2.1.3
 *
 */
public class DecisionBoundariesExtractorTest {

	@Test
	public void testExtractorProper() {
		
		Classifier[] classifiers = {new NearestCentroidBoundary(), new NearestCentroidBoundary()};
		DecisionBoundariesExtractor extractor = new DecisionBoundariesExtractor();
		
		try {
			DecisionBoundaries extracted =extractor.extractBoundaries(classifiers);
			assertTrue("Extracted not null", extracted != null);
		} catch (Exception e) {
			fail("Decision boundaries extraction has fialied: " + e.toString());
		} 
	}
	
	@Test 
	public void testExtractorFail() {
		Classifier[] classifiers = {new NearestCentroidBoundary(), new J48()};
		DecisionBoundariesExtractor extractor = new DecisionBoundariesExtractor();
		
		try {
			DecisionBoundaries extracted =extractor.extractBoundaries(classifiers);
			fail("No boundary classifer has passed the extraction");
		} catch (Exception e) {
			
		}
	}

}
