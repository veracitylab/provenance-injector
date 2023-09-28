package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityCreationTestCase {

    private AssociationCache cache;

    @Before
    public void setUp() {
        cache = AssociationCacheRegistry.getCache();
        cache.clear();
    }

    @Test
    public void testSingleEntityCachedWithStaticInvocation() {
        String expected = "theArg";

        SomeClass.doSomethingStatically(expected);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expected);
    }

    @Test
    public void testSingleEntityCachedWithStaticInvocationIfDuplicateValue() {
        String expected = "theArg";

        SomeClass.doSomethingStatically(expected);
        SomeClass.doSomethingStatically(expected);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expected);
    }

    @Test
    public void testSingleEntityCachedWithDynamicInvocation() {
        SomeClass someClass = new SomeClass();
        String expected = "theArg";

        someClass.doSomethingDynamically(expected);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expected);
    }

    @Test
    public void testSingleEntityCachedWithDynamicInvocationIfDuplicateValue() {
        SomeClass someClass = new SomeClass();
        String expected = "theArg";

        someClass.doSomethingDynamically(expected);
        someClass.doSomethingDynamically(expected);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expected);
    }

    @Test
    public void testTrackingEntitySingleBool() {
        SomeClass someClass = new SomeClass();
        boolean expectedVal = true;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleChar() {
        SomeClass someClass = new SomeClass();
        char expectedVal = 't';

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleByte() {
        SomeClass someClass = new SomeClass();
        byte expectedVal = 0x16;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleShort() {
        SomeClass someClass = new SomeClass();
        short expectedVal = 2;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleInt() {
        SomeClass someClass = new SomeClass();
        int expectedVal = 2;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleFloat() {
        SomeClass someClass = new SomeClass();
        float expectedVal = 2F;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleLong() {
        SomeClass someClass = new SomeClass();
        long expectedVal = 2L;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleDouble() {
        SomeClass someClass = new SomeClass();
        double expectedVal = 2D;

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

    @Test
    public void testTrackingEntitySingleObject() {
        SomeClass someClass = new SomeClass();
        Object expectedVal = "the object";

        someClass.somethingWithArg(expectedVal);
        assertThat(cache.getEntityCache()).hasSize(1);

        Optional<Entity> result = cache.getEntityCache().stream().findFirst();
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo(expectedVal);
    }

}
