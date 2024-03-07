package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Activity;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalProvenanceTrackerTestCase {
    private GlobalProvenanceTracker tracker;
    @BeforeEach
    void setUp() {
        tracker = new GlobalProvenanceTracker();
    }

    @Test
    void testCullDoesNotModifyListPreviouslyReturnedByPickup() {
        String id = tracker.start();
        Invocation inv = makeInvocation("foo");
        tracker.track(inv);
        tracker.finish();
        List<Invocation> result = tracker.pickup(id);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(inv);

        tracker.cull(id);

        // Will fail if cull() modified the same list object as pickup() returned
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(inv);
    }

    Invocation makeInvocation(String suffix) {
        List<Activity> activities = List.of(Activity.create("blue" + suffix), Activity.create("orange" + suffix));
        List<Entity> entities = List.of(Entity.create("red" + suffix, "redValue"), Entity.create("green" + suffix, "greenValue"));
        return Invocation.create(activities, entities);
    }
}
