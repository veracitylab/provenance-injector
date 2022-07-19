package nz.ac.wgtn.veracity.provenance.injector.rt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Abstraction for encoding data gathered via instrumentation, and to be returned to the fuzzing client.
 * @author jens dietrich
 */
public interface Encoder {

    public static Encoder DEFAULT = new JSONEncoder();

    String getContentType();

    void encode(Map<DataKind, List<Object>> data, PrintWriter out) throws IOException;
}
