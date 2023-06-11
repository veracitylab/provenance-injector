package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;

import java.util.*;

public class EntityTracker {

    private static final EntityTracker instance = new EntityTracker();

    private final Map<Integer, Entity> refs;

    private final Map<String, Integer> taintToIdentities;

    private final Map<String, Object> targetIngestor;


    private EntityTracker() {
        this.refs = new HashMap<>();
        this.taintToIdentities = new HashMap<>();
        this.targetIngestor = new HashMap<>();
    }

    public static EntityTracker getInstance() {
        return instance;
    }

    public synchronized void addItem(String taint, Entity entity, Object target) {
        if (entity != null && !targetIngestor.containsKey(taint)) {
            int identityHash = System.identityHashCode(entity.getValue());
            refs.put(identityHash, entity);
            taintToIdentities.put(taint, identityHash);
            return;
        }

        if (entity != null && targetIngestor.containsKey(taint)) {
            entity.setTarget(targetIngestor.get(taint));
            refs.put(System.identityHashCode(entity.getValue()), entity);
            taintToIdentities.remove(taint);
            targetIngestor.remove(taint);
            return;
        }

        if (target != null && !taintToIdentities.containsKey(taint)) {
            targetIngestor.put(taint, target);
            return;
        }

        if (target != null && taintToIdentities.containsKey(taint)) {
            int referencedHash = taintToIdentities.get(taint);
            refs.get(referencedHash).setTarget(target);
            taintToIdentities.remove(taint);
            targetIngestor.remove(taint);
        }
    }

    public synchronized Map<Integer, Entity> getEntities() {
        return Collections.unmodifiableMap(this.refs);
    }

    public synchronized void nuke() {
        this.refs.clear();
        this.targetIngestor.clear();
        this.taintToIdentities.clear();
    }
}
