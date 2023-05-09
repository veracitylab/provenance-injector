package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityTracker {

    private static final EntityTracker instance = new EntityTracker();

    private final Map<Integer, Entity> refs;

    private EntityTracker() {
        this.refs = new HashMap<>();
    }

    public static EntityTracker getInstance() {
        return instance;
    }

    public synchronized void addEntity(Entity ref, int identityHash) {
        this.refs.putIfAbsent(identityHash, ref);
    }

    public synchronized Map<Integer, Entity> getEntities() {
        return Collections.unmodifiableMap(this.refs);
    }

    public synchronized  void nuke() {
        this.refs.clear();
    }
}
