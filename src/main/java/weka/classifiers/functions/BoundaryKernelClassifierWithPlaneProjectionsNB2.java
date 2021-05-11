/**
 * 
 */
package weka.classifiers.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryPlane;
import weka.classifiers.functions.explicitboundaries.gemoetry.Plane;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.UtilsPT;

/**
 * @author pawel trajdos
 * @version 2.3.0
 * @since 2.3.0
 *
 */
public class BoundaryKernelClassifierWithPlaneProjectionsNB2 extends BoundaryKernelClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7099889515910604001L;

	/**
	 * @param nearestCentroidBoundary
	 */
	public BoundaryKernelClassifierWithPlaneProjectionsNB2(ClassifierWithBoundaries nearestCentroidBoundary) {
		super(nearestCentroidBoundary);
	}

	/**
	 * 
	 */
	public BoundaryKernelClassifierWithPlaneProjectionsNB2() {
		super();
	}
	
	private class ChangedNB extends NaiveBayes{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7469411129800944858L;

		@Override
		public double[] distributionForInstance(Instance instance) throws Exception {
			if (m_UseDiscretization) {
			      m_Disc.input(instance);
			      instance = m_Disc.output();
			    }
			   //Prior probs ale delivered by BoundaryKernelClassifier
			    double[] probs = new double[m_NumClasses];
			    for (int j = 0; j < m_NumClasses; j++) {
			      probs[j] = 1.0;
			    }
			    Enumeration<Attribute> enumAtts = instance.enumerateAttributes();
			    int attIndex = 0;
			    while (enumAtts.hasMoreElements()) {
			      Attribute attribute = enumAtts.nextElement();
			      if (!instance.isMissing(attribute)) {
			        double temp, max = 0;
			        for (int j = 0; j < m_NumClasses; j++) {
			          temp = Math.max(1e-75, Math.pow(m_Distributions[attIndex][j]
			            .getProbability(instance.value(attribute)),
			            m_Instances.attribute(attIndex).weight()));
			          probs[j] *= temp;
			          if (probs[j] > max) {
			            max = probs[j];
			          }
			          if (Double.isNaN(probs[j])) {
			            throw new Exception("NaN returned from estimator for attribute "
			              + attribute.name() + ":\n"
			              + m_Distributions[attIndex][j].toString());
			          }
			        }
			        if ((max > 0) && (max < 1e-75)) { // Danger of probability underflow
			          for (int j = 0; j < m_NumClasses; j++) {
			            probs[j] *= 1e75;
			          }
			        }
			      }
			      attIndex++;
			    }

			    //No normalization -- normalization is performed after probability calculation
			    return probs;
		}
		
		
	}
	/**
	 * Naive Bayes estimator used
	 */
	protected ChangedNB nbEstim;
	
	private boolean useKernel=false;
	
	protected Instances getProjectedInstances(Instances data) throws Exception {
		Instances projectedInstances = new Instances(data, 0);
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		
		for(int i=0;i<this.numInstances;i++) {
			projectedInstances.add(plane.projectOnPlane(data.get(i)));
		}
		return projectedInstances;
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		super.buildClassifier(data);
		this.nbEstim = new ChangedNB();
		this.nbEstim.setUseKernelEstimator(this.useKernel);
		
		Instances projectedInstances = this.getProjectedInstances(data);
		this.nbEstim.buildClassifier(projectedInstances);
	}

	@Override
	protected double[] preparePreDistribution(Instance instance) throws Exception {
		double[] distribution = super.preparePreDistribution(instance); 
		DecisionBoundaryPlane boundary  = (DecisionBoundaryPlane) this.boundClassRef.getBoundary();
		Plane plane = boundary.getDecisionPlane();
		Instance tmpInstance = plane.projectOnPlane(instance);
		
		
		
		double[] nbDistribution = this.nbEstim.distributionForInstance(tmpInstance);
		double distrSum=0;
		for(int c=0;c<nbDistribution.length;c++)
			if(this.numInsancesPerClass[c]>0) {
				distribution[c]*=nbDistribution[c];
				distrSum+= distribution[c];
			}
		
		if(Utils.gr(distrSum, 0))
			Utils.normalize(distribution,distrSum);
		
		return distribution;
	}

	/**
	 * @return the useKernel
	 */
	public boolean isUseKernel() {
		return this.useKernel;
	}

	/**
	 * @param useKernel the useKernel to set
	 */
	public void setUseKernel(boolean useKernel) {
		this.useKernel = useKernel;
	}
	
	public String useKernelTipText() {
		return "Indicates whether kernel estimation is ised";
	}

	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		newVector.addElement(new Option(
			      "\tUse kernel estimator "+
		          "(default: "+ false +  ").\n",
			      "UKE", 0, "-UKE"));		
		newVector.addAll(Collections.list(super.listOptions()));
	    
		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		this.setUseKernel(Utils.getFlag("UKE", options));
		super.setOptions(options);
	}

	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		if(this.isUseKernel())
			options.add("-UKE");
		
		Collections.addAll(options, super.getOptions());
		return options.toArray(new String[0]);
	}

}
