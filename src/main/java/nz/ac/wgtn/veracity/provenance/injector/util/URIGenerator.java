package nz.ac.wgtn.veracity.provenance.injector.util;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.net.URI;
import java.util.Collection;

public class URIGenerator {
    private  URIGenerator() {

    }



    /**
     * Utility method for creating URI's from method signatures.
     *
     * @param clazz class containing the method
     * @param method method name
     * @param descriptor method descriptor
     * @return URI in the format: $qualifiedClassname/$methodName#methodDescriptor
     */
    public static URI createMethodDescriptor(String clazz, String method, String descriptor) {
        clazz = clazz.replace('/', '.');
        return URI.create(clazz + "/" + method + "#" + descriptor);
    }
}
