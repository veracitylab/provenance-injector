package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.tracker.ProvenanceTracker;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class AssociationCache {

    private final ProvenanceTracker<Invocation> externalTracker;
    private final Set<Invocation> invocationCache;
    private final Map<Integer, Entity> entityCache;
    private final Map<Integer, Set<Entity>> targetsToEntitiesCache;
    private final Map<String, Integer> taintedIdentitiesCache;
    private final Map<String, Object> taintedTargetsCache;


    public AssociationCache(ProvenanceTracker<Invocation> tracker) {
        this.externalTracker = tracker;
        this.invocationCache = Collections.synchronizedSet(new LinkedHashSet<>());
        this.entityCache = Collections.synchronizedMap(new LinkedHashMap<>());
        this.targetsToEntitiesCache = Collections.synchronizedMap(new LinkedHashMap<>());
        this.taintedIdentitiesCache = Collections.synchronizedMap(new LinkedHashMap<>());
        this.taintedTargetsCache = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    void cacheInvocation(Invocation invocation, Object target) {
//        System.out.println("AssociationCache.cacheInvocation(invocation=" + invocation + ", target=" + target + ") called.");   //DEBUG
        int targetIdent = System.identityHashCode(target);

        if (targetIdent != 0 && targetsToEntitiesCache.containsKey(targetIdent)) {
            invocation.associateEntities(targetsToEntitiesCache.get(targetIdent));
            System.out.println("DEBUG: Entity was associated");
        }

        invocationCache.add(invocation);
//        System.out.println("AssociationCache.cacheInvocation() about to call externalTracker.track().");   //DEBUG
        externalTracker.track(invocation);
    }

    public void cacheEntity(String entityTaint, Entity entity, Object target) {
        if (entity != null && !taintedTargetsCache.containsKey(entityTaint)) {
            int identity = System.identityHashCode(entity.getValue());
            entityCache.put(identity, entity);
            taintedIdentitiesCache.put(entityTaint, identity);
            return;
        }

        if (entity != null && taintedTargetsCache.containsKey(entityTaint)) {
            entity.setTarget(taintedTargetsCache.get(entityTaint));
            int entityIdent = System.identityHashCode(entity.getValue());
            int targetIdent = System.identityHashCode(entity.getTarget());

            entityCache.put(entityIdent, entity);
            targetsToEntitiesCache.computeIfAbsent(targetIdent, key -> Collections.synchronizedSet(new HashSet<>())).add(entity);

            taintedIdentitiesCache.remove(entityTaint);
            taintedTargetsCache.remove(entityTaint);
            return;
        }

        if (target != null && !taintedIdentitiesCache.containsKey(entityTaint)) {
            taintedTargetsCache.put(entityTaint, target);
            return;
        }

        if (target != null && taintedIdentitiesCache.containsKey(entityTaint)) {
            int entityIdent = taintedIdentitiesCache.get(entityTaint);
            int targetIdent = System.identityHashCode(target);

            Entity existing = entityCache.get(entityIdent);
            existing.setTarget(target);

            targetsToEntitiesCache.computeIfAbsent(targetIdent, key -> Collections.synchronizedSet(new HashSet<>())).add(existing);
            taintedIdentitiesCache.remove(entityTaint);
            taintedTargetsCache.remove(entityTaint);
        }
    }

    public Set<Invocation> getInvocationCache() {
        return Set.copyOf(invocationCache);
    }

    public Set<Entity> getEntityCache() {
        return Set.copyOf(entityCache.values());
    }

    Map<Integer, Entity> getEntityCacheWithIdentities() {
        return Map.copyOf(entityCache);
    }

    public void clear() {
        invocationCache.clear();
        entityCache.clear();
        targetsToEntitiesCache.clear();
        taintedIdentitiesCache.clear();
        taintedTargetsCache.clear();
    }
}
