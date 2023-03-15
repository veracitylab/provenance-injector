package nz.ac.wgtn.veracity.provenance.injector.providers;

import nz.ac.wgtn.veracity.approv.jbind.ActivityMapping;
import nz.ac.wgtn.veracity.approv.jbind.ActivityMappingProvider;

import java.util.Set;

public class MockActivityMappingProvider implements ActivityMappingProvider {
    @Override
    public Set<ActivityMapping> getActivityMappings() throws Exception {
        System.out.printf("DEBUG: %s loaded%n", this.getClass().getName());
        return Set.of();
    }
}
