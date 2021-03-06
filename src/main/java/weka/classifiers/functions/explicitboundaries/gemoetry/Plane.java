/**
 * 
 */
package weka.classifiers.functions.explicitboundaries.gemoetry;

import java.io.Serializable;

import weka.core.DebugSetter;
import weka.core.Debuggable;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * @author pawel
 * @since 0.1.0
 * @version 2.1.3
 */
public class Plane implements Serializable, Debuggable {

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
	 * Orthonormal base of the plane
	 */
	protected Instance[] planeBase;
	
	/**
	 * Offset of the plane
	 */
	protected double offset = 0;
	
	/**
	 * The dot product defined in the space
	 */
	protected DotProduct dotProduct ;
	
	protected boolean normalizeDistance=false;
	
	protected GrammShmidtOrthonormal gsOrth;
	
	protected boolean debug=false;
	
	
	
	
	public Plane(Instances dataSpace) {
		this.dotProduct = new DotProductEuclidean();
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
		this.gsOrth = new GrammShmidtOrthonormal();
		
		try {
			this.createPlaneBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		result = Math.abs(this.dotProduct.dotProduct(this.normalVector, vec) + this.getOffset());
		double normalVecNorm =this.dotProduct.norm(this.normalVector);
		if(!Utils.eq(normalVecNorm, 0)) {
			result/= normalVecNorm;
		}
		 
		result = this.distanceNormalizer(result);
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
		result = this.dotProduct.dotProduct(this.normalVector, vec) + this.offset;
		double normalVecNorm =this.dotProduct.norm(this.normalVector);
		if(!Utils.eq(normalVecNorm, 0)) {
			result/= normalVecNorm;
		}
		result = this.distanceNormalizer(result);
		return result;
	}

	
	private void createPlaneBase() throws Exception {
		Instance[] base = createBase();//TODO error is here
		this.planeBase = this.gsOrth.createOrthonormalBase(base);
	}
	
	private Instance[] createBase() {
		int numAttrs = numericAttribsNumber();
		if(numAttrs<=1) {
			return new Instance[] {this.normalVector.copy(this.normalVector.toDoubleArray())};
		}
		Instance[] base = new Instance[numAttrs-1];
		
		int classIdx = this.dataHeader.classIndex();
		double[] nVecRep = this.normalVector.toDoubleArray();
		
		int coefIdx = getNonzeroNormVecPosIdx();
		if(coefIdx<0) {
			return new Instance[] {this.normalVector.copy(this.normalVector.toDoubleArray())};
		}
		
		double coef = nVecRep[coefIdx];
		double[] instRep = null;
		
		int baseCount =0;
		for(int a=0;a<nVecRep.length;a++) {
			instRep = new double[nVecRep.length];
			if(coefIdx == a || a== classIdx)
				continue;
			
			if(!this.dataHeader.attribute(a).isNumeric()) {
				instRep[a] = nVecRep[a];
				continue;
			}
			
			instRep[a] =1.0;
			instRep[coefIdx] = -(nVecRep[a] + this.offset)/coef;
			base[baseCount++] = this.normalVector.copy(instRep);
		}
		return base;
	}
	
	/**
	 * Find first nonzero position of the normal vector.
	 * If none has been found, returns -1
	 * @return 
	 */
	private int getNonzeroNormVecPosIdx() {
		int numAttrs = this.dataHeader.numAttributes();
		int classIdx = this.dataHeader.classIndex();
		double[] nVecRep = this.normalVector.toDoubleArray();
		
		
		for(int a =0;a<numAttrs;a++) {
			if(a==classIdx)
				continue;
			if(!Utils.eq(nVecRep[a], 0)) {
				return a;
			}
		}
		return -1;
	}
	
	public Instance projectOnPlane(Instance inst)throws Exception {
		
		Instance projection =null;
		Instance tmp;
		projection = this.dotProduct.projection(inst, this.planeBase[0]);
		for(int i=1;i<this.planeBase.length;i++) {
			tmp = this.dotProduct.projection(inst, this.planeBase[i]);
			InstancesGeometricOperations.addInstances(projection, tmp);
		}
	
		InstancesGeometricOperations.scale(projection, 1.0/this.planeBase.length);
			
		
		return projection;
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
		
		this.createPlaneBase();
	}


	protected double distanceNormalizer(double distance) {
		double result = distance;
		if(!this.normalizeDistance)
			return result;
		
			
			
			double normalizer = this.numericAttribsNumber();
			result/= Math.sqrt(normalizer);
		
				
		return result;
	}
	
	protected int numericAttribsNumber() {
		int numAttributes = 0;
		int initialAttributesNum = this.normalVector.numAttributes();
		Instances dataset = this.normalVector.dataset();
		int classIdx  = dataset.classIndex();
		for(int i =0;i<initialAttributesNum;i++) {
			if(i==classIdx)
				continue;
			if(!dataset.attribute(i).isNumeric())
				continue;
			
			numAttributes++;
		}
		return numAttributes;
		
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
		this.gsOrth.setDotProd(dotProduct);
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

	/**
	 * @return the normalizeDistance
	 */
	public boolean isNormalizeDistance() {
		return this.normalizeDistance;
	}

	/**
	 * @param normalizeDistance the normalizeDistance to set
	 */
	public void setNormalizeDistance(boolean normalizeDistance) {
		this.normalizeDistance = normalizeDistance;
	}

	@Override
	public boolean isDebug() {
		return this.debug;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
		DebugSetter.setDebug(dotProduct, debug);
		DebugSetter.setDebug(gsOrth, debug);
		
	}

}
