package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker {

    private static final EntityTracker instance = new EntityTracker();

    private final Map<Integer, Entity> refs;

    private final Map<Integer, Set<Entity>> targetRefs;

    private final Map<String, Integer> taintToIdentities;

    private final Map<String, Object> targetIngestor;


    private EntityTracker() {
        this.refs = new ConcurrentHashMap<>();
        this.targetRefs = new ConcurrentHashMap<>();
        this.taintToIdentities = new ConcurrentHashMap<>();
        this.targetIngestor = new ConcurrentHashMap<>();
    }

    public static EntityTracker getInstance() {
        return instance;
    }

    public void addItem(String taint, Entity entity, Object target) {
        if (entity != null && !targetIngestor.containsKey(taint)) {
            int identityHash = System.identityHashCode(entity.getValue());
            refs.put(identityHash, entity);
            taintToIdentities.put(taint, identityHash);
            return;
        }

        if (entity != null && targetIngestor.containsKey(taint)) {
            entity.setTarget(targetIngestor.get(taint));
            int entityIdent = System.identityHashCode(entity.getValue());
            int targetIdent = System.identityHashCode(entity.getTarget());

            refs.put(entityIdent, entity);
            targetRefs.computeIfAbsent(targetIdent, key -> ConcurrentHashMap.newKeySet()).add(entity);

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
            int targetIdent = System.identityHashCode(target);

            Entity entityLookup = refs.get(referencedHash);
            entityLookup.setTarget(target);

            targetRefs.computeIfAbsent(targetIdent, key -> ConcurrentHashMap.newKeySet()).add(entityLookup);
            taintToIdentities.remove(taint);
            targetIngestor.remove(taint);
        }
    }

    public Map<Integer, Entity> getEntities() {
        return Collections.unmodifiableMap(this.refs);
    }

    public Map<Integer, Set<Entity>> getTargetRefs() {
        return Collections.unmodifiableMap(this.targetRefs);
    }

    public Set<Entity> lookupFromTarget(Object value) {
        int identity = System.identityHashCode(value);
        return this.targetRefs.get(identity);
    }

    public void nuke() {
        this.refs.clear();
        this.targetIngestor.clear();
        this.taintToIdentities.clear();
    }
}
