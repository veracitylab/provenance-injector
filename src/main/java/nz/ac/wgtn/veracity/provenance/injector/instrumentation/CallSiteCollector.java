package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CallSiteCollector {
    private static CallSiteCollector instance = null;
    private static final boolean LOGGING = true;

    private final ConcurrentMap<String, Collection<String>> callsites;


    private CallSiteCollector() {
        this.callsites = new ConcurrentHashMap<>();
    }

    public static synchronized CallSiteCollector getInstance() {
        if (instance == null) {
            instance = new CallSiteCollector();
        }

        return instance;
    }

    public synchronized void addCallSites(String className, Collection<String> sitesToAdd) {
        className = className.replace('/', '.');
        if (LOGGING) {
            System.out.printf("Callsites in %s:%n%s", className, String.join("\n", sitesToAdd));
        }
        this.callsites.putIfAbsent(className, sitesToAdd);
    }

    public synchronized Map<String, Collection<String>> currentCallSites() {
        return Map.copyOf(this.callsites);
    }
}
