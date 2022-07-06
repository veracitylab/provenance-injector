package nz.ac.wgtn.veracity.provenance.injector.jee;

import nz.ac.wgtn.veracity.provenance.ProvenanceBinding;
import nz.ac.wgtn.veracity.provenance.ProvenanceEvent;
import nz.ac.wgtn.veracity.provenance.ProvenanceKind;
import nz.ac.wgtn.veracity.provenance.ProvenanceLocationKind;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.Constants;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.InvocationTrackerManagerFilter;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.JSONEncoder;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.ProvenancePickupServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import testdata.nz.ac.wgtn.veracity.provenance.injector.jee.ServletWithInternalInvocations;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import static org.junit.Assert.*;

public class ServletWithInternalInvocationsTest {


    @Before
    public void init() {
        // install special test ProvenanceBinding
        ProvenanceBinding binding = new ProvenanceBinding() {
            @Override
            public EnumSet<ProvenanceKind> inferProvenanceKindFromMethodInvocation(String declaringClass, String methodName, String descriptor) {
                if (methodName.equals("foo1")) {
                    return EnumSet.of(ProvenanceKind.DATABASE_WRITE);
                }
                else if (methodName.equals("foo2")) {
                    return EnumSet.of(ProvenanceKind.NETWORK_WRITE);
                }
                else {
                    return EnumSet.of(ProvenanceKind.NONE);
                }
            }
        };
        ProvenanceBinding.DEFAULT = binding;  // avoid install as this may trigger instrumentation
    }


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

        JSONArray data = new JSONArray(response.getContentAsString());
        List<ProvenanceEvent> events = JSONEncoder.decodeArray(data);
        assertNotNull(data);
        assertEquals(2,events.size());

        assertTrue(events.stream()
                .filter(e -> e.getKind()==ProvenanceKind.DATABASE_WRITE)
                .filter(e -> e.getLocationKind()== ProvenanceLocationKind.METHOD)
                .filter(e -> e.getLocation().get("className").equals(ServletWithInternalInvocations.class.getName()))
                .filter(e -> e.getLocation().get("methodName").equals("foo1"))
                .anyMatch(e -> e.getLocation().get("descriptor").equals("()V")));

        assertTrue(events.stream()
                .filter(e -> e.getKind()==ProvenanceKind.NETWORK_WRITE)
                .filter(e -> e.getLocationKind()== ProvenanceLocationKind.METHOD)
                .filter(e -> e.getLocation().get("className").equals(ServletWithInternalInvocations.class.getName()))
                .filter(e -> e.getLocation().get("methodName").equals("foo2"))
                .anyMatch(e -> e.getLocation().get("descriptor").equals("()V")));

//        assertTrue(specs.stream()
//                .filter(m -> m.getClassName().equals("test.testdata.nz.ac.wgtn.veracity.provenance.injector.jee.ServletWithInternalInvocations"))
//                .filter(m -> m.getMethodName().equals("foo1"))
//                .anyMatch(m -> m.getDescriptor().equals("()V")));

//        assertTrue(specs.stream()
//                .filter(m -> m.getClassName().equals("test.testdata.nz.ac.wgtn.veracity.provenance.injector.jee.ServletWithInternalInvocations"))
//                .filter(m -> m.getMethodName().equals("foo2"))
//                .anyMatch(m -> m.getDescriptor().equals("()V")));

        // part 3: try again, should have been deleted now once data has been picked up
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        pickup = new ProvenancePickupServlet();
        request.setPathInfo(ticket);
        pickup.doGet(request,response);
        assertEquals(404,response.getStatus());

    }

}
