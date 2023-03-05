package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;

public class Entity {

    private int code;

    private final URI source;

    private final URI type;

    private final Object value;

    private final Object target;

    private Entity(URI source, URI type, Object value) {
        this.source = source;
        this.value = value;
        this.type = type;
        //TODO: Add way to associate this entity with the consumer of the object. To do this I will add some code to the
        // injector that will obtain the Object memory reference and associate by that.
        //
        this.target = null;
    }

    public static Entity from(URI source, String type, Object value) {
        return new Entity(source, URI.create(type), value);
    }

    public Object getValue() {
        return value;
    }

    public URI getType() {
        return this.type;
    }

    public URI getSource() {
        return this.source;
    }

    public Object getTarget() {
        return this.target;
    }
}
