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
        return "global";
    }

    @Override
    public void finish() {
        outbox.addAll(this.invocations);
        invocations.clear();
    }

    public void track(Invocation invocation) {
        invocations.add(invocation);
    }

    @Override
    public List<Invocation> pickup(String id) {
        return List.copyOf(outbox);     // Don't return the same list that cull() will clear out!
    }

    @Override
    public boolean cull(String id) {
        outbox.clear();
        return true;
    }
}
