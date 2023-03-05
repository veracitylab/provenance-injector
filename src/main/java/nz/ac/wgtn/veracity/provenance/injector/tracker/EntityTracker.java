package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class EntityTracker {

    private static final EntityTracker instance = new EntityTracker();

    private final Collection<Entity> refs;

    private EntityTracker() {
        this.refs = new HashSet<>();
    }

    public static EntityTracker getInstance() {
        return instance;
    }

    public synchronized void addEntity(Entity ref) {
        this.refs.add(ref);
    }

    public synchronized Collection<Entity> getRefs() {
        return Collections.unmodifiableCollection(this.refs);
    }
}
