package nz.ac.wgtn.veracity.provenance.injector.tracker2.jee;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.tracker2.ProvenanceTracker;
import nz.ac.wgtn.veracity.provenance.injector.tracker2.ThreadLocalProvenanceTracker;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Abstract servlet to pickup provenance data.
 * @author jens dietrich
 */
public abstract class ProvenanceInfoPickupServlet extends HttpServlet {

    protected static final ProvenanceTracker<Invocation> tracker = new ThreadLocalProvenanceTracker<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getPathInfo();

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
