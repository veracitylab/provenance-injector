package nz.ac.wgtn.veracity.provenance.injector.model;

public class Entity {

    private final Integer id;

    private final String type;

    private final Object value;

    private Object target;

    private Entity(String type, Object value, Object target) {
        this.id = System.identityHashCode(value);
        this.value = value;
        this.type = type;
        this.target = target;
    }

    public Integer getId() {
        return this.id;
    }

    public Object getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public static Entity create(String type, Object value) {
        return new Entity(type, value, null);
    }
}
