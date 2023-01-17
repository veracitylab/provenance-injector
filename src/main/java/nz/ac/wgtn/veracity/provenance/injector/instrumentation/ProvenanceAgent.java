package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import net.bytebuddy.jar.asm.ClassReader;
import nz.ac.wgtn.veracity.approv.jbind.Execution;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProvenanceAgent {

    private static final Collection<String> ignorePackages = Set.of(
            "sun/",
            "java/lang/",
            "java/util/",
            "java/security/",
            "java/io/"
    );

    private ProvenanceAgent() {

    }

    /**
     * Allows installation of java agent from command line.
     *
     * @param agentArguments arguments for agent
     * @param instrumentation  instrumentation instance
     */
    public static void premain(String agentArguments,
                               Instrumentation instrumentation) {
            install(instrumentation);
    }

    /**
     * Allows installation of java agent with Attach API.
     *
     * @param agentArguments arguments for agent
     * @param instrumentation instrumentation instance
     */
    public static void agentmain(String agentArguments,
                                 Instrumentation instrumentation) {
        install(instrumentation);
    }

    /**
     * Installs the agent and sets up the provenance bindings.
     * @param instrumentation instrumentation instance
     */
    private static void install(Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                visitCallSites(className);
                return classfileBuffer;
            }
        }, true);
    }

    public static void visitCallSites(String className) {
        if(ignorePackages.stream().parallel().anyMatch(className::startsWith)) {
            return;
        }
        try {
            Collection<Execution> executions = new HashSet<>();
            CallSiteVisitor visitor = new CallSiteVisitor();
            new ClassReader(className).accept(visitor, 0);
            ActivityCollector.getInstance().evaluateExecutions(visitor.getCurrentMethod(), visitor.getExecutions());
        } catch (IOException ex) {
            System.err.printf("Unable to load class %s", className);
        }
    }

}