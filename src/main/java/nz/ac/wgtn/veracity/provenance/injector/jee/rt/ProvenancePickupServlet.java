package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servlet that can be used to pick up provenance data.
 * Encoding is delegated to an Encoder.
 * If no methods are found, a 404 is returned.
 * @author jens dietrich
 */


public class ProvenancePickupServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String id = request.getPathInfo();
        if (id==null || id.length()==0) {

            // set up for monitoring in web browser -- to check that tracing takes place
            // and has been installed correctly
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<body>");
            out.println("<b>Tracing service is available</b><p/>");
            out.println("<i>Available tickets:</i>");
            String tickets = InvocationTracker.DEFAULT.getAvailableTickets().stream().collect(Collectors.joining(" , "));
            out.println(tickets);
            out.println("</body>");
            out.println("</html>");
            out.close();
            return;
        }

        // request.getServletContext().log("TrackedInvocationsPickupServlet -- id exists: " + id);

        while (id.startsWith("/")) {
            id = id.substring(1);
        }
        // request.getServletContext().log("TrackedInvocationsPickupServlet -- normalised id: " + id);

        if (Constants.SYSTEM_INVOCATIONS_TICKET.equals(id))  {
            String applicationPackagePrefixes = request.getParameter(Constants.SYSTEM_INVOCATIONS_APPLICATION_PACKAGE_PREFIXES_PARAMETER);
            if (applicationPackagePrefixes==null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Missing request parameter: " + Constants.SYSTEM_INVOCATIONS_APPLICATION_PACKAGE_PREFIXES_PARAMETER);
            }
            else {
                Set<String> invocations = SystemInvocationTracker.getTrackedInvocations(applicationPackagePrefixes);
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                for (String invocation:invocations) {
                    out.println(invocation);
                }
                out.close();
            }
        }

        else {
            Map<DataKind, List<Object>> tracked = InvocationTracker.DEFAULT.pickup(id);
            if (tracked == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType(Encoder.DEFAULT.getContentType());
            PrintWriter out = response.getWriter();
            Encoder.DEFAULT.encode(tracked, out);
            out.close();
        }
    }



}
