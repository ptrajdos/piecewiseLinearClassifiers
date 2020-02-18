package weka.classifiers.functions.explicitboundaries;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import weka.classifiers.functions.BoundaryBasedClassifier;
import weka.classifiers.functions.explicitboundaries.models.FLDABoundary;
import weka.classifiers.functions.explicitboundaries.models.LogisticBoundary;
import weka.classifiers.functions.explicitboundaries.models.MultilayerPerceptronBoundary;
import weka.classifiers.functions.explicitboundaries.models.NearestCentroidBoundary;
import weka.classifiers.functions.explicitboundaries.models.SMOLinearBoundary;
import weka.core.Instance;
import weka.core.Instances;
import weka.tools.data.RandomDataGenerator;

@RunWith(Parameterized.class)
public class ClassifierWithBoundariesTest {

	@Parameters
    public static Collection<Object[]> classesAndMethods() throws NoSuchMethodException, SecurityException {
        List<Object[]> list = new ArrayList<Object[]>();
        list.add( generateParamsForClass(SMOLinearBoundary.class));
        list.add( generateParamsForClass(NearestCentroidBoundary.class));
        list.add( generateParamsForClass(MultilayerPerceptronBoundary.class));
        list.add( generateParamsForClass(FLDABoundary.class));
        list.add( generateParamsForClass(LogisticBoundary.class));
        list.add( generateParamsForClass(BoundaryBasedClassifier.class));
        return list;
    }
    
    public static Object[] generateParamsForClass(Class clazz) throws NoSuchMethodException, SecurityException {
    	return new Object[] {clazz, clazz.getMethod("getBoundary"),
    			clazz.getMethod("buildClassifier",Instances.class)};
    }
    
    private Class clazz;
    private Method method;
    private Method buildMethod;

    public ClassifierWithBoundariesTest(Class clazz, Method method, Method buildMethod) {
         this.clazz = clazz;
         this.method = method;
         this.buildMethod= buildMethod;
    }

    @Test
    public void testBehavior() {
        this.method.setAccessible(true);
        assertTrue("Build not null", this.buildMethod!=null);
        
        RandomDataGenerator gen = new RandomDataGenerator();
        gen.setNumNominalAttributes(0);
        Instances data = gen.generateData();
        Instance testInstance = data.get(0);
        	
        
        
        try {
        	Object learner = clazz.newInstance();
        	//this.buildMethod.in
        	this.buildMethod.invoke(learner, data);
			DecisionBoundary bound = (DecisionBoundary) this.method.invoke(learner, null);
			assertTrue("Not null boundary", bound!=null);
			double classVal  = bound.classify(testInstance);
			assertTrue("Class value", classVal<data.numClasses() & classVal>=0);
			assertTrue("Header compatibility", data.equalHeaders(bound.getDatasetHeader()));
			
			
		} catch (Exception e) {
			fail("An exception has been caught " + e.toString());
		}
		
    }

}
