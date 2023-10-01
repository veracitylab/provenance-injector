package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.tracker2.NoopProvenanceTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssociationCacheTestCase {

    private AssociationCache cache;

    @BeforeEach
    void setUp() {
        cache = new AssociationCache(new NoopProvenanceTracker());
    }

    @Test
    void testCachingAnEntityWithNoTargetWillNotHaveAnyAssociations() {
        Entity entity = Entity.create("caller", "the object");
        String taint = UUID.randomUUID().toString();

        cache.cacheEntity(taint, entity, null);

        assertThat(cache.getEntityCache()).hasSize(1);
    }

    @Test
    void testCachingAnEntityThenATargetWithSameTaintWillAssociateThatTarget() {
        Entity entity = Entity.create("caller", "the object");
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
    void testCachingAEntityThenATargetWithDifferentTaintWillNotAssociateThatTarget() {
        Entity entity = Entity.create("caller", "the object");
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
    void testCachingATargetButNoEntityWillNotPopulateEntityCache() {
        String taint = UUID.randomUUID().toString();
        String target = "the target";

        cache.cacheEntity(taint, null, target);
        assertThat(cache.getEntityCache()).isEmpty();
    }

    @Test
    void testCachingATargetThenEntityWithSameTaintWillPopulateEntityCache() {
        Entity entity = Entity.create("caller", "the object");
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
    void testCachingATargetThenEntityWithDifferentTaintWillPopulateEntityCacheButNotAssociateTheFirstTarget() {
        Entity entity = Entity.create("caller", "the object");
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