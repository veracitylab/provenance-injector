package nz.ac.wgtn.veracity.provenance.injector;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass;
import nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeDatabaseClass;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;

import static nz.ac.wgtn.veracity.provenance.injector.TestUtils.loadAgentJar;
import static org.junit.Assert.fail;

public class EntityTrackingTest {

    @BeforeClass
    public static void setUpAll() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        long process = runtime.getPid();

        try {
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(process));
            File agent = loadAgentJar();
            vm.loadAgent(agent.getAbsolutePath());

        } catch (AttachNotSupportedException ex) {
            System.err.printf("Error attaching to self, JVM option %s not present%n", "-Djdk.attach.allowAttachSelf=true");
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        } catch (AgentLoadException | AgentInitializationException ex) {
            System.err.println("Error loading agent");
            fail(ex.getMessage());
        }
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
        URI expectedSource = URI.create("java.sql.DriverManager/getConnection#(Ljava/lang/String;Ljava/util/Properties;Ljava/lang/Class;)Ljava/sql/Connection;");
        databaseClass.someDatabaseMethod();

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedValue, (String) entity.getValue());
        Assert.assertEquals(expectedType, entity.getType());
        Assert.assertEquals(expectedSource, entity.getSource());
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
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (boolean) entity.getValue());
    }

    @Test
    public void testTrackingEntityArgSingleChar() {
        SomeClass someClass = new SomeClass();
        char expectedVal = 't';
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (char) entity.getValue());
    }

    @Test
    public void testTrackingEntityArgSingleByte() {
        SomeClass someClass = new SomeClass();
        byte expectedVal = 0x16;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (byte) entity.getValue());
    }

    @Test
    public void testTrackingEntityArgSingleShort() {
        SomeClass someClass = new SomeClass();
        short expectedVal = 2;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (short) entity.getValue());
    }

    @Test
    public void testTrackingEntityArgSingleInt() {
        SomeClass someClass = new SomeClass();
        int expectedVal = 2;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (int) entity.getValue());
    }

    @Test
    public void testTrackingEntityArgSingleFloat() {
        SomeClass someClass = new SomeClass();
        float expectedVal = 2F;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (float) entity.getValue(), 1e-6F);
    }

    @Test
    public void testTrackingEntityArgSingleLong() {
        SomeClass someClass = new SomeClass();
        long expectedVal = 2L;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (long) entity.getValue());
    }

    @Test
    public void testTrackingEntityArgSingleDouble() {
        SomeClass someClass = new SomeClass();
        double expectedVal = 2D;
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, (double) entity.getValue(), 1e-6D);
    }

    @Test
    public void testTrackingEntityArgSingleObject() {
        SomeClass someClass = new SomeClass();
        Object expectedVal = "a string object";
        someClass.somethingWithArg(expectedVal);

        EntityTracker tracker = EntityTracker.getInstance();
        Assert.assertEquals(1, tracker.getEntities().size());
        Entity entity = tracker.getEntities().iterator().next();
        Assert.assertEquals(expectedVal, entity.getValue());
    }
}
