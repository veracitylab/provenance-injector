package nz.ac.wgtn.veracity.provenance.injector.instrumentation;


import net.bytebuddy.jar.asm.ClassReader;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ClassLoadTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //TODO: Replace filter with something better. The filter should transform the class name to something that is easily matched against in the binding API
        if (className.contains("nz/ac/vuw")) {
            visitCallSites(className);
        }
        return classfileBuffer;
    }

    private static void visitCallSites(String className) {
        try {
            Collection<String> sites = new HashSet<>();
            new ClassReader(className).accept(new CallSiteVisitor(sites), 0);
            CallSiteCollector.getInstance().addCallSites(className, sites);
        } catch (IOException ex) {
            System.err.printf("Unable to load class %s", className);
        }

    }
}
