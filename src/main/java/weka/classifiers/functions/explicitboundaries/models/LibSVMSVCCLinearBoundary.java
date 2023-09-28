/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.models;

import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.DebugSetter;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Capabilities.Capability;

/**
 * Class 
 * @author pawel trajdos
 * @since 2.5.0
 * @version 2.5.0
 * 
 *
 */
public class LibSVMSVCCLinearBoundary extends LibSVM implements ClassifierWithBoundaries {

	private static final long serialVersionUID = 1349179387758340005L;
	
	/**
	 * Header of the dataset
	 */
	protected Instances dataHeader = null;
	
	protected MajorityPlaneBoundaryModel defaultModel = null;
	
	protected DecisionBoundaryPlane boundary;
	
	protected ZeroR zeroModel;
	
	/**TODO
	 * Calculate boundary and use it for prediction
	 */

	/**
	 * 
	 */
	public LibSVMSVCCLinearBoundary() {
		super();
		this.defaultModel = new MajorityPlaneBoundaryModel();
		
		this.setNormalize(false);
		this.setSVMType(null);
		this.setKernelType(null);
	}

	@Override
	public DecisionBoundary getBoundary() throws Exception {
		if(this.defaultModel.isUseDefault()) {
			return this.defaultModel.getPlaneModel();
		}
		return this.boundary;
	}
	
	protected void calculateBoundary(Instances insts)throws Exception{
		
	
		int nAttribs = insts.numAttributes();
		
		
		int start0 = 0;
		int start1 = this.m_Model.nSV[0];
		
		int c0 = this.m_Model.nSV[0];
		int c1 = this.m_Model.nSV[1];
		
		double[] coeff = this.m_Model.sv_coef[0];
		
		double[] normal_vec_node = new double[nAttribs];
		
		int sign = ( (int)(insts.get(0).classValue()) == 0)? 1:-1;
		
		
		for(int k=0;k<c0;k++) {
			int nSvAttribs = this.m_Model.SV[start0+k].length;
			for(int a=0;a<nSvAttribs;a++) {
				int attIdx = this.m_Model.SV[start0+k][a].index;
				
				normal_vec_node[attIdx-1]+= sign * coeff[start0+k] * this.m_Model.SV[start0+k][a].value;
			}
		}
		
		for(int k=0;k<c1;k++) {
			int nSvAttribs = this.m_Model.SV[start1+k].length;
			for(int a=0;a<nSvAttribs;a++) {
				int attIdx = this.m_Model.SV[start1+k][a].index;
				
				normal_vec_node[attIdx-1]+= sign * coeff[start1+k] * this.m_Model.SV[start1+k][a].value;
			}
		}
		
		
		
		int classIdx = insts.classIndex();
		
		normal_vec_node[classIdx] = Double.NaN;
		
		Instance normalVector = insts.get(0).copy(normal_vec_node);
		normalVector.setDataset(this.dataHeader);
		
		Plane plane = new Plane(this.dataHeader);
		plane.setNormalVector(normalVector);
		plane.setOffset(-sign*this.m_Model.rho[0]);
		
		
		this.boundary = new DecisionBoundaryPlane(this.dataHeader, 0, 1, plane);
		
		return;
		
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.SMO#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances insts) throws Exception {
		if(!this.getDoNotCheckCapabilities())
			this.getCapabilities().testWithFail(insts);
		
		
		this.zeroModel = null;
		
		this.defaultModel.buildDefaultModelPlane(insts);
		
		this.dataHeader = new Instances(insts, 0);
		
		if(this.defaultModel.isUseDefault()) {
			this.zeroModel = new ZeroR();
			this.zeroModel.buildClassifier(insts);
			return;
		}
		
		super.buildClassifier(insts);
		
		this.calculateBoundary(insts);
	}

	@Override
	public void setNormalize(boolean value) {
		//This is intentional. No normalization is allowed.
		super.setNormalize(false);
	}

	@Override
	public void setSVMType(SelectedTag value) {
		//Only SVM-C is allowed
		super.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
	}

	@Override
	public void setKernelType(SelectedTag value) {
		// Only Linear kernel is allowed
		super.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
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
		base.setMinimumNumberInstances(0);
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

	@Override
	public double[] distributionForInstance(Instance inst) throws Exception {
		if(this.defaultModel.isUseDefault())
			return this.zeroModel.distributionForInstance(inst);
		
		 if (!getDoNotReplaceMissingValues()) {
		      m_ReplaceMissingValues.input(inst);
		      m_ReplaceMissingValues.batchFinished();
		      inst = m_ReplaceMissingValues.output();
		    }

		    if (m_Filter != null) {
		      m_Filter.input(inst);
		      m_Filter.batchFinished();
		      inst = m_Filter.output();
		    }

		    m_NominalToBinary.input(inst);
		    m_NominalToBinary.batchFinished();
		    inst = m_NominalToBinary.output();
		
		double[] distribution;
		distribution  = new double[2];
		distribution[0] = 0.5*(this.boundary.getValue(inst) + 1.0);
		distribution[1] = 1 - distribution[0];
		
		
		return distribution;
	}
	
	
	
	
		
		

}
