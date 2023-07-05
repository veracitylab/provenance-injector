package nz.ac.wgtn.veracity.provenance.injector;

import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvocationTrackingSerializationTest {

    private static final Invocation INVOCATION = Invocation.fromMethodIsn(
            URI.create("com.example.CallerClass/exampleMethod#()V"),
            URI.create("com.example.InterestingClass/interestingMethod#()V"),
            List.of(URI.create("https://com.example/app-provenance#InterestingActivity"))
    );

    @Test
    public void testJsonSerializationProducesCorrectObject() {
        String result = INVOCATION.toJSON();
        JSONObject object = new JSONObject(result);
        assertTrue(object.has("caller"));
        assertTrue(object.has("invocation"));
        assertTrue(object.has("activities"));
    }

    @Test
    public void testJsonSerializationProducesCorrectValues() {
        String result = INVOCATION.toJSON();
        JSONObject object = new JSONObject(result);

        JSONArray expectedList = new JSONArray();
        expectedList.put("https://com.example/app-provenance#InterestingActivity");

        assertEquals("com.example.CallerClass/exampleMethod#()V", object.getString("caller"));
        assertEquals("com.example.InterestingClass/interestingMethod#()V", object.getString("invocation"));
        assertEquals(expectedList.toString(), object.getJSONArray("activities").toString());
    }
}
