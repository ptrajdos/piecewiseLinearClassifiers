package weka.classifiers.functions.explicitboundaries.gemoetry;

import java.io.Serializable;

import weka.core.Instance;
import weka.tools.InstancesTools;

public class GrammShmidtOrthonormal implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -712090121362205074L;
	
	private DotProduct dotProd;


	public GrammShmidtOrthonormal() {
		this.dotProd = new DotProductEuclidean();
	}

	
	public Instance[] createOrthonormalBase(Instance[] base)throws Exception {
	
		
		Instance[] newBase = new Instance[base.length];
		
		newBase[0] = this.dotProd.normalize(base[0]);
		Instance tmpInstance = null;
		for(int i=1;i<base.length;i++) {
			tmpInstance = InstancesTools.copyInstance(base[i]);
			for(int j=0;j<=i-1;j++) {
				tmpInstance = InstancesGeometricOperations.subtractInstances(tmpInstance, this.dotProd.projection(base[i], newBase[j])); 
			}
			newBase[i] = this.dotProd.normalize(tmpInstance);
		}
	
		return newBase;
	}


	/**
	 * @return the dotProd
	 */
	public DotProduct getDotProd() {
		return this.dotProd;
	}


	/**
	 * @param dotProd the dotProd to set
	 */
	public void setDotProd(DotProduct dotProd) {
		this.dotProd = dotProd;
	}
	
}
