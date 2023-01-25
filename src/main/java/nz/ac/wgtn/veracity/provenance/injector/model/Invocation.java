package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;

/**
 * This class will be a tuple of URI's containing the calling method, the method instruction and the
 * associated activity.
 */
public class Invocation {
    private final URI caller;

    //TODO: Finish this model class

    public Invocation(URI caller) {
        this.caller = caller;
    }

    public static Invocation fromDescriptor(String clazz, String method, String descriptor) {
        clazz = clazz.replace('/', '.');
        URI caller = URI.create(clazz + "/" + method + "#" + descriptor);

        return new Invocation(caller);
    }
}
