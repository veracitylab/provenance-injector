package nz.ac.wgtn.veracity.provenance.injector.tracker.jee;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.serializer.JsonProvSerializer;
import nz.ac.wgtn.veracity.provenance.injector.serializer.ProvSerializer;
import nz.ac.wgtn.veracity.provenance.injector.tracker.ProvenanceTracker;

import java.io.PrintWriter;
import java.util.List;

import static nz.ac.wgtn.veracity.provenance.injector.tracker.jee.Constants.CONTENT_TYPE_JSON;

public class JsonProvenanceInfoPickupServlet extends ProvenanceInfoPickupServlet {

    private final ProvSerializer serializer;


    public JsonProvenanceInfoPickupServlet(ProvenanceTracker<Invocation> tracker) {
        super(tracker);
        this.serializer = new JsonProvSerializer();
    }


    @Override
    protected String extractId(HttpServletRequest request) {
        return request.getPathInfo();
    }

    @Override
    protected void encode(PrintWriter out, List<Invocation> provenanceData) {
        out.write(serializer.serialize(provenanceData));
    }

    @Override
    protected String getContentType() {
        return CONTENT_TYPE_JSON;
    }
}
