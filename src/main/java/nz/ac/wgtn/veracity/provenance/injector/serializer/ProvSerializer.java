package nz.ac.wgtn.veracity.provenance.injector.serializer;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.util.Collection;

public interface ProvSerializer {

    String serialize(Invocation invocation);

    Collection<String> serialize(Collection<Invocation> invocations);
}
