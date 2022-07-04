package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global utility to track invocations of methods outside request-handling threads.
 * Methods can be filtered by package names (simple check whether strings start).
 * The filter is passed to the instrumented server by the client, so is initially not available.
 * @author jens dietrich
 */
public class SystemInvocationTracker {

    private static Set<String> trackedInvocations = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static String[] applicationPackages = null;

    public static void trackSystemInvocation(String invocation) {
        if (applicationPackages==null) {
            trackedInvocations.add(invocation);
        }
        else {
            for (String applicationPackage:applicationPackages) {
                if (invocation.startsWith(applicationPackage)) {
                    trackedInvocations.add(invocation);
                }
            }
        }
    }

    /**
     * Pick up tracked system invocations.
     * @param appPcks -- the comma-seperated list of application package prefixes (example: "org.apache.foo,org.apache.bar")
     * @return a set of (string representations of) invoked methods
     */
    public static Set<String> getTrackedInvocations(String appPcks) {
        boolean initFilter = applicationPackages==null;
        if (initFilter) {
            applicationPackages = appPcks.split(",");
            // remove non-application methods already recorded
            Iterator<String> methods = trackedInvocations.iterator();
            while (methods.hasNext()) {
                boolean isApplicationClass = false;
                String next = methods.next();
                for (String appPck:applicationPackages) {
                    isApplicationClass = isApplicationClass || next.startsWith(appPck);
                }
                if (!isApplicationClass) {
                    methods.remove();
                }
            }
        }
        Set<String> set = new HashSet<>();
        set.addAll(trackedInvocations);
        trackedInvocations.clear(); // acceptable small change that something might get lost here since the map is concurrent , but better than locking/ non-concurrent map
        return set;
    }

    // reset the class - mainly for unit testing
    public static void reset() {
        trackedInvocations = Collections.newSetFromMap(new ConcurrentHashMap<>());
        applicationPackages = null;
    }


}
