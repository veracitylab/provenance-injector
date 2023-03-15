package nz.ac.wgtn.veracity.provenance.injector.providers;

import nz.ac.wgtn.veracity.approv.jbind.EntityMapping;
import nz.ac.wgtn.veracity.approv.jbind.EntityMappingProvider;

import java.util.Set;

public class MockEntityMappingProvider implements EntityMappingProvider {

    @Override
    public Set<EntityMapping> getEntityMappings() throws Exception {
       return Set.of();
    }
}
