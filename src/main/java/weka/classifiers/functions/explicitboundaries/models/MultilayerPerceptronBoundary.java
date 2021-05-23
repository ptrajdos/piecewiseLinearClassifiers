/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import java.lang.reflect.Field;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.neural.NeuralConnection;
import weka.classifiers.functions.neural.NeuralNode;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DebugSetter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 2.2.1
 *
 */
public class MultilayerPerceptronBoundary extends MultilayerPerceptron implements ClassifierWithBoundaries {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3003132125128951174L;
	
	/**
	 * Only data header -- without instances
	 */
	protected Instances m_Data;
	
	protected DecisionBoundary boundary;
	
	protected MajorityPlaneBoundaryModel defaultPlaneModel = null;
	
	protected ZeroR defaultModel;
	
	

	/**
	 * 
	 */
	public MultilayerPerceptronBoundary() {
		super();
		this.defaultPlaneModel = new MajorityPlaneBoundaryModel();
	}
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.MultilayerPerceptron#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances i) throws Exception {
		if(!this.getDoNotCheckCapabilities())
			this.getCapabilities().testWithFail(i);
		//Only linear models are allowed
		this.setHiddenLayers("0");
		this.setNormalizeNumericClass(false);
		
		this.defaultPlaneModel.buildDefaultModelPlane(i);
		if(this.defaultPlaneModel.isUseDefault()) {
			this.defaultModel = new ZeroR();
			this.defaultModel.buildClassifier(i);
		}else 
			super.buildClassifier(i);
		
		
			
		
		
		this.m_Data = i;
		calcBoundary();
		
		/**
		 * Data header without instances
		 */
		this.m_Data = new Instances(i,0);
	}



	
	public void calcBoundary() throws Exception {
		
		int numAttrs = this.m_Data.numAttributes();
		int classAttrNum = this.m_Data.classIndex();
		
		this.defaultPlaneModel = new MajorityPlaneBoundaryModel();
		this.defaultPlaneModel.buildDefaultModelPlane(this.m_Data);
		if(this.defaultPlaneModel.isUseDefault()) {
			this.boundary = this.defaultPlaneModel.getPlaneModel();
			return;
		}
			
	
		
		
		
		Instance normalVec  = new DenseInstance(numAttrs);
		normalVec.setDataset(this.m_Data);
		
		 NeuralNode con;
		 con = (NeuralNode) this.getNeuralConnections()[0];
		 double[] weights = con.getWeights();
		 
		 double offset = weights[0];
		 
	
		 int weiCount=1;
		 
		 double[] nVrep = normalVec.toDoubleArray();
		 for(int a=0;a<numAttrs;a++) {
			 if(a== classAttrNum)
				 continue;
			 nVrep[a] = weights[weiCount++];
			 
		 }
		 normalVec = normalVec.copy(nVrep);
		 
		 DecisionBoundaryPlane boundary1 = new DecisionBoundaryPlane(normalVec.dataset(),0, 1);
		 boundary1.getDecisionPlane().setNormalVector(normalVec);
		 boundary1.getDecisionPlane().setOffset(offset);
		 
		this.boundary = boundary1;
	}
	
	protected NeuralConnection[] getNeuralConnections()throws Exception {
		Field f ;//; this.getClass().getDeclaredField("m_neuralNodes");
		f = Class.forName("weka.classifiers.functions.MultilayerPerceptron").getDeclaredField("m_neuralNodes");
		f.setAccessible(true);
		
		NeuralConnection[] connections = (NeuralConnection[]) f.get(this);
		
		return connections;
		
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.MultilayerPerceptron#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities base = super.getCapabilities();
		base.disableAll();
		base.enable(Capability.NUMERIC_ATTRIBUTES);
		base.enable(Capability.BINARY_CLASS);
		base.setMinimumNumberInstances(0);
		return base;
	}
	
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		return this.boundary;
	}
	
	
	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
		DebugSetter.setDebug(this.boundary, debug);
	}



	public static void main(String[] args) {
		runClassifier(new MultilayerPerceptronBoundary(), args);
	}



	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		if(this.defaultPlaneModel.isUseDefault() )
			return this.defaultModel.distributionForInstance(instance);
		
		double[] distribution;
		distribution  = new double[2];
		distribution[0] = 0.5*(this.boundary.getValue(instance) + 1.0);
		distribution[1] = 1 - distribution[0];
		
		
		return distribution;
	}

	

}
