package nz.ac.wgtn.veracity.provenance.injector.jee;

import nz.ac.wgtn.veracity.provenance.injector.jee.rt.SystemInvocationTracker;
import org.junit.Before;
import org.junit.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SystemInvocationTrackerTest {

    public static final String SYSTEM_METHOD1 = "java.util.List::size()I";
    public static final String SYSTEM_METHOD2 = "java.lang.Object::hashCode()I";
    public static final String APP_METHOD1 = "org.apache.foo.Foo:foo1()V";
    public static final String APP_METHOD2 = "org.apache.foo.Foo:foo2()V";
    public static final String APP_METHOD3 = "org.apache.bar.Bar:bar()V";
    public static final String OTHER_METHOD = "org.apache.other.Other::other()V";

    public static final Set<String> SCOPE = Stream.of(SYSTEM_METHOD1,SYSTEM_METHOD2,APP_METHOD1,APP_METHOD2,APP_METHOD3,OTHER_METHOD).collect(Collectors.toSet());

    @Before
    public void setup() {
        SystemInvocationTracker.reset();
    }

    public Set<String> setFrom(String... elements) {
        Set<String> set = new HashSet<>();
        for (String element:elements) {
            set.add(element);
        }
        return set;
    }

    // asserts the quality of sets
    // this is w.r.t. scope , i.e. sets may contain some other methods due to the fact that code is instrumented when tests are executed
    public void assertSetContains(Set<String> expected,Set<String> actual) {
        for (String s:expected) {
            assertTrue(SCOPE.contains(s));
            assertTrue(actual.contains(s));
        }
        for (String s:SCOPE) {
            if (!expected.contains(s)) {
                assertFalse(actual.contains(s));
            }
        }
    }

    @Test
    public void test1 () {
        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        Set<String> trackedInvocations = SystemInvocationTracker.getTrackedInvocations("");
        assertSetContains(setFrom(SYSTEM_METHOD1,APP_METHOD1,OTHER_METHOD),trackedInvocations);
    }

    @Test
    public void test2 () {
        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        Set<String> trackedInvocations = SystemInvocationTracker.getTrackedInvocations("org.apache");
        assertSetContains(setFrom(APP_METHOD1,OTHER_METHOD),trackedInvocations);
    }

    @Test
    public void test3 () {
        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        Set<String> trackedInvocations = SystemInvocationTracker.getTrackedInvocations("org.apache.foo");
        assertSetContains(setFrom(APP_METHOD1),trackedInvocations);
    }

    @Test
    public void test4 () {
        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD2);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD3);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        Set<String> trackedInvocations = SystemInvocationTracker.getTrackedInvocations("org.apache.foo,org.apache.bar");
        assertSetContains(setFrom(APP_METHOD1,APP_METHOD2,APP_METHOD3),trackedInvocations);
    }

    @Test
    public void test5 () {
        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD2);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD3);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        Set<String> trackedInvocations = SystemInvocationTracker.getTrackedInvocations("org.apache.foo,org.apache.bar");
        assertSetContains(setFrom(APP_METHOD1,APP_METHOD2,APP_METHOD3),trackedInvocations);
    }

    @Test
    public void test6 () {
        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD1);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD2);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        Set<String> trackedInvocations = SystemInvocationTracker.getTrackedInvocations("org.apache.foo,org.apache.bar");
        assertSetContains(setFrom(APP_METHOD1,APP_METHOD2),trackedInvocations);

        SystemInvocationTracker.trackSystemInvocation(SYSTEM_METHOD2);
        SystemInvocationTracker.trackSystemInvocation(APP_METHOD3);
        SystemInvocationTracker.trackSystemInvocation(OTHER_METHOD);
        trackedInvocations = SystemInvocationTracker.getTrackedInvocations("org.apache.foo,org.apache.bar");
        assertSetContains(setFrom(APP_METHOD3),trackedInvocations);

    }


}
