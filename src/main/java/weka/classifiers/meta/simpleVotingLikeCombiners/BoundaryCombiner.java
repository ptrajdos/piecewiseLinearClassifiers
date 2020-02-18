/**
 * 
 */
package weka.classifiers.meta.simpleVotingLikeCombiners;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.IteratedSingleClassifierEnhancer;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.DecisionBoundaryCombiner;
import weka.classifiers.functions.explicitboundaries.combiners.PotentialFunctionCombiner;
import weka.classifiers.meta.tools.CommitteeExtractor;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.UtilsPT;

/**
 * @author pawel
 *
 */
public class BoundaryCombiner extends OutputCombinerBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7699187642315489209L;
	
	/**
	 * Combiner for boundaries
	 */
	protected DecisionBoundaryCombiner boundaryCombiner = null; 
	
	protected Instances dataHeader = null;

	/**
	 * 
	 */
	public BoundaryCombiner() {
		this.boundaryCombiner = new PotentialFunctionCombiner();
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.meta.customizableBagging.OutputCombiner#getClass(weka.classifiers.IteratedSingleClassifierEnhancer, weka.core.Instance)
	 */
	@Override
	public double getClass(IteratedSingleClassifierEnhancer arg0, Instance arg1) throws Exception {
		
		this.extractBoundaries(arg0, arg1);
		
		return this.boundaryCombiner.getClass(arg1);
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.meta.customizableBagging.OutputCombiner#getDistributionForInstance(weka.classifiers.IteratedSingleClassifierEnhancer, weka.core.Instance)
	 */
	@Override
	public double[] getDistributionForInstance(IteratedSingleClassifierEnhancer arg0, Instance arg1) throws Exception {
		int classIdx  = (int)this.getClass(arg0, arg1);
		int numClasses = this.dataHeader.numClasses();
		double[] result = new double[numClasses];
		result[classIdx] = 1.0;
		return result;
	}
	
	protected void extractBoundaries(IteratedSingleClassifierEnhancer arg0, Instance arg1) throws Exception{
		Classifier[] committee = CommitteeExtractor.getCommittee(arg0);
		List<DecisionBoundary> boundaryList = new LinkedList<DecisionBoundary>();
		for(int i =0; i<committee.length;i++){
			
			if( ! (committee[i] instanceof ClassifierWithBoundaries))
				throw new Exception("One of the committee members is not an instance of " + ClassifierWithBoundaries.class.toGenericString() );
			
			boundaryList.add( ((ClassifierWithBoundaries)committee[i]).getBoundary());
		}
		DecisionBoundaries boundaries = new DecisionBoundaries(boundaryList);
		this.dataHeader = boundaryList.get(0).getDatasetHeader();
		this.boundaryCombiner.setBoundaries(boundaries);
		
	}
	
	protected void extractBoundaries(MultipleClassifiersCombiner arg0, Instance arg1) throws Exception{
		Classifier[] committee = CommitteeExtractor.getCommittee(arg0);
		List<DecisionBoundary> boundaryList = new LinkedList<DecisionBoundary>();
		for(int i =0; i<committee.length;i++){
			
			if( ! (committee[i] instanceof ClassifierWithBoundaries))
				throw new Exception("One of the committee members is not an instance of " + ClassifierWithBoundaries.class.toGenericString() );
			
			boundaryList.add( ((ClassifierWithBoundaries)committee[i]).getBoundary());
		}
		DecisionBoundaries boundaries = new DecisionBoundaries(boundaryList);
		this.dataHeader = boundaryList.get(0).getDatasetHeader();
		this.boundaryCombiner.setBoundaries(boundaries);
		
	}

	/* (non-Javadoc)
	 * @see weka.core.RevisionHandler#getRevision()
	 */
	@Override
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1$");
	}

	/* (non-Javadoc)
	 * @see weka.core.OptionHandler#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		 newVector.addElement(new Option(
			      "\t Class distribution-combining-object to use "+
		          "(default:"+PotentialFunctionCombiner.class.toGenericString()  +" ).\n",
			      "BC", 0, "-BC"));
		    
		return newVector.elements();
	}

	/* (non-Javadoc)
	 * @see weka.core.OptionHandler#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		
		this.setBoundaryCombiner((DecisionBoundaryCombiner) UtilsPT.parseObjectOptions(options, "BC", new PotentialFunctionCombiner(), DecisionBoundaryCombiner.class));
		
	}

	/* (non-Javadoc)
	 * @see weka.core.OptionHandler#getOptions()
	 */
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
	    options.add("-BC");
	    options.add(UtilsPT.getClassAndOptions(this.getBoundaryCombiner()));
	    
	    return options.toArray(new String[0]);
	}

	/**
	 * @return the boundaryCombiner
	 */
	public DecisionBoundaryCombiner getBoundaryCombiner() {
		return this.boundaryCombiner;
	}
	
	public String boundaryCombinerTipText(){
		return "Boundary Combining Object";
	}

	/**
	 * @param boundaryCombiner the boundaryCombiner to set
	 */
	public void setBoundaryCombiner(DecisionBoundaryCombiner boundaryCombiner) {
		this.boundaryCombiner = boundaryCombiner;
	}

	@Override
	public double getClass(MultipleClassifiersCombiner arg0, Instance arg1) throws Exception {
		this.extractBoundaries(arg0, arg1);
		
		return this.boundaryCombiner.getClass(arg1);
	}

	@Override
	public double[] getDistributionForInstance(MultipleClassifiersCombiner arg0, Instance arg1) throws Exception {
		int classIdx  = (int)this.getClass(arg0, arg1);
		int numClasses = this.dataHeader.numClasses();
		double[] result = new double[numClasses];
		result[classIdx] = 1.0;
		return result;
	}

	
}
