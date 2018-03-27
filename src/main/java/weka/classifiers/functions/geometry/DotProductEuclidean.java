/**
 * 
 */
package weka.classifiers.functions.geometry;

import java.io.Serializable;

import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel
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
	public double dotProduct(Instances dataSet, Instance inst1, Instance inst2) throws Exception {
		// TODO Auto-generated method stub
		if( (!dataSet.checkInstance(inst1)) | (!dataSet.checkInstance(inst2)))
			throw new Exception("Incompatible instances");
		
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

}
