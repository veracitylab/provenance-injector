package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to manage the lifecycle of the invocation tracker.
 * @author jens dietrich
 * @author shawn
 * shawn - record taint strings from header for taint tracking
 */
public class InvocationTrackerManagerFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest)request).getServletPath();
        boolean isApplicationRequest = !path.contains(Constants.PROVENANCE_PATH_TOKEN);
        if (isApplicationRequest) {
            String ID = InvocationTracker.DEFAULT.startInvocationTracking();
            ((HttpServletResponse) response).addHeader(Constants.PROVENANCE_HEADER, ID);
        }

        try {
            chain.doFilter(request, response);
        }
        catch (Exception x) {
            // catch exception to continue in case  an exception leads to a 500
            if (isApplicationRequest) {
                InvocationTracker.DEFAULT.finishInvocationTracking();
            }
            throw new ServletException("wrapped application exception",x);
        }

        if (isApplicationRequest) {
            InvocationTracker.DEFAULT.finishInvocationTracking();
        }

    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
    }

}

