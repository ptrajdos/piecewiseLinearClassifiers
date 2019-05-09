/**
 * 
 */
package weka.classifiers.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries;
import weka.classifiers.functions.explicitboundaries.DecisionBoundary;
import weka.classifiers.functions.explicitboundaries.models.FLDABoundary;
import weka.classifiers.functions.explicitboundaries.models.LinearRegressionBoundary;
import weka.classifiers.functions.explicitboundaries.models.LogisticBoundary;
import weka.classifiers.functions.explicitboundaries.models.MultilayerPerceptronBoundary;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.functions.explicitboundaries.models.SMOLinearBoundary;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.Utils;
import weka.tools.SerialCopier;

/**
 * @author pawel trajdos
 * @version 1.4.0
 *
 */
public class BoundaryBasedClassifier extends SingleClassifierEnhancer
		implements ClassifierWithBoundaries, Randomizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2999118309114988803L;
	
	/**
	 * Calibrator for calculating soft outputs using given boundaries;
	 */
	protected Classifier calibrator ;
	
	protected boolean useCalibrator = false;
	protected boolean calibratorLearned = false;

	/**
	 * The number of folds for fitting the calibrator
	 */
	protected int numFolds = 3;

	protected Instances dataHeader = null;
	
	protected int seed=0;
	
	protected ClassifierWithBoundaries tmpClassifier = null;
	/**
	 * 
	 */
	public BoundaryBasedClassifier(ClassifierWithBoundaries boundClass) {
		this.m_Classifier = boundClass;
		try {
			//TODO this fails under weka GUI call
			//ClassCastException only for NearestNeighbourBoundary Mahalanobis prototype
			this.tmpClassifier = (ClassifierWithBoundaries) SerialCopier.makeCopy(boundClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.calibrator = new Logistic();
	}
	public BoundaryBasedClassifier() {
		this(new NearestCentroidBoundary());
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		this.m_Classifier.buildClassifier(data);
		if(this.useCalibrator)
			this.buildCalibrator(data);
	}
	
	protected void buildCalibrator(Instances data)throws Exception{
		//TODO something goes wrong when the callibrator model is built
		//Incompatible instances
		int numInstances = data.numInstances();
		if(this.numFolds> numInstances)
			this.numFolds = numInstances;
		
		
		//prepare new attributes
		 ArrayList<Attribute> atts = new ArrayList<Attribute>(2);
	      atts.add(new Attribute("classifierPrediction"));
	      Attribute origClaaAttr = data.classAttribute();
	      atts.add(origClaaAttr);
	      //atts.add(new Attribute("class",null));
	      Instances transData = new Instances("data", atts, 0);
	      transData.setClassIndex(1);
	      this.dataHeader = transData;
	      
		
		if(this.numFolds<=0){
			//No crossvalidation -- use training set for validation
			double[] vals = new double[2];
			Instance tmpInstance = null;
			DecisionBoundary boundary = this.getBoundary();
			for(int i=0;i<numInstances;i++){
				tmpInstance = data.get(i);
				vals = new double[2];
				vals[1] = tmpInstance.classValue();
				vals[0] = boundary.getValue(tmpInstance);
				transData.add(new DenseInstance(tmpInstance.weight(), vals));
			}
			
		}else{
			//Perform crossvalidation
			Instances tmpData = new Instances(data);
			Random random = new Random(this.seed);
			tmpData.randomize(random);
			tmpData.stratify(this.numFolds);
			ClassifierWithBoundaries tmpModel;
			int numValInstances = 0;
			Instance tmpInstance = null;
			double[] vals  =new double[2];
			DecisionBoundary boundary = null;
			for(int f=0;f<this.numFolds;f++){
				Instances train = tmpData.trainCV(this.numFolds, f);
				Instances test = tmpData.testCV(this.numFolds, f);
				numValInstances = test.numInstances(); 
				tmpModel = (ClassifierWithBoundaries) SerialCopier.makeCopy(this.tmpClassifier);
				tmpModel.buildClassifier(train);
				boundary = tmpModel.getBoundary();
				for(int i=0;i<numValInstances;i++){
					tmpInstance = test.get(i);
					vals  =new double[2];
					vals[1] = tmpInstance.classValue();
					vals[0] = boundary.getValue(tmpInstance);
					transData.add(new DenseInstance(tmpInstance.weight(), vals));
				}
			}
			
			
			
		}
		this.calibratorLearned =true;
		this.calibrator.buildClassifier(transData);
	}

	

	/* (non-Javadoc)
	 * @see weka.classifiers.AbstractClassifier#distributionForInstance(weka.core.Instance)
	 */
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		
		if(this.useCalibrator & this.calibratorLearned){
		
		DecisionBoundary bound = ((ClassifierWithBoundaries) this.m_Classifier).getBoundary();
		double[] vals = new double[2];
		vals[0] = bound.getValue(instance);
		vals[1] = Utils.missingValue();
		Instance tmpInstance = new DenseInstance(1, vals);
		tmpInstance.setDataset(this.dataHeader);
		double[] res = this.calibrator.distributionForInstance(tmpInstance);
		return res;
		}
		
		return this.m_Classifier.distributionForInstance(instance);
		
	}
	/* (non-Javadoc)
	 * @see weka.classifiers.functions.explicitboundaries.ClassifierWithBoundaries#getBoundary()
	 */
	@Override
	public DecisionBoundary getBoundary() throws Exception {
		return ((ClassifierWithBoundaries)this.m_Classifier).getBoundary();
	}
	/**
	 * @return the calibrator
	 */
	public Classifier getCalibrator() {
		return this.calibrator;
	}
	/**
	 * @param calibrator the calibrator to set
	 */
	public void setCalibrator(Classifier calibrator) {
		this.calibrator = calibrator;
	}
	
	public String calibratorTipText() {
		return "Callibrator that is used";
	}
	
	/**
	 * @return the numFolds
	 */
	public int getNumFolds() {
		return this.numFolds;
	}
	/**
	 * @param numFolds the numFolds to set
	 */
	public void setNumFolds(int numFolds) {
		this.numFolds = numFolds;
	}
	public String numFoldsTipText() {
		return "The number of folds that are used to build the calibrator";
	}
	@Override
	public void setSeed(int seed) {
		this.seed = seed;
	}
	public String seedTipText() {
		return "Random seed to be used by the classifier";
	}
	@Override
	public int getSeed() {
		return this.seed;
	}
	/**
	 * @return the useCalibrator
	 */
	public boolean getUseCalibrator() {
		return this.useCalibrator;
	}
	/**
	 * @param useCalibrator the useCalibrator to set
	 */
	public void setUseCalibrator(boolean useCalibrator) {
		this.useCalibrator = useCalibrator;
	}
	
	public String useCalibratorTipText() {
		return "Determines whether the callibration is used";
	}

	public String globalInfo() {
		return "Class that allows using boundary based classifiers as normal classifiers"+
				"Boundary based predictions are transformed into response based ones";
	}
	
	
	
	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);
		
		 newVector.addElement(new Option(
			      "\tDetermines whether the callibrator is used "+
		          "(default: F).\n",
			      "CA", 0, "-CA"));
		 
		 newVector.addElement(new Option(
			      "\tThe Callibrator model to use "+
		          "(default: weka.classifiers.functions.Logistic.Logistic ).\n",
			      "CAM", 0, "-CAM"));
		
		 
		 newVector.addElement(new Option(
			      "\tThe number of crossvalidation folds for the callibrator "+
		          "(default: F).\n",
			      "CV", 0, "-CV"));
		 
		 
		 newVector.addAll(Collections.list(super.listOptions()));
		    
		return newVector.elements();
	}
	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		this.setUseCalibrator(Utils.getFlag("CA", options));
		
		String cvNumStr = Utils.getOption("CV", options);
		int cvNum =3;
		try {
		cvNum = Integer.parseInt(cvNumStr);
		}catch(Exception e) {
			cvNum =3;
		}
		
		String calibratorString = Utils.getOption("CAM", options);
	    if(calibratorString.length() != 0) {
	      String calibratorClassSpec[] = Utils.splitOptions(calibratorString);
	      if(calibratorClassSpec.length == 0) { 
	        throw new Exception("Invalid Calibrator " +
	                            "specification string."); 
	      }
	      String className = calibratorClassSpec[0];
	      calibratorClassSpec[0] = "";

	      this.setCalibrator( (Classifier)
	                  Utils.forName( Classifier.class, 
	                                 className, 
	                                 calibratorClassSpec)
	                                        );
	    }
	    else 
	      this.setCalibrator(new Logistic());

		
		super.setOptions(options);
	}
	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#getOptions()
	 */
	@Override
	public String[] getOptions() {
		Vector<String> options = new Vector<String>();
		
		if(this.getUseCalibrator())
			options.add("-CA");
		
		options.add("-CV");
		options.add(""+this.getNumFolds());
		
		options.add("-CAM");
		String calibratorOptions = this.calibrator instanceof OptionHandler? " "+Utils.joinOptions( ((OptionHandler) this.calibrator).getOptions()): " ";
	    options.add(this.calibrator.getClass().getName()+calibratorOptions);
	    
		
		
		
		Collections.addAll(options, super.getOptions());
	    return options.toArray(new String[0]);
	}
	
	
	/* (non-Javadoc)
	 * @see weka.classifiers.SingleClassifierEnhancer#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities caps =super.getCapabilities();
		caps.disableAll();
		caps.enable(Capability.NUMERIC_ATTRIBUTES);
		caps.enable(Capability.BINARY_CLASS);

		return caps;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	public static void main(String[] args) {
		runClassifier(new BoundaryBasedClassifier(), args);
	}

}
