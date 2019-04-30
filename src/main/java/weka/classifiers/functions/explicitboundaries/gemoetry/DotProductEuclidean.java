/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.gemoetry;

import java.io.Serializable;
import java.util.Arrays;

import weka.core.Instance;
import weka.core.Instances;
import weka.tools.InstancesTools;

/**
 * @author pawel trajdos
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
		
		InstancesTools.checkCompatibility(dataSet, inst1);
		InstancesTools.checkCompatibility(dataSet, inst2);
		
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
	public double norm(Instances dataSet, Instance vec) throws Exception {
		return Math.sqrt(this.dotProduct(dataSet, vec, vec));
	}

	@Override
	public Instance projection(Instances dataSet, Instance inst1, Instance inst2) throws Exception {
		double[] tmp = inst2.toDoubleArray();
		double[] representation =  Arrays.copyOf(tmp, tmp.length) ;
		
		double weight = this.dotProduct(dataSet, inst1, inst2)/this.norm(dataSet, inst2);
		
		int attNum = dataSet.numAttributes();
		for(int a=0;a<attNum;a++) {
			if(!dataSet.attribute(a).isNumeric())
				continue;
			representation[a]*=weight;
		}
		Instance result = inst2.copy(representation);
		return result;
	}

}
