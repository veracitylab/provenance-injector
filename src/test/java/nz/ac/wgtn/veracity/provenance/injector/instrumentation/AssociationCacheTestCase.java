package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.tracker2.NoopProvenanceTracker;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AssociationCacheTestCase {

    private AssociationCache cache;

    @Before
    public void setUp() {
        cache = new AssociationCache(new NoopProvenanceTracker());
    }

    @Test
    public void testCachingAnEntityWithNoTargetWillNotHaveAnyAssociations() {
        Entity entity = Entity.from("caller", "the object");
        String taint = UUID.randomUUID().toString();

        cache.cacheEntity(taint, entity, null);

        assertThat(cache.getEntityCache()).hasSize(1);
    }

    @Test
    public void testCachingAnEntityThenATargetWithSameTaintWillAssociateThatTarget() {
        Entity entity = Entity.from("caller", "the object");
        String taint = UUID.randomUUID().toString();
        String target = "the target";

        cache.cacheEntity(taint, entity, null);
        assertThat(cache.getEntityCache()).hasSize(1);

        cache.cacheEntity(taint, null, target);
        Optional<Entity> result = cache.getEntityCache().stream().findFirst();

        assertThat(result).isPresent();
        assertThat(result.get().getTarget()).isEqualTo(target);
    }

    @Test
    public void testCachingAEntityThenATargetWithDifferentTaintWillNotAssociateThatTarget() {
        Entity entity = Entity.from("caller", "the object");
        String taint = UUID.randomUUID().toString();
        String target = "the target";

        cache.cacheEntity(taint, entity, null);
        assertThat(cache.getEntityCache()).hasSize(1);

        cache.cacheEntity("random taint", null, target);
        Optional<Entity> result = cache.getEntityCache().stream().findFirst();

        assertThat(result).isPresent();
        assertThat(result.get().getTarget()).isNull();
    }

    @Test
    public void testCachingATargetButNoEntityWillNotPopulateEntityCache() {
        String taint = UUID.randomUUID().toString();
        String target = "the target";

        cache.cacheEntity(taint, null, target);
        assertThat(cache.getEntityCache()).isEmpty();
    }

    @Test
    public void testCachingATargetThenEntityWithSameTaintWillPopulateEntityCache() {
        Entity entity = Entity.from("caller", "the object");
        String taint = UUID.randomUUID().toString();
        String target = "the target";

        cache.cacheEntity(taint, null, target);
        assertThat(cache.getEntityCache()).isEmpty();

        cache.cacheEntity(taint, entity, null);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getTarget()).isEqualTo(target);
    }

    @Test
    public void testCachingATargetThenEntityWithDifferentTaintWillPopulateEntityCacheButNotAssociateTheFirstTarget() {
        Entity entity = Entity.from("caller", "the object");
        String taint = UUID.randomUUID().toString();
        String target = "the target";

        cache.cacheEntity(taint, null, target);
        assertThat(cache.getEntityCache()).isEmpty();

        cache.cacheEntity("other taint", entity, null);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getTarget()).isNull();

    }
}