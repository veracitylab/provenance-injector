package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GlobalProvenanceTracker implements ProvenanceTracker<Invocation> {

    private final Set<Invocation> invocations;
    private final List<Invocation> outbox = Collections.synchronizedList(new ArrayList<>());

    public GlobalProvenanceTracker() {
        this.invocations = Collections.synchronizedSet(new LinkedHashSet<>());
    }

    @Override
    public String start() {
        System.out.println("GlobalProvenanceTracker.start() called!");  //DEBUG
        return "global";
    }

    @Override
    public void finish() {
        System.out.println("GlobalProvenanceTracker.finish() called! Will move " + invocations.size() + " invocations to outbox.");  //DEBUG
        outbox.addAll(this.invocations);
        invocations.clear();
    }

    public void track(Invocation invocation) {
        System.out.println("GlobalProvenanceTracker.track(" + invocation + ") called!");  //DEBUG
        invocations.add(invocation);
        System.out.println("GlobalProvenanceTracker.track(): There are now " + invocations.size() + " invocations tracked so far.");  //DEBUG
    }

    @Override
    public List<Invocation> pickup(String id) {
        System.out.println("GlobalProvenanceTracker.pickup(" + id + ") called! Will send " + outbox.size() + " invocations back.");  //DEBUG
        return List.copyOf(outbox);     // Don't return the same list that cull() will clear out!
    }

    @Override
    public boolean cull(String id) {
        System.out.println("GlobalProvenanceTracker.cull() called! Removing " + outbox.size() + " invocations from outbox.");  //DEBUG
        outbox.clear();
        return true;
    }
}
