package weka.classifiers.functions.explicitboundaries.gemoetry;

import java.io.Serializable;

import weka.core.DebugSetter;
import weka.core.Debuggable;
import weka.core.Instance;
import weka.tools.InstancesTools;

public class GrammShmidtOrthonormal implements Serializable, Debuggable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -712090121362205074L;
	
	private DotProduct dotProd;
	
	private boolean debug=true;


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
				tmpInstance = InstancesGeometricOperations.subtractInstances(tmpInstance, this.dotProd.projection(base[i], newBase[j]),debug); 
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


	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return this.debug;
	}


	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		DebugSetter.setDebug(dotProd, debug);
	}
	
	
	
}
