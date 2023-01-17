package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.approv.jbind.Execution;
import nz.ac.wgtn.veracity.approv.jbind.EntityCreation;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActivityCollector {
    private static ActivityCollector instance = null;

    private final ConcurrentMap<String, Collection<Execution>> executions;

    private final ConcurrentMap<String, Collection<URI>> activities;

    private ActivityCollector() {
        this.executions = new ConcurrentHashMap<>();
        this.activities = new ConcurrentHashMap<>();
    }

    public static synchronized ActivityCollector getInstance() {
        if (instance == null) {
            instance = new ActivityCollector();
        }

        return instance;
    }

    public synchronized void evaluateExecutions(String methodName, Collection<Execution> executionsToAdd) {
        this.executions.putIfAbsent(methodName, executionsToAdd);
        executionsToAdd.forEach(exe -> {
            addActivitiesIfPresent(methodName, exe);
            addEntitiesIfPresent(methodName, exe);
        });
    }

    public synchronized void nuke() {
        this.executions.clear();
        this.activities.clear();
    }


    public synchronized Collection<Execution> executionsForMethod(String methodName) {
        return this.executions.getOrDefault(methodName, Set.of());
    }

    public synchronized Collection<URI> activitiesForMethod(String methodName) {
        return this.activities.getOrDefault(methodName, Set.of());
    }

    private void addActivitiesIfPresent(String methodName, Execution execution) {
        Set<URI> foundActivities = Bindings.getActivities(execution.getOwner(), execution.getName(), execution.getDescriptor());
        if (!foundActivities.isEmpty()) {
            this.activities.putIfAbsent(methodName, foundActivities);
        }
    }

    private void addEntitiesIfPresent(String methodName, Execution execution) {
        Collection<EntityCreation> items = Bindings.getEntityCreations(execution.getOwner(), execution.getName(), execution.getDescriptor());
    }
}
