package nz.ac.wgtn.veracity.provenance.injector;


import nz.ac.wgtn.veracity.approv.jbind.ActivityMappingProvider;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass;
import nz.ac.wgtn.veracity.provenance.injector.tracker.InvocationTracker;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Collection;
import java.util.ServiceLoader;

import static org.junit.Assert.*;


public class InvocationTrackingInjectorTest {
    private static final String PREFIX = "nz/ac/wgtn/veracity/provenance/injector/sampleclasses/";
    private static final String CLASS_1_NAME = PREFIX + "SomeClass";
    private static final String CLASS_2_NAME = PREFIX + "SomeDatabaseClass";


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
        final String expectedJSON = "{\n" +
                "  \"caller\": \"nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass/someDatabaseMethod#()V\",\n" +
                "  \"invocation\": \"java.sql.DriverManager/getConnection#(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;\",\n" +
                "  \"activities\": [\n" +
                "    \"https://veracity.wgtn.ac.nz/app-provenance#DBAccess\"\n" +
                "  ]\n" +
                "}";


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
            assertEquals(expectedJSON, target.toJSON());
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

    @Test
    public void thisTestIsForTestingTheServiceLoaders() {
        ServiceLoader<ActivityMappingProvider> activityMappingsProviders = ServiceLoader.load(ActivityMappingProvider.class);
        System.out.println("Beans");
    }

}
