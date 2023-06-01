package nz.ac.wgtn.veracity.provenance.injector;


import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass;
import nz.ac.wgtn.veracity.provenance.injector.tracker.InvocationTracker;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Collection;

import static org.junit.Assert.*;


public class InvocationTrackingInjectorTest {
    @BeforeClass
    public static void setUpAll() {
        TestUtils.attachAgentClass();
    }

    @Before
    public void setUp() {
        InvocationTracker.getInstance().nuke();
    }

    @Test
    public void testMatchingActivityInvocationsAreTracked() {
        try {
            SomeDatabaseClass dbClass = new SomeDatabaseClass();
            dbClass.someDatabaseMethod();
        } catch (Exception ex) {
            System.err.println("Exception detected in subject execution");
            ex.printStackTrace();
        } finally {
            InvocationTracker tracker = InvocationTracker.getInstance();
            Collection<Invocation> invocations = tracker.getInvocations();

            assertEquals(1, invocations.size());
            Invocation target = invocations.iterator().next();
            assertNotNull(target);
        }
    }

    @Test
    public void testNoMatchingActivityInvocationsAreTracked() {
        try {
            SomeClass nonInterestingClass = new SomeClass();
            nonInterestingClass.doSomeThing();
        } catch (Exception e) {
            System.err.println("Exception detected in subject execution");
            e.printStackTrace();
        } finally {
            InvocationTracker tracker = InvocationTracker.getInstance();
            Collection<Invocation> invocations = tracker.getInvocations();
            assertEquals(0, invocations.size());
        }
    }

}
