package nz.ac.wgtn.veracity.provenance.injector.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URI;


class JsonProvSerializerTest {

    private static final URI DEFAULT_TYPE = URI.create("http://veracity.wgtn.ac.nz/app-provenance#Database");
    private static final Object DEFAULT_VALUE = "Hello!";
    private static final Entity DEFAULT_ENTITY = Entity.from(DEFAULT_TYPE.toString(), DEFAULT_VALUE);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonSchema schema;
    private ProvSerializer serializer;


    @BeforeAll
    static void setUpAll() {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        InputStream is = JsonProvSerializerTest.class.getClassLoader().getResourceAsStream("schema.json");
        schema = factory.getSchema(is);
    }

    @BeforeEach
    void setUp() {
        serializer = new JsonProvSerializer();
    }

    @Test
    void testEntityConformsToSchema() throws JsonProcessingException {
//        String serialized = serializer.serializeEntities(List.of(DEFAULT_ENTITY)).toString();
//
//        JsonNode tree = mapper.readTree(serialized);
//        Set<ValidationMessage> errors = schema.validate(tree);
//        assertThat(errors).isEmpty();
    }

    @Test
    void testActivityConformsToSchema() throws JsonProcessingException {
//        String serialized = JsonProvSerializer.serializeActivity(URI.create("http://exampleActivity"));
//
//        JsonNode tree = mapper.readTree(serialized);
//        Set<ValidationMessage> errors = schema.validate(tree);
//        assertEquals(0, errors.size());
    }
}