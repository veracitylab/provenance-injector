package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


/**
 * This class will be a tuple of URI's containing the calling method, the method instruction and the
 * associated activity.
 */
public class Invocation {
    private final URI caller;
    private final URI invocation;
    private final Collection<URI> activities;
    private final Collection<Entity> associatedEntities;
    private final Instant generationTime;

    private Invocation(URI caller, URI invocation, Collection<URI> activities) {
        this.caller = caller;
        this.invocation = invocation;
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
     * @param caller     caller of the method with provenance activities
     * @param invocation method with provenance activities bound
     * @param activities activities related to the method invocation
     * @return Invocation object as a 3-URI tuple
     */
    public static Invocation fromMethodIsn(URI caller, URI invocation, Collection<URI> activities) {
        return new Invocation(caller, invocation, activities);
    }
}
