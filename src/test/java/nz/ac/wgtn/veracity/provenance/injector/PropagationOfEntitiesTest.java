package nz.ac.wgtn.veracity.provenance.injector;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import nz.ac.wgtn.veracity.provenance.injector.tracker.InvocationTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

public class PropagationOfEntitiesTest {

    @BeforeClass
    public static void setUpAll() {
        TestUtils.attachAgentClass();
    }


    @Before
    public void clearEntityTracker() {
        EntityTracker.getInstance().nuke();
    }

    @Test
    public void testTrackingDatabaseEntityTargetCollectedWhenPresent() throws Exception {
        SomeDatabaseClass databaseClass = new SomeDatabaseClass();
        String expectedValue = "jdbc:h2:mem:test";
        databaseClass.someDatabaseMethod();

        EntityTracker entityTracker = EntityTracker.getInstance();
        Assert.assertEquals(1, entityTracker.getEntities().size());
        Entity entity = entityTracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedValue, entity.getValue());
        Assert.assertNotNull(entity.getTarget());

        InvocationTracker invocationTracker = InvocationTracker.getInstance();
        Collection<Invocation> invocations = invocationTracker.getInvocations();
        Assert.assertEquals(1, invocations.size());
    }
}
