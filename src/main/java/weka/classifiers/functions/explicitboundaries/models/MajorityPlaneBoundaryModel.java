/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import java.io.Serializable;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;

/**
 * @author pawel
 *
 */
public class MajorityPlaneBoundaryModel implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 867284839821216595L;
	
	protected DecisionBoundaryPlane planeModel = null;
	
	protected boolean useDefault = false;

	/**
	 * 
	 */
	public MajorityPlaneBoundaryModel() {
		
	}
	
	/**
	 * Builds a default decision boundary if there are instances belonging to only one class.
	 * @param dataset
	 * @return
	 * @throws Exception 
	 */
	public  void buildDefaultModelPlane(Instances dataset) throws Exception {
		int[] counters = InstancesTools.getClassCounts(dataset);
		int targetIdx = counters[0]>counters[1]? 1:-1;
		
		this.useDefault = (counters[0]>0 & counters[1]>0)? false:true;
		
		DecisionBoundaryPlane decBound = new DecisionBoundaryPlane(dataset, 0, 1);
		int numAttrs = dataset.numAttributes();
		Instance zeroVec = new DenseInstance(numAttrs);
		for(int a=0;a<numAttrs;a++) {
			zeroVec.setValue(a, 0);
		}
		zeroVec.setDataset(dataset);
		
		decBound.getDecisionPlane().setNormalVector(zeroVec);
		decBound.getDecisionPlane().setOffset(targetIdx);
		
		this.planeModel = decBound;
	}

	/**
	 * @return the planeModel
	 */
	public DecisionBoundaryPlane getPlaneModel() {
		return this.planeModel;
	}

	/**
	 * @return the useDefault
	 */
	public boolean isUseDefault() {
		return this.useDefault;
	}
	
	

}
