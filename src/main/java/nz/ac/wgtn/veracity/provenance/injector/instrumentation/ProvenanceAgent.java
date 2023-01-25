package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassWriter;
import nz.ac.wgtn.veracity.approv.jbind.Execution;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

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
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if(ignorePackages.stream().parallel().anyMatch(className::startsWith)) {
                    return classfileBuffer;
                }

                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter writer = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_FRAMES);
                try {
                    CallSiteVisitor visitor = new CallSiteVisitor(writer);
                    classReader.accept(visitor, 0);
                    return writer.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return classfileBuffer;
            }
        }, true);
    }
}