package nz.ac.wgtn.veracity.provenance.injector.rt;

import nz.ac.wgtn.veracity.provenance.injector.model.ProvenanceEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Abstraction for encoding data gathered via instrumentation, and to be returned to the fuzzing client.
 * @author jens dietrich
 */
public interface Encoder {

    public static Encoder DEFAULT = new JSONEncoder();

    String getContentType();

    void encode(List<ProvenanceEvent> data, PrintWriter out) throws IOException;
}
