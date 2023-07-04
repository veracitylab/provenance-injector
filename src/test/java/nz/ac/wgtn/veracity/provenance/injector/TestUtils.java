package nz.ac.wgtn.veracity.provenance.injector;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.*;
import java.util.jar.*;

import static org.junit.Assert.fail;

public class TestUtils {

    private static final AtomicBoolean attached = new AtomicBoolean(false);

    public static void attachAgentClass() {
        if (attached.get()) {
            return;
        }

        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        long process = runtime.getPid();

        try {
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(process));
            File agent = loadAgentJar();
            vm.loadAgent(agent.getAbsolutePath());
            attached.set(true);

        } catch (AttachNotSupportedException ex) {
            System.err.printf("Error attaching to self, JVM option %s not present%n", "-Djdk.attach.allowAttachSelf=true");
            fail(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        } catch (AgentLoadException | AgentInitializationException ex) {
            System.err.println("Error loading agent");
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    private static File loadAgentJar() throws IOException {
        File agent = new File("target/provenance-agent.jar");

        if (!agent.exists()) {
            throw new RuntimeException("Agent jar not detected");
        }

        return agent;
    }

}
