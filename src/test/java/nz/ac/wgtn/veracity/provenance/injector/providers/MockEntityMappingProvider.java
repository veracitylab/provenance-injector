package nz.ac.wgtn.veracity.provenance.injector.providers;

import nz.ac.wgtn.veracity.approv.jbind.EntityMapping;
import nz.ac.wgtn.veracity.approv.jbind.EntityMappingProvider;
import nz.ac.wgtn.veracity.approv.jbind.EntityRef;
import nz.ac.wgtn.veracity.approv.jbind.Execution;

import java.net.URI;
import java.util.Set;

public class MockEntityMappingProvider implements EntityMappingProvider {

    private static final String WITH_ARG_METHOD = "somethingWithArg";

    @Override
    public Set<EntityMapping> getEntityMappings() {
        System.out.printf("DEBUG: %s loaded%n", this.getClass().getName());
        return Set.of(
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.BOOLEAN, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.CHAR, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.BYTE, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.SHORT, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.INTEGER, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.FLOAT, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.LONG, 0),
                EntityMappingFactory.getSinglePrimitiveArgCollectorMapping(WITH_ARG_METHOD, JvmPrimitiveTypes.DOUBLE, 0),

                EntityMappingFactory.getSingleArgCollectorMapping("doSomethingStatically", "(Ljava/lang/String;)Ljava/lang/String;", 0),
                EntityMappingFactory.getSingleArgCollectorMapping("somethingWithArg","(Ljava/lang/Object;)V", 0),
                EntityMappingFactory.getSingleArgCollectorMapping("doSomethingDynamically","(Ljava/lang/String;)Ljava/lang/String;", 0)
        );
    }


    static class EntityMappingFactory {

        static EntityMapping getSinglePrimitiveArgCollectorMapping(String method, JvmPrimitiveTypes argument, int argIndex) {
            EntityMapping mapping = new EntityMapping();
            mapping.setEntity(URI.create("https://veracity.wgtn.ac.nz/app-provenance#argument-method"));
            mapping.setGroup("mocks");

            Execution execution = new Execution();
            execution.setOwner("nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass");
            execution.setName(method);
            execution.setDescriptor(String.format("(%s)V", argument.repr));
            mapping.setExecution(execution);

            mapping.setSource(EntityRef.ARG);
            mapping.setSourceIndex(argIndex);
            mapping.setTarget(EntityRef.RETURN);

            mapping.setCreate(true);
            return mapping;
        }


        static EntityMapping getSingleArgCollectorMapping(String method, String signature, int argIndex) {
            EntityMapping mapping = new EntityMapping();
            mapping.setEntity(URI.create("https://veracity.wgtn.ac.nz/app-provenance#argument-method"));
            mapping.setGroup("mocks");

            Execution execution = new Execution();
            execution.setOwner("nz.ac.wgtn.veracity.provenance.injector.sampleclasses.SomeClass");
            execution.setName(method);
            execution.setDescriptor(signature);
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
        LONG("J"),
        DOUBLE("D");

        final String repr;


        JvmPrimitiveTypes(String value) {
            this.repr = value;
        }
    }
}
