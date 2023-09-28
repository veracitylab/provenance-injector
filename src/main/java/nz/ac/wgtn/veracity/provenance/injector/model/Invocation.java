package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


/**
 * This class represents a bundle of activities and their associated entities
 */
public class Invocation {
    private final Collection<URI> activities;
    private final Collection<Entity> associatedEntities;
    private final Instant generationTime;

    private Invocation(Collection<URI> activities) {
        this.activities = activities;
        this.generationTime = Instant.now();
        this.associatedEntities = new HashSet<>();
    }

    public Collection<URI> getActivities() {
        return activities;
    }

    public Instant getGenerationTime() {
        return generationTime;
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

    /**
     * Creates a new invocation object
     *
     * @param activities activities related to the method invocation
     * @return Invocation object as a 3-URI tuple
     */
    public static Invocation fromMethodIsn(Collection<URI> activities) {
        return new Invocation(activities);
    }
}
