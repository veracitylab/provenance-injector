package nz.ac.wgtn.veracity.provenance.injector.tracker.jee;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.tracker.ProvenanceTracker;



/**
 * Abstract class to facilitate provenance tracking in JEE applications.
 * This will set a provenance header with an id that can then be used to
 * fetch provenance info collected.
 * The tracker used is ThreadLocalProvenanceTracker, based on the assumption that the filter and the application code
 * will run in the same thread.
 * @author jens dietrich
 */
public abstract class ProvenanceTrackerFilter implements Filter {

    protected final ProvenanceTracker<Invocation> tracker;

    protected ProvenanceTrackerFilter(ProvenanceTracker<Invocation> tracker) {
        this.tracker = tracker;
    }

    /**
     * Specify whether this is an application request, and not a request to get collected provevance info.
     * For instance, this could be based on a token in the path.
     * @param request
     * @return
     */
    protected abstract boolean isApplicationRequest(ServletRequest request) ;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
        boolean isApplicationRequest = isApplicationRequest(request);
        if (isApplicationRequest) {
            String id = tracker.start();
            ((HttpServletResponse) response).addHeader(Constants.PROVENANCE_HEADER, id);
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
