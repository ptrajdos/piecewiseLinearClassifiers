/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.gemoetry;

import java.io.Serializable;
import java.util.Arrays;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.tools.InstancesTools;

/**
 * @author pawel trajdos
 * @since 0.1.0
 * @version 1.4.0
 *
 */
public class DotProductEuclidean implements DotProduct, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1477670834909210075L;

	/**
	 * 
	 */
	public DotProductEuclidean() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.functions.dotProducts.DotProductCalculator#dotProduct(weka.core.Instances, weka.core.Instance, weka.core.Instance)
	 */
	@Override
	public double dotProduct(Instance inst1, Instance inst2) throws Exception {
		
		InstancesTools.checkCompatibility(inst1, inst2);
		Instances dataSet = inst1.dataset();
		
		int numAttrs = dataSet.numAttributes();
		int classIdx = dataSet.classIndex();
		
		double[] inst1D = inst1.toDoubleArray();
		double[] inst2D = inst2.toDoubleArray();
		double product=0;
		for(int a =0;a<numAttrs;a++){
			if(a == classIdx)
				continue;
			
			if(!dataSet.attribute(a).isNumeric())
				continue;
			product += inst1D[a]*inst2D[a];
			
		}
		
		return product;
	}

	@Override
	public double norm(Instance vec) throws Exception {
		return Math.sqrt(this.dotProduct(vec, vec));
	}

	@Override
	public Instance projection(Instance inst1, Instance inst2) throws Exception {
		double[] tmp = inst2.toDoubleArray();
		double[] representation =  Arrays.copyOf(tmp, tmp.length) ;
		Instances dataSet = inst1.dataset();
		double weight=0;
		double i2Norm = this.norm(inst2);
		if(!Utils.eq(i2Norm, 0))
			weight= this.dotProduct(inst1, inst2)/i2Norm;
		
		int attNum = dataSet.numAttributes();
		int classIdx = dataSet.classIndex();
		for(int a=0;a<attNum;a++) {
			if(!dataSet.attribute(a).isNumeric() || a == classIdx)
				continue;
			representation[a]*=weight;
		}
		Instance result = inst2.copy(representation);
		return result;
	}

	@Override
	public Instance normalize(Instance inst) throws Exception {
		double norm = this.norm(inst);
		if(!Utils.eq(norm, 0))
			return InstancesGeometricOperations.scale(inst, 1.0/norm);
		
		return inst.copy(inst.toDoubleArray());
	}

}
