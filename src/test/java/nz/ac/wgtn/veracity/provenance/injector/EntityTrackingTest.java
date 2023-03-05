package nz.ac.wgtn.veracity.provenance.injector;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static nz.ac.wgtn.veracity.provenance.injector.TestUtils.loadAgentJar;
import static org.junit.Assert.fail;

public class EntityTrackingTest {

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

    public void testTrackingEntityArgSingleInt() {
        SomeClass someClass = new SomeClass();
        int expectedVal = 2;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();

    }

    public void testTrackingEntityArgSingleFloat() {

    }

    public void testTrackingEntityArgSingleDouble() {

    }

    public void testTrackingEntityArgSingleObject() {

    }
}
