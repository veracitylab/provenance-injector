package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;


/**
 * This class represents a bundle of activities and their associated entities
 */
public class Invocation {
    private final Collection<Activity> activities;
    private final Collection<Entity> associatedEntities;

    private Invocation(Collection<Activity> activities, Collection<Entity> entities) {
        this.activities = activities;
        this.associatedEntities = entities;
    }

    public Collection<Activity> getActivities() {
        return activities;
    }

    public Collection<Entity> getAssociatedEntities() {
        return Collections.unmodifiableCollection(this.associatedEntities);
    }

    public void associateEntity(Entity entity) {
        this.associatedEntities.add(entity);
    }

    public void associateEntities(Collection<Entity> entities) {
        this.associatedEntities.addAll(entities);
    }

    public static Invocation create(Collection<Activity> activities) {
        return new Invocation(activities, new LinkedHashSet<>());
    }

    public static Invocation create(Collection<Activity> activities, Collection<Entity> entities) {
        return new Invocation(activities, entities);
    }
}
