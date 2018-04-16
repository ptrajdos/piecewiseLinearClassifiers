/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.gemoetry;

import java.io.Serializable;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author pawel
 *
 */
public class Plane implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8391921672226254124L;

	/**
	 * Header of the dataset
	 */
	protected Instances dataHeader = null;
	
	/**
	 * Normal vector of the plane.
	 * Instance is a vector in the data-space.
	 */
	protected Instance normalVector = null;
	
	/**
	 * Offset of the plane
	 */
	protected double offset = 0;
	
	/**
	 * The dot product defined in the space
	 */
	protected DotProduct dotProduct = new DotProductEuclidean();
	
	
	
	
	public Plane(Instances dataSpace) {
		this.dataHeader = new Instances(dataSpace, 0);
		this.normalVector = new DenseInstance(dataSpace.numAttributes());
		this.normalVector.setDataset(dataSpace);
		
		int classIdx = dataSpace.classIndex();
		int numAttrs = dataSpace.numAttributes();
		
		// Produce unit normal vector
		this.normalVector.setClassMissing();
		for(int a=0;a<numAttrs;a++){
			if(a == classIdx)
				continue;
			
			if(!dataSpace.attribute(a).isNumeric())
				continue;
			this.normalVector.setValue(a, 1);
			
		}
		
	}
	
	/**
	 * Calculate the distance of the vector to the plane
	 * @param vec for which the distance is calculated
	 * @return distance
	 * @throws Exception if instance is incompatible
	 */
	public double distanceToPlane(Instance vec) throws Exception{
		double result =0;
		result = Math.abs(this.dotProduct.dotProduct(this.dataHeader, this.normalVector, vec) + this.getOffset());
		result/= this.dotProduct.norm(this.dataHeader, this.normalVector);
		return result;
	}
	
	/**
	 * Dtermines on which side of the plane the vector is.
	 * The side is determined by the sign of the value 
	 * @param vec
	 * @return
	 * @throws Exception
	 */
	public double sideOfThePlane(Instance vec) throws Exception{
		double result =0 ;
		result = this.dotProduct.dotProduct(this.dataHeader, this.normalVector, vec) + this.offset;
		result/= this.dotProduct.norm(this.dataHeader, this.normalVector);
		return result;
	}




	/**
	 * @return the normalVector
	 */
	public Instance getNormalVector() {
		return this.normalVector;
	}




	/**
	 * @param normalVector the normalVector to set
	 * @throws Exception When the normal vector is incompatible with the dataset
	 */
	public void setNormalVector(Instance normalVector) throws Exception {
		if(!this.dataHeader.checkInstance(normalVector))
			throw new Exception("Invalid Normal Vector");
		this.normalVector = normalVector;
	}




	/**
	 * @return the offset
	 */
	public double getOffset() {
		return this.offset;
	}




	/**
	 * @param offset the offset to set
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}




	/**
	 * @return the dotProduct
	 */
	public DotProduct getDotProduct() {
		return this.dotProduct;
	}




	/**
	 * @param dotProduct the dotProduct to set
	 */
	public void setDotProduct(DotProduct dotProduct) {
		this.dotProduct = dotProduct;
	}

	/**
	 * @return the dataHeader
	 */
	public Instances getDataHeader() {
		return this.dataHeader;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer description  =  new StringBuffer();
		description.append("Plane:\nNormal Vector: " + this.normalVector +"\nOffset: " + this.offset);
		
		return description.toString();
	}
	
	
	
	
	

}
