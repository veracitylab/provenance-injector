package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InvocationCreationTestCase {

    private AssociationCache cache;

    @Before
    public void setUp() {
        cache = AssociationCacheRegistry.getCache();
        cache.clear();
    }

    @Test
    public void testSingleInvocationCached() {
        SomeClass someClass = new SomeClass();
        someClass.somethingFunny();

        assertThat(cache.getInvocationCache()).hasSize(1);
    }
}
