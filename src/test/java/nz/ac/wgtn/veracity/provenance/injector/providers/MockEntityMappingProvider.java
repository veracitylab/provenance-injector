package nz.ac.wgtn.veracity.provenance.injector.providers;

import nz.ac.wgtn.veracity.approv.jbind.EntityMapping;
import nz.ac.wgtn.veracity.approv.jbind.EntityMappingProvider;
import nz.ac.wgtn.veracity.approv.jbind.EntityRef;
import nz.ac.wgtn.veracity.approv.jbind.Execution;

import java.net.URI;
import java.util.Set;

public class MockEntityMappingProvider implements EntityMappingProvider {

    @Override
    public Set<EntityMapping> getEntityMappings() {
        System.out.printf("DEBUG: %s loaded%n", this.getClass().getName());
        return Set.of(
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.BOOLEAN, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.CHAR, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.BYTE, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.SHORT, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.INTEGER, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.FLOAT, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.LONG, 0),
                EntityMappingFactory.getSingleArgCollectorMapping(JvmPrimitiveTypes.DOUBLE, 0)
        );
    }

    static class EntityMappingFactory {
        static EntityMapping getSingleArgCollectorMapping(JvmPrimitiveTypes argument, int argIndex) {
            EntityMapping mapping = new EntityMapping();
            mapping.setEntity(URI.create("https://veracity.wgtn.ac.nz/app-provenance#argument-method"));
            mapping.setGroup("mocks");

            Execution execution = new Execution();
            execution.setOwner("nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass");
            execution.setName("somethingWithArg");
            execution.setDescriptor(String.format("(%s)V", argument.repr));
            mapping.setExecution(execution);

            mapping.setSource(EntityRef.ARG);
            mapping.setSourceIndex(argIndex);
            mapping.setTarget(EntityRef.RETURN);

            mapping.setCreate(true);
            return mapping;
        }
    }

    enum JvmPrimitiveTypes {
        BOOLEAN("Z"),
        CHAR("C"),
        BYTE("B"),
        SHORT("S"),
        INTEGER("I"),
        FLOAT("F"),
        LONG("L"),
        DOUBLE("D");


        final String repr;
        JvmPrimitiveTypes(String value) {
            this.repr = value;
        }
    }
}
