package nz.ac.wgtn.veracity.provenance.injector;

import nz.ac.wgtn.veracity.approv.jbind.EntityRef;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class EntityTracker {

    private static final EntityTracker instance = new EntityTracker();

    private final Collection<EntityRef> refs;

    private EntityTracker() {
        this.refs = new HashSet<>();
    }

    public static EntityTracker getInstance() {
        return instance;
    }

    public synchronized void addEntity(EntityRef ref) {
        this.refs.add(ref);
    }

    public synchronized Collection<EntityRef> getRefs() {
        return Collections.unmodifiableCollection(this.refs);
    }
}
