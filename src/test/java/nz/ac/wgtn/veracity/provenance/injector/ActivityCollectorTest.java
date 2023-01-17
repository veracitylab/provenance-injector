package nz.ac.wgtn.veracity.provenance.injector;


import nz.ac.wgtn.veracity.approv.jbind.Execution;
import nz.ac.wgtn.veracity.provenance.injector.instrumentation.ActivityCollector;
import nz.ac.wgtn.veracity.provenance.injector.instrumentation.ProvenanceAgent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;


public class ActivityCollectorTest {
    private static final String PREFIX = "nz/ac/wgtn/veracity/provenance/injector/sampleclasses/";
    private static final String CLASS_1_NAME = PREFIX + "SomeClass";
    private static final String CLASS_2_NAME = PREFIX + "SomeDatabaseClass";


    @Before
    public void setUp() {
        ActivityCollector.getInstance().nuke();
    }

    @Test
    public void test_collect_executions_no_activity() {
        ActivityCollector collector = ActivityCollector.getInstance();
        ProvenanceAgent.visitCallSites(CLASS_1_NAME);

        String methodName = CLASS_1_NAME + "::somethingElse()V";

        Collection<Execution> executions = collector.executionsForMethod(methodName);
        Collection<URI> activities = collector.activitiesForMethod(methodName);

        Assert.assertEquals(2, executions.size());
        Assert.assertEquals(0, activities.size());

    }

    @Test
    public void test_collect_executions_with_activity() {
        ActivityCollector collector = ActivityCollector.getInstance();
        ProvenanceAgent.visitCallSites(CLASS_2_NAME);

        String methodName = CLASS_2_NAME + "::someDatabaseMethod()V";

        Collection<Execution> executions = collector.executionsForMethod(methodName);
        Collection<URI> activities = collector.activitiesForMethod(methodName);

        Assert.assertFalse(executions.isEmpty());
        Assert.assertTrue(activities.size() >= 1);
    }

    private static String stringConvert(String input) {
        return input.replace("/", ".");
    }
}
