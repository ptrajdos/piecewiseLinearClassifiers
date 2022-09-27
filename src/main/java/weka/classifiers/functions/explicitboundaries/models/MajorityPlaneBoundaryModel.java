/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import java.io.Serializable;

import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.core.DebugSetter;
import weka.core.Debuggable;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;

/**
 * @author pawel
 *
 */
public class MajorityPlaneBoundaryModel implements Serializable, Debuggable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 867284839821216595L;
	
	protected DecisionBoundaryPlane planeModel = null;
	
	protected boolean useDefault = false;
	
	protected boolean debug;

	/**
	 * 
	 */
	public MajorityPlaneBoundaryModel() {
		
	}
	
	/**
	 * Builds a default decision boundary if there are instances belonging to only one class.
	 * @param dataset
	 * @throws Exception 
	 */
	public  void buildDefaultModelPlane(Instances dataset) throws Exception {
		int[] counters = InstancesTools.getClassCounts(dataset);
		int targetIdx = counters[0]>counters[1]? 1:-1;
		
		this.useDefault = (counters[0]>0 & counters[1]>0)? false:true;
		
		int numAttrs = dataset.numAttributes();
		int classIdx = dataset.classIndex();
		
		this.useDefault = this.useDefault || (numAttrs==1 & classIdx>=0); // no instances or class only
		
		DecisionBoundaryPlane decBound = new DecisionBoundaryPlane(dataset, 0, 1);
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

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		DebugSetter.setDebug(this.planeModel, debug);
	}
	
	

}
