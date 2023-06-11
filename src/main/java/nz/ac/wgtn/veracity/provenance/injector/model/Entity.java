package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;

public class Entity {

    private int code;

    private final URI source;

    private final URI type;

    private final Object value;

    private Object target;

    private Entity(URI source, URI type, Object value) {
        this.source = source;
        this.value = value;
        this.type = type;
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

    public void setTarget(Object target) {
        this.target = target;
    }
}
