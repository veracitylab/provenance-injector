package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

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
        System.out.println("DEBUGGING: Invocation detected");
        this.invocations.add(invocation);
    }

    public synchronized Collection<Invocation> getInvocations() {
        return Collections.unmodifiableCollection(this.invocations);
    }

    public synchronized void nuke() {
        this.invocations.clear();
    }
}
