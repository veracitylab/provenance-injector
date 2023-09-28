package nz.ac.wgtn.veracity.provenance.injector.tracker2.jee;

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

    protected static final ProvenanceTracker tracker = new ThreadLocalProvenanceTracker();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String id = request.getPathInfo();
        if (id==null || id.length()==0) {
            PrintWriter out = response.getWriter();
            // set up for monitoring in web browser -- to check that tracing takes place
            // and has been installed correctly
            response.setContentType(getContentType());
            List data = tracker.pickup(id);
            encode(out,data);
            tracker.cull(id);
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
    protected abstract void encode(PrintWriter out, List provenanceData) throws IOException ;

    /**
     * Get the content type, like application/json
     * @return
     */
    protected abstract String getContentType() ;

}
