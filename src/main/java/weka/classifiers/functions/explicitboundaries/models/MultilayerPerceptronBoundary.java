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
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DebugSetter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel trajdos
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

	/**
	 * 
	 */
	public MultilayerPerceptronBoundary() {
		super();
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
		
		if(numAttrs ==1 & classAttrNum>=0) {
			MajorityPlaneBoundaryModel majPlane = new MajorityPlaneBoundaryModel();
			majPlane.buildDefaultModelPlane(this.m_Data);
			this.boundary = majPlane.getPlaneModel();
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
		base.setMinimumNumberInstances(2);
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

}
