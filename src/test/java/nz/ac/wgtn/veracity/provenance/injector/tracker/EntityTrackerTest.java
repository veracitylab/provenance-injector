package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

public class EntityTrackerTest {

    @Before
    public void setUp() {
        EntityTracker.getInstance().nuke();
    }

    @Test
    public void testAddingAnEntityWithNoTargetWillCreateARef() {
        Entity newEntity = Entity.from(URI.create("source"), "caller", "The Object");
        String taint = UUID.randomUUID().toString();

        EntityTracker tracker = EntityTracker.getInstance();
        tracker.addItem(taint, newEntity, null);

        Assert.assertEquals(1, tracker.getEntities().size());
    }

    @Test
    public void testAddingAnEntityThenATargetWillAssociateThatTarget() {
        Entity newEntity = Entity.from(URI.create("source"), "caller", "The Object");
        String taint = UUID.randomUUID().toString();
        String targetObject = "The target";

        EntityTracker tracker = EntityTracker.getInstance();
        tracker.addItem(taint, newEntity, null);

        Assert.assertEquals(1, tracker.getEntities().size());

        tracker.addItem(taint, null, targetObject);
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();

        Assert.assertNotNull(entity.getTarget());
        Assert.assertEquals(targetObject, entity.getTarget());
    }

    @Test
    public void testAddingAnEntityThenATargetWithDifferentTaintWillNotAssociateThatTarget() {
        Entity newEntity = Entity.from(URI.create("source"), "caller", "The Object");
        String taint = UUID.randomUUID().toString();
        String targetObject = "The target";

        EntityTracker tracker = EntityTracker.getInstance();
        tracker.addItem(taint, newEntity, null);

        Assert.assertEquals(1, tracker.getEntities().size());

        tracker.addItem("random value", null, targetObject);
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();

        Assert.assertNull(entity.getTarget());
    }

    @Test
    public void testAddingATargetObjectWithNoEntityWillNotCreateARef() {
        String taint = UUID.randomUUID().toString();
        String targetObject = "The target";

        EntityTracker tracker = EntityTracker.getInstance();
        tracker.addItem(taint, null, targetObject);

        Assert.assertEquals(0, tracker.getEntities().size());
    }

    @Test
    public void testAddingATargetObjectThenAnEntityWillCreateARef() {
        Entity newEntity = Entity.from(URI.create("source"), "caller", "The Object");
        String taint = UUID.randomUUID().toString();
        String targetObject = "The target";

        EntityTracker tracker = EntityTracker.getInstance();
        tracker.addItem(taint, null, targetObject);

        Assert.assertEquals(0, tracker.getEntities().size());

        tracker.addItem(taint, newEntity, null);

        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();

        Assert.assertNotNull(entity.getTarget());
        Assert.assertEquals(targetObject, entity.getTarget());
    }

    @Test
    public void testAddingATargetObjectThenAnEntityWithDifferentTaintWillCreateARefButNotAssociate() {
        Entity newEntity = Entity.from(URI.create("source"), "caller", "The Object");
        String taint = UUID.randomUUID().toString();
        String targetObject = "The target";

        EntityTracker tracker = EntityTracker.getInstance();
        tracker.addItem(taint, null, targetObject);

        Assert.assertEquals(0, tracker.getEntities().size());

        tracker.addItem("Some random taint", newEntity, null);

        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();

        Assert.assertNull(entity.getTarget());
    }
}