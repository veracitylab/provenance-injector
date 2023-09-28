package nz.ac.wgtn.veracity.provenance.injector.tracker2;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.util.List;

public class NoopProvenanceTracker implements ProvenanceTracker<Invocation> {

    @Override
    public String start() {
        return "noop";
    }

    @Override
    public void finish() {
        // noop
    }

    @Override
    public void track(Invocation data) {
        // noop
    }

    @Override
    public List<Invocation> pickup(String id) {
        return List.of();
    }

    @Override
    public boolean cull(String id) {
        return true;
    }
}
