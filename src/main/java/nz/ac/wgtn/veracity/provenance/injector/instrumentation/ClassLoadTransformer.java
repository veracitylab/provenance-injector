package nz.ac.wgtn.veracity.provenance.injector.instrumentation;


import net.bytebuddy.jar.asm.ClassReader;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

public class ClassLoadTransformer implements ClassFileTransformer {
    private static final Collection<String> ignorePackages = Set.of(
            "sun/",
            "java/lang/",
            "java/util/",
            "java/security/",
            "java/io/"
    );

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        visitCallSites(className);
        return classfileBuffer;
    }

    private static void visitCallSites(String className) {
        if(ignorePackages.stream().parallel().anyMatch(className::startsWith)) {
            return;
        }
        try {
            Collection<String> sites = new HashSet<>();
            new ClassReader(className).accept(new CallSiteVisitor(sites), 0);
            CallSiteCollector.getInstance().addCallSites(className, sites);
        } catch (IOException ex) {
            System.err.printf("Unable to load class %s", className);
        }

    }
}
