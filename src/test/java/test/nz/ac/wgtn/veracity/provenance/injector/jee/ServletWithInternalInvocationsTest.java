package test.nz.ac.wgtn.veracity.provenance.injector.jee;

import nz.ac.wgtn.veracity.provenance.injector.jee.rt.Constants;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.InvocationTrackerManagerFilter;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.ProvenancePickupServlet;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

public class ServletWithInternalInvocationsTest {

    @Test
    public void testInvocationTracking () throws Exception {

        // part 1: record invocations
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterConfig config = new MockFilterConfig();
        FilterChain chain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                new ServletWithInternalInvocations().doGet((HttpServletRequest)req,(HttpServletResponse)res);
            }
        };

        InvocationTrackerManagerFilter invocationTrackerManagerFilter = new InvocationTrackerManagerFilter();
        invocationTrackerManagerFilter.init(config);
        invocationTrackerManagerFilter.doFilter(request,response,chain);
        invocationTrackerManagerFilter.destroy();

        String ticket = response.getHeader(Constants.PROVENANCE_HEADER);

        assertNotNull(ticket);

        // part 2: pick up recorded invocations
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        ProvenancePickupServlet pickup = new ProvenancePickupServlet();
        request.setPathInfo(ticket);
        pickup.doGet(request,response);

        assertEquals("application/json",response.getContentType());

        JSONObject data = new JSONObject(response.getContentAsString());

        List<MethodSpec> specs = Utils.extractInvocations(data);

        assertTrue(specs.stream()
                .filter(m -> m.getClassName().equals("test.nz.ac.wgtn.veracity.provenance.injector.jee.ServletWithInternalInvocations"))
                .filter(m -> m.getMethodName().equals("doGet"))
                .anyMatch(m -> m.getDescriptor().equals("(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V")));

        assertTrue(specs.stream()
                .filter(m -> m.getClassName().equals("test.nz.ac.wgtn.veracity.provenance.injector.jee.ServletWithInternalInvocations"))
                .filter(m -> m.getMethodName().equals("foo1"))
                .anyMatch(m -> m.getDescriptor().equals("()V")));

        assertTrue(specs.stream()
                .filter(m -> m.getClassName().equals("test.nz.ac.wgtn.veracity.provenance.injector.jee.ServletWithInternalInvocations"))
                .filter(m -> m.getMethodName().equals("foo2"))
                .anyMatch(m -> m.getDescriptor().equals("()V")));

        // part 3: try again, should have been deleted now once data has been picked up
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        pickup = new ProvenancePickupServlet();
        request.setPathInfo(ticket);
        pickup.doGet(request,response);
        assertEquals(404,response.getStatus());

    }

}
