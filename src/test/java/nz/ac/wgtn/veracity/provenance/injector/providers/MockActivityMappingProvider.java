package nz.ac.wgtn.veracity.provenance.injector.providers;

import nz.ac.wgtn.veracity.approv.jbind.ActivityMapping;
import nz.ac.wgtn.veracity.approv.jbind.ActivityMappingProvider;
import nz.ac.wgtn.veracity.approv.jbind.Execution;

import java.net.URI;
import java.util.List;
import java.util.Set;

public class MockActivityMappingProvider implements ActivityMappingProvider {
    @Override
    public Set<ActivityMapping> getActivityMappings() throws Exception {
        System.out.printf("DEBUG: %s loaded%n", this.getClass().getName());
        ActivityMapping activityMapping = new ActivityMapping();
        activityMapping.setActivity(URI.create("https://veracity.wgtn.ac.nz/app-provenance#activity"));

        Execution execution = new Execution();
        execution.setOwner("nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass");
        execution.setDescriptor("()Ljava/lang/String;");
        execution.setName("somethingFunny");
        activityMapping.setExecutions(List.of(execution));

        return Set.of(activityMapping);
    }
}
