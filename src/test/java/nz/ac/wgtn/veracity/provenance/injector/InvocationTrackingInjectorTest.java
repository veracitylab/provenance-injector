package nz.ac.wgtn.veracity.provenance.injector;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class InvocationTrackingInjectorTest {
    private static final String PREFIX = "nz/ac/wgtn/veracity/provenance/injector/sampleclasses/";
    private static final String CLASS_1_NAME = PREFIX + "SomeClass";
    private static final String CLASS_2_NAME = PREFIX + "SomeDatabaseClass";


    @BeforeClass
    public static void setUpAll() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        long process = runtime.getPid();

        try {
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(process));
            File agent = loadAgentJar();
            vm.loadAgent(agent.getAbsolutePath());

        } catch (AttachNotSupportedException ex) {
            System.err.printf("Error attaching to self, JVM option %s not present%n", "-Djdk.attach.allowAttachSelf=true");
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        } catch (AgentLoadException | AgentInitializationException ex) {
            System.err.println("Error loading agent");
            fail(ex.getMessage());
        }
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

            assertEquals(2, invocations.size());
            List<String> targetRepr = invocations.stream().map(Invocation::toJSON).filter(it -> it.equals(expectedJSON)).collect(Collectors.toList());
            assertEquals(1, targetRepr.size());
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

    private static File loadAgentJar() {
        File agent = new File("target/provenance-agent.jar");

        if (!agent.exists()) {
            throw new RuntimeException("Agent jar not detected");
        }

        return agent;
    }

}
