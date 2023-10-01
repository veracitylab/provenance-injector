package nz.ac.wgtn.veracity.provenance.injector.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import nz.ac.wgtn.veracity.provenance.injector.model.Activity;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIterator;


class JsonProvSerializerTestCase {

    private static final URI DEFAULT_TYPE = URI.create("http://veracity.wgtn.ac.nz/app-provenance#Database");
    private static final Object DEFAULT_VALUE = "Hello!";
    private static final Entity DEFAULT_ENTITY = Entity.create(DEFAULT_TYPE.toString(), DEFAULT_VALUE);
    private static final Activity DEFAULT_ACTIVITY = Activity.create("activity");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonSchema schema;
    private ProvSerializer serializer;


    @BeforeAll
    static void setUpAll() {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        InputStream is = JsonProvSerializerTestCase.class.getClassLoader().getResourceAsStream("schema.json");
        schema = factory.getSchema(is);
    }

    @BeforeEach
    void setUp() {
        serializer = new JsonProvSerializer();
    }

    @Test
    void testSingleInvocationWithOnlyActivitiesConformsToSchema() throws JsonProcessingException {
        Invocation invocation = Invocation.create(List.of(DEFAULT_ACTIVITY));
        String serialized = serializer.serialize(invocation);

        JsonNode tree = mapper.readTree(serialized);
        Set<ValidationMessage> errors = schema.validate(tree);
        assertThat(errors).isEmpty();
    }

    @Test
    void testSingleInvocationWithBothActivitiesAndEntitiesConformsToSchema() throws JsonProcessingException {
        Invocation invocation = Invocation.create(List.of(DEFAULT_ACTIVITY), List.of(DEFAULT_ENTITY));
        String serialized = serializer.serialize(invocation);

        JsonNode tree = mapper.readTree(serialized);
        Set<ValidationMessage> errors = schema.validate(tree);
        assertThat(errors).isEmpty();
    }
}