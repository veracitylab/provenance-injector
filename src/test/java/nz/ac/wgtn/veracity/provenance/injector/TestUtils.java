package nz.ac.wgtn.veracity.provenance.injector;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static org.junit.Assert.fail;

public class TestUtils {

    public static void attachAgentClass() {
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
    private static File loadAgentJar() {
        File agent = new File("target/provenance-agent.jar");

        if (!agent.exists()) {
            throw new RuntimeException("Agent jar not detected");
        }

        return agent;
    }
}
