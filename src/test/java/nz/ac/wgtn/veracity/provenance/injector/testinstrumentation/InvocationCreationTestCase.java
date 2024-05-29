package nz.ac.wgtn.veracity.provenance.injector.testinstrumentation;

import nz.ac.wgtn.veracity.provenance.injector.instrumentation.AssociationCache;
import nz.ac.wgtn.veracity.provenance.injector.instrumentation.AssociationCacheRegistry;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvocationCreationTestCase {

    private AssociationCache cache;

    @BeforeEach
    void setUp() {
        cache = AssociationCacheRegistry.getCache();
        cache.clear();
    }

    @Test
    void testSingleInvocationCached() {
        SomeClass someClass = new SomeClass();
        someClass.somethingFunny();

        assertThat(cache.getInvocationCache()).hasSize(1);
    }
}
