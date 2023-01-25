package nz.ac.wgtn.veracity.provenance.injector;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InvocationTracker {

    private static final InvocationTracker instance = new InvocationTracker();
    private final Collection<Invocation> invocations;

    private InvocationTracker() {
        invocations = ConcurrentHashMap.newKeySet();
    }

    public static InvocationTracker getInstance() {
        return instance;
    }

    public synchronized void addInvocation(Invocation invocation) {
        this.invocations.add(invocation);
    }
}
