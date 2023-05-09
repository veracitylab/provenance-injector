package nz.ac.wgtn.veracity.provenance.injector;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityFromArgTrackingTest {

    @BeforeClass
    public static void setUpAll() {
        TestUtils.attachAgentClass();
    }


    @Before
    public void clearEntityTracker() {
        EntityTracker.getInstance().nuke();
    }


    @Test
    public void testTrackingDatabaseEntityWhenPresent() throws Exception {
        SomeDatabaseClass databaseClass = new SomeDatabaseClass();
        String expectedValue = "jdbc:h2:mem:test";
        URI expectedType = URI.create("https://veracity.wgtn.ac.nz/app-provenance#Database");
        URI expectedSource = URI.create("java.sql.DriverManager/getConnection#(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;");
        databaseClass.someDatabaseMethod();

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedValue, entity.getValue());
        Assert.assertEquals(expectedType, entity.getType());
        Assert.assertEquals(expectedSource, entity.getSource());
    }


    @Test
    public void testTrackingDatabaseEntityTargetCollectedWhenPresent() throws Exception {
        SomeDatabaseClass databaseClass = new SomeDatabaseClass();
        String expectedValue = "jdbc:h2:mem:test";
        databaseClass.someDatabaseMethod();

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedValue, entity.getValue());
        Assert.assertNotNull(entity.getTarget());
    }


    @Test
    public void testTrackingStaticInvocationGeneratesSingleEntity() {
        String expectedValue = "theArg";
        SomeClass.doSomethingStatically("theArg");

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedValue, entity.getValue());
    }


    @Test
    public void testTrackingDynamicInvocationGeneratesMultipleEntities() {
        SomeClass someClass = new SomeClass();
        String expectedValue = "theArg";
        String expectedValue2 = "theArg2";
        someClass.doSomethingDynamically(expectedValue);
        someClass.doSomethingDynamically(expectedValue2);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(2, tracker.getEntities().size());
        Collection<Entity> entities = tracker.getEntities().values();
        Assert.assertEquals(Set.of(expectedValue, expectedValue2), entities.stream().map(Entity::getValue).collect(Collectors.toSet()));

    }


    @Test
    public void testTrackingDatabaseEntityWhenNotPresent() {
        SomeClass someClass = new SomeClass();
        someClass.doSomeThing();

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(0, tracker.getEntities().size());
    }


    @Test
    public void testTrackingEntityArgSingleBool() {
        SomeClass someClass = new SomeClass();
        boolean expectedVal = true;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, entity.getValue());
    }


    @Test
    public void testTrackingEntityArgSingleChar() {
        SomeClass someClass = new SomeClass();
        char expectedVal = 't';
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (char) entity.getValue());
    }


    @Test
    public void testTrackingEntityArgSingleByte() {
        SomeClass someClass = new SomeClass();
        byte expectedVal = 0x16;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (byte) entity.getValue());
    }


    @Test
    public void testTrackingEntityArgSingleShort() {
        SomeClass someClass = new SomeClass();
        short expectedVal = 2;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (short) entity.getValue());
    }


    @Test
    public void testTrackingEntityArgSingleInt() {
        SomeClass someClass = new SomeClass();
        int expectedVal = 2;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (int) entity.getValue());
    }


    @Test
    public void testTrackingEntityArgSingleFloat() {
        SomeClass someClass = new SomeClass();
        float expectedVal = 2F;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (float) entity.getValue(), 1e-6F);
    }


    @Test
    public void testTrackingEntityArgSingleLong() {
        SomeClass someClass = new SomeClass();
        long expectedVal = 2L;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (long) entity.getValue());
    }


    @Test
    public void testTrackingEntityArgSingleDouble() {
        SomeClass someClass = new SomeClass();
        double expectedVal = 2D;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, (double) entity.getValue(), 1e-6D);
    }


    @Test
    public void testTrackingEntityArgSingleObject() {
        SomeClass someClass = new SomeClass();
        Object expectedVal = "a string object";
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().entrySet().iterator().next().getValue();
        Assert.assertEquals(expectedVal, entity.getValue());
    }
}
