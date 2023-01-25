package nz.ac.wgtn.veracity.provenance.injector;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InvocationTracker {

    private static final InvocationTracker instance = new InvocationTracker();
    //TODO: Replace this with the URI tuple model when completed
    private final Map<String, Set<URI>> activityInvocations;

    private InvocationTracker() {
        activityInvocations = new ConcurrentHashMap<>();
    }

    public static InvocationTracker getInstance() {
        return instance;
    }

    public synchronized void addInvocation(String owner, Set<URI> activities) {
        this.activityInvocations.putIfAbsent(owner, activities);
    }
}
