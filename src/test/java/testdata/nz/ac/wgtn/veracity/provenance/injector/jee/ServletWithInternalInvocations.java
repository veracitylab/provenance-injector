package testdata.nz.ac.wgtn.veracity.provenance.injector.jee;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Test subject. Keep it testdata namespace to avoid instrumentation exclusions.
 * @author jens dietrich
 */
public class ServletWithInternalInvocations extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        foo1();
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void foo1() throws IOException {
        foo2();
    }

    private void foo2() throws IOException {
        // call to some external URL
        URL url = new URL("http://foo.com");
        url.openConnection();
    }
}
