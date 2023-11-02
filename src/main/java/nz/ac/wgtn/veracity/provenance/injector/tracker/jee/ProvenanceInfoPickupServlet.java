package nz.ac.wgtn.veracity.provenance.injector.tracker.jee;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.tracker.ProvenanceTracker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Abstract servlet to pickup provenance data.
 * @author jens dietrich
 */
public abstract class ProvenanceInfoPickupServlet extends HttpServlet {

    protected final ProvenanceTracker<Invocation> tracker;

    protected ProvenanceInfoPickupServlet(ProvenanceTracker<Invocation> tracker) {
        this.tracker = tracker;
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String id = extractId(request);

        if (id == null || id.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try(PrintWriter writer = response.getWriter()) {
            response.setContentType(getContentType());
            List<Invocation> data = tracker.pickup(id);
            encode(writer, data);
            tracker.cull(id);

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extract the if from a request for provenance data.
     * For instance, the id might be the path , this could be implemented as "return request.getPathInfo()".
     * @param request
     * @return
     */
    protected abstract String extractId(HttpServletRequest request) ;

    /**
     * Encode data.
     * @return
     * @param out the stream to write to
     * @param provenanceData the data to write
     */
    protected abstract void encode(PrintWriter out, List<Invocation> provenanceData) throws IOException ;

    /**
     * Get the content type, like application/json
     * @return
     */
    protected abstract String getContentType() ;

}
