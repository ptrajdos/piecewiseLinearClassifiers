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
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel
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
		//Only linear models are allowed
		this.setHiddenLayers("0");
		super.buildClassifier(i);
		
		/**
		 * Data header without instances
		 */
		this.m_Data = new Instances(i,0);
	}



	@Override
	public DecisionBoundary getBoundary() throws Exception {
	
		int numAttrs = this.m_Data.numAttributes();
		Instance normalVec  = new DenseInstance(numAttrs);
		normalVec.setDataset(this.m_Data);
		
		 NeuralNode con;
		 con = (NeuralNode) this.getNeuralConnections()[0];
		 double[] weights = con.getWeights();
		 
		 double offset = weights[0];
		 
		 int conAttrs = con.getNumInputs();
		 for(int i=1;i<=conAttrs;i++) {
			 normalVec.setValue(i-1, weights[i]);
		 }
		 
		 DecisionBoundaryPlane boundary = new DecisionBoundaryPlane(normalVec.dataset(),0, 1);
		 boundary.getDecisionPlane().setNormalVector(normalVec);
		 boundary.getDecisionPlane().setOffset(offset);
		 
		return boundary;
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
		base.disable(Capability.NOMINAL_CLASS);
		base.disable(Capability.NUMERIC_CLASS);
		base.enable(Capability.BINARY_CLASS);
		return base;
	}
	
	

}
