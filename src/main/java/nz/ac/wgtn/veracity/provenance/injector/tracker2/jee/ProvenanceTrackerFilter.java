package nz.ac.wgtn.veracity.provenance.injector.tracker2.jee;

import nz.ac.wgtn.veracity.provenance.injector.tracker2.ProvenanceTracker;
import nz.ac.wgtn.veracity.provenance.injector.tracker2.ThreadLocalProvenanceTracker;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Abstract class to facilitate provenance tracking in JEE applications.
 * This will set a provenance header with an id that can then be used to
 * fetch provenance info collected.
 * The tracker used is ThreadLocalProvenanceTracker, based on the assumption that the filter and the application code
 * will run in the same thread.
 * @author jens dietrich
 */
public abstract class ProvenanceTrackerFilter implements Filter  {

    protected static final ProvenanceTracker tracker = new ThreadLocalProvenanceTracker();

    /**
     * Specify whether this is an application request, and not a request to get collected provevance info.
     * For instance, this could be based on a token in the path.
     * @param request
     * @return
     */
    protected abstract boolean isApplicationRequest(ServletRequest request) ;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean isApplicationRequest = isApplicationRequest(request);
        if (isApplicationRequest) {
            String ID = tracker.start();
            ((HttpServletResponse) response).addHeader(Constants.PROVENANCE_HEADER, ID);
        }

        try {
            chain.doFilter(request, response);
        }
        catch (Exception x) {
            // catch exception to continue in case  an exception leads to a 500
            if (isApplicationRequest) {
                tracker.finish();
            }
            throw new ServletException("wrapped application exception",x);
        }

        if (isApplicationRequest) {
            tracker.finish();
        }

    }

}
