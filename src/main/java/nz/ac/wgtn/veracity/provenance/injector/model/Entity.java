package nz.ac.wgtn.veracity.provenance.injector.model;

import java.net.URI;

public class Entity {

    private int code;

    private URI source;

    private URI type;

    private Object value;

    private Entity(URI source, URI type, Object value) {
        this.source = source;
        this.value = value;
    }

    public static Entity from(URI source, String type, Object value) {
        return new Entity(source, URI.create(type), value);
    }
}
