package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;


public class Entity {

    private final Integer id;

    private final URI source;

    private final URI type;

    private final Object value;

    private Object target;

    private Entity(URI source, URI type, Object value) {
        this.id = System.identityHashCode(value);
        this.source = source;
        this.value = value;
        this.type = type;
        this.target = null;
    }

    public static Entity from(URI source, String type, Object value) {
        return new Entity(source, URI.create(type), value);
    }

    public Integer getId() {
        return this.id;
    }

    public Object getValue() {
        return this.value;
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
