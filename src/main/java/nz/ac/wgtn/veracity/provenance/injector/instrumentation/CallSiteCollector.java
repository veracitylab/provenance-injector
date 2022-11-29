package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import java.util.*;

public class CallSiteCollector {
    private static CallSiteCollector instance = null;

    private final Map<String, Collection<String>> callsites;


    private CallSiteCollector() {
        this.callsites = new HashMap<>();
    }

    public static synchronized CallSiteCollector getInstance() {
        if (instance == null) {
            instance = new CallSiteCollector();
        }

        return instance;
    }

    public synchronized void addCallSites(String className, Collection<String> sitesToAdd) {
        className = className.replace('/', '.');
        System.out.printf("Callsites in %s:\n%s", className, String.join("\n", sitesToAdd));
        this.callsites.put(className, sitesToAdd);
    }

    public synchronized Map<String, Collection<String>> currentCallSites() {
        return Map.copyOf(this.callsites);
    }
}
