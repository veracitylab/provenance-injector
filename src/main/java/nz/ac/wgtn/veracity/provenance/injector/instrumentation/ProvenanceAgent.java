package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.tracker.GlobalProvenanceTracker;
import nz.ac.wgtn.veracity.provenance.injector.tracker.ProvenanceTracker;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Set;

public class ProvenanceAgent {

    private static final Collection<String> ignorePackages = Set.of(
            "sun/",
            "java/lang/",
            "java/util/",
            "java/security/",
            "java/nio"
//            "java/sql"
    );

    private static ProvenanceTracker<Invocation> tracker;

    private ProvenanceAgent() {

    }

    /**
     * Allows installation of java agent from command line.
     *
     * @param agentArguments arguments for agent
     * @param instrumentation  instrumentation instance
     */
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        install(instrumentation);
    }

    /**
     * Allows installation of java agent with Attach API.
     *
     * @param agentArguments arguments for agent
     * @param instrumentation instrumentation instance
     */
    public static void agentmain(String agentArguments, Instrumentation instrumentation) {
        install(instrumentation);
    }

    /**
     * Installs the agent. The agent will only run on classes that are not included in the ignorePackages list
     * @param instrumentation instrumentation instance
     */
    private static void install(Instrumentation instrumentation) {
        // Construct cache to be used by instrumentation classes
        tracker = new GlobalProvenanceTracker();
        AssociationCache cache = new AssociationCache(tracker);
        AssociationCacheRegistry.registerCache(cache);

        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if(ignorePackages.stream().parallel().anyMatch(className::startsWith)) {
                    return null;
                }

                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter writer = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);

                try {
                    CallSiteVisitor visitor = new CallSiteVisitor(writer);
                    classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
                    return writer.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return classfileBuffer;
            }
        }, true);
    }

    public static ProvenanceTracker<Invocation> getProvenanceTracker() {
        return tracker;
    }
}