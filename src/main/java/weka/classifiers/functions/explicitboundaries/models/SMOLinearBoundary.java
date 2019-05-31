/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import java.lang.reflect.Field;

import weka.classifiers.functions.SMO;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DebugSetter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.1.1
 *
 */
public class SMOLinearBoundary extends SMO implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2879314365187089955L;
	
	/**
	 * Header of the dataset
	 */
	protected Instances dataHeader = null;
	
	protected MajorityPlaneBoundaryModel defaultModel = null;
	
	protected DecisionBoundaryPlane boundary;
	

	/**
	 * 
	 */
	public SMOLinearBoundary() {
		super();
		PolyKernel linKernel  = new PolyKernel();
		linKernel.setExponent(1.0);
		this.setKernel(linKernel);
		this.defaultModel = new MajorityPlaneBoundaryModel();
		
		
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultModel.isUseDefault()) {
			return this.defaultModel.getPlaneModel();
		}
		return this.boundary;
	}
	
	protected void calculateBoundary(Instances insts)throws Exception{
		SMO.BinarySMO binaryModel = this.m_classifiers[0][1];
		
		@SuppressWarnings("rawtypes")
		Class modClass = binaryModel.getClass();
		Field m_bF = modClass.getDeclaredField("m_b");
		m_bF.setAccessible(true);
		double offset =  m_bF.getDouble(binaryModel);
		
		Field sparseWeightsF = modClass.getDeclaredField("m_sparseWeights");
		sparseWeightsF.setAccessible(true);
		double[] sparseWeights = (double[]) sparseWeightsF.get(binaryModel);
		Field sparseIndicesF = modClass.getDeclaredField("m_sparseIndices");
		sparseIndicesF.setAccessible(true);
		int[] sparseIndices = (int[]) sparseIndicesF.get(binaryModel);
		int classIdx = this.m_classIndex;
		int numAttrs =this.dataHeader.numAttributes();
		
		double[] attValues = new double[numAttrs];
		
		
	
		for(int a =0;a<sparseIndices.length;a++){
			if(sparseIndices[a] == classIdx)
				continue;
			
			attValues[sparseIndices[a]] = -sparseWeights[a];
			//normalVector.setValue(sparseIndices[a], -sparseWeights[a]);
	
		}
		attValues[classIdx]= Double.NaN;
		
		Instance normalVector = insts.get(0).copy(attValues);
		normalVector.setDataset(this.dataHeader);
		
		
		
		Plane plane = new Plane(this.dataHeader);
		plane.setNormalVector(normalVector);
		plane.setOffset(offset);
			
		this.boundary = new DecisionBoundaryPlane(this.dataHeader, 0, 1, plane);
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances insts) throws Exception {
		if(!this.getDoNotCheckCapabilities())
			this.getCapabilities().testWithFail(insts);
		/**
		 * Set no data normalization 
		 */
		this.setFilterType(new SelectedTag(SMO.FILTER_NONE, SMO.TAGS_FILTER));
		super.buildClassifier(insts);
		this.dataHeader = new Instances(insts, 0);
		this.defaultModel.buildDefaultModelPlane(insts);
		this.calculateBoundary(insts);
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runClassifier(new SMOLinearBoundary(), args);

	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities base = super.getCapabilities();
		base.disableAll();
		base.enable(Capability.NUMERIC_ATTRIBUTES);
		base.enable(Capability.BINARY_CLASS);
		return base;
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
		DebugSetter.setDebug(this.boundary, debug);
		DebugSetter.setDebug(this.defaultModel, debug);
	}
	

}
