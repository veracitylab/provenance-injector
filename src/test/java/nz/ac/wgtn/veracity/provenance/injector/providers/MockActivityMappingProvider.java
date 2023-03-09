package nz.ac.wgtn.veracity.provenance.injector.providers;

import nz.ac.wgtn.veracity.approv.jbind.ActivityMapping;
import nz.ac.wgtn.veracity.approv.jbind.ActivityMappingProvider;

import java.util.Set;

public class MockActivityMappingProvider implements ActivityMappingProvider {
    @Override
    public Set<ActivityMapping> getActivityMappings() throws Exception {
        System.out.println("This has been loaded by the service loader");
        return null;
    }
}
