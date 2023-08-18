package nz.ac.wgtn.veracity.provenance.injector.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;

import java.net.URI;

public class Entity {

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

    public String toProvEntity() {
        JSONObject entity = new JSONObject();
        entity.put("id", System.identityHashCode(this.value));

        JSONArray attrs = new JSONArray();

        String[] typeRepr = this.type.toString().split("/");
        if (typeRepr.length >= 2) {
            JSONObject provType = new JSONObject();
            provType.put("prov:type", typeRepr[typeRepr.length - 1]);
            attrs.put(provType);
        }

        entity.put("attributes", attrs);


        return entity.toString();
    }
}
