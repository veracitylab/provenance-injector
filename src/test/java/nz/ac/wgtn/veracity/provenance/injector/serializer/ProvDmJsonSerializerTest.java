package nz.ac.wgtn.veracity.provenance.injector.serializer;

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

public class ProvDmJsonSerializerTest {

    private static final URI DEFAULT_TYPE = URI.create("http://veracity.wgtn.ac.nz/app-provenance#Database");
    private static final Object DEFAULT_VALUE = "Hello!";
    private static final Entity DEFAULT_ENTITY = Entity.from(DEFAULT_TYPE.toString(), DEFAULT_VALUE);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonSchema schema;


    @BeforeClass
    public static void setUpAll() {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        InputStream is = ProvDmJsonSerializerTest.class.getClassLoader().getResourceAsStream("schema.json");
        schema = factory.getSchema(is);
    }

    @Test
    public void testEntityConformsToSchema() throws JsonProcessingException {
        String serialized = ProvDmJsonSerializer.serializeEntities(List.of(DEFAULT_ENTITY));

        JsonNode tree = mapper.readTree(serialized);
        Set<ValidationMessage> errors = schema.validate(tree);
        assertEquals(0, errors.size());
    }

    @Test
    public void testActivityConformsToSchema() throws JsonProcessingException {
        String serialized = ProvDmJsonSerializer.serializeActivity(URI.create("http://exampleActivity"));

        JsonNode tree = mapper.readTree(serialized);
        Set<ValidationMessage> errors = schema.validate(tree);
        assertEquals(0, errors.size());
    }
}