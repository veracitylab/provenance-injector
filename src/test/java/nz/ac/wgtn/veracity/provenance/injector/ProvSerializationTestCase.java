package nz.ac.wgtn.veracity.provenance.injector;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProvSerializationTestCase {
    private static final JsonSchemaFactory SCHEMA_FACTORY = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(ValidationConfiguration.newBuilder().setUseFormat(true).freeze())
            .freeze();

    private static JsonSchema schema;

    @BeforeClass
    public static void setUpAll() throws ProcessingException {
        schema = SCHEMA_FACTORY.getJsonSchema("resource:/schema.json");
    }

    /**
     * Test single
     */
    @Test
    public void testAssociationOfSingleEntityCreationWithActivity() {

    }

}
