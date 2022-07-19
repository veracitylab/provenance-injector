package nz.ac.wgtn.veracity.provenance.injector.rt;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
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
