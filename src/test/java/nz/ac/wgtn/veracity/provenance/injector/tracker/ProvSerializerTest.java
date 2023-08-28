package nz.ac.wgtn.veracity.provenance.injector.tracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProvSerializerTest {

    private static final URI DEFAULT_SOURCE = URI.create("http://nz.ac.wgtn.veracity.provenance.example/exampleMethod#()V");
    private static final URI DEFAULT_TYPE = URI.create("http://veracity.wgtn.ac.nz/app-provenance#Database");
    private static final Object DEFAULT_VALUE = "Hello!";
    private static final Entity DEFAULT_ENTITY = Entity.from(DEFAULT_SOURCE, DEFAULT_TYPE.toString(), DEFAULT_VALUE);

    private static final ObjectMapper mapper = new ObjectMapper();

    private static JsonSchema schema;


    @BeforeClass
    public static void setUpAll() {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        InputStream is = ProvSerializerTest.class.getClassLoader().getResourceAsStream("schema.json");
        schema = factory.getSchema(is);
    }

    @Test
    public void testEntityConformsToSchema() throws JsonProcessingException {
        String serialized = ProvSerializer.serializeEntities(List.of(DEFAULT_ENTITY));

        JsonNode tree = mapper.readTree(serialized);
        Set<ValidationMessage> errors = schema.validate(tree);
        assertEquals(0, errors.size());
    }

    @Test
    public void testActivityConformsToSchema() throws JsonProcessingException {
        URI activity = URI.create("http://exampleActivity");
        String serialized = ProvSerializer.serializeActivities(List.of(activity));

        JsonNode tree = mapper.readTree(serialized);
        Set<ValidationMessage> errors = schema.validate(tree);
        assertEquals(0, errors.size());
    }
}