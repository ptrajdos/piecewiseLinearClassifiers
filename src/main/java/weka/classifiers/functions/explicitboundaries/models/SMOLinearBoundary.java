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
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel
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

	/**
	 * 
	 */
	public SMOLinearBoundary() {
		super();
		PolyKernel linKernel  = new PolyKernel();
		linKernel.setExponent(1.0);
		this.setKernel(linKernel);
		
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		//There is only one model in the binary classifier
		SMO.BinarySMO binaryModel = this.m_classifiers[0][1];
		
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
		
		Instance normalVector = new DenseInstance(numAttrs);
		normalVector.setDataset(dataHeader);
		
		for(int a =0;a<sparseIndices.length;a++){
			if(sparseIndices[a] == classIdx)
				continue;
			
			normalVector.setValue(a, -sparseWeights[a]);
		}
		
		Plane plane = new Plane(dataHeader);
		plane.setNormalVector(normalVector);
		plane.setOffset(offset);
			
		DecisionBoundaryPlane planeBoundary = new DecisionBoundaryPlane(dataHeader, 0, 1, plane);
		
		
		return planeBoundary;
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances insts) throws Exception {
		super.buildClassifier(insts);
		this.dataHeader = new Instances(insts, 0);
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
		base.disable(Capability.NOMINAL_CLASS);
		base.enable(Capability.BINARY_CLASS);
		return base;
	}

}
