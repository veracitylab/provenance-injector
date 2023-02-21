package nz.ac.wgtn.veracity.provenance.injector;

import java.io.File;

public class TestUtils {
    public static File loadAgentJar() {
        File agent = new File("target/provenance-agent.jar");

        if (!agent.exists()) {
            throw new RuntimeException("Agent jar not detected");
        }

        return agent;
    }
}
