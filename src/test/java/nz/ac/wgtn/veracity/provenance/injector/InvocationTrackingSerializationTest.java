package nz.ac.wgtn.veracity.provenance.injector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
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
        JsonElement element = JsonParser.parseString(result);

        assertTrue(element.isJsonObject());

        JsonObject object = element.getAsJsonObject();

        assertTrue(object.has("caller"));
        assertTrue(object.has("invocation"));
        assertTrue(object.has("activities"));

        assertTrue(object.get("caller").isJsonPrimitive());
        assertTrue(object.get("invocation").isJsonPrimitive());
        assertTrue(object.get("activities").isJsonArray());
    }

    @Test
    public void testJsonSerializationProducesCorrectValues() {
        String result = INVOCATION.toJSON();
        JsonElement element = JsonParser.parseString(result);
        JsonObject object = element.getAsJsonObject();

        JsonArray expectedList = new JsonArray();
        expectedList.add("https://com.example/app-provenance#InterestingActivity");

        assertEquals("com.example.CallerClass/exampleMethod#()V", object.get("caller").getAsString());
        assertEquals("com.example.InterestingClass/interestingMethod#()V", object.get("invocation").getAsString());
        assertEquals(expectedList, object.get("activities").getAsJsonArray());
    }
}
