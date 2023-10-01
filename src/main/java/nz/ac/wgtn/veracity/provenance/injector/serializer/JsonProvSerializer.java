package nz.ac.wgtn.veracity.provenance.injector.serializer;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.json.JSONObject;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class is responsible for serializing Prov-DM objects and behaviours
 */
public class JsonProvSerializer implements ProvSerializer {

    @Override
    public String serialize(Invocation invocation) {
        return serializeInvocation(invocation).toString();
    }

    @Override
    public Collection<String> serialize(Collection<Invocation> invocations) {
        return invocations.stream()
                .map(inv -> serializeInvocation(inv).toString())
                .collect(Collectors.toList());
    }

    private JSONObject serializeInvocation(Invocation invocation) {
        JSONObject entities = serializeEntities(invocation.getAssociatedEntities());
        //TODO: Activities
        //TODO: Generations
        //TODO: Usages

        JSONObject inv = new JSONObject();
        inv.put("entity", entities);
        //TODO: Activities
        //TODO: Generations
        //TODO: Usages
        return inv;
    }

    private JSONObject serializeEntities(Collection<Entity> entities) {
        JSONObject wrapper = new JSONObject();
        entities.forEach(entity -> wrapper.put(entity.getId().toString(), serializeEntity(entity)));
        return wrapper;
    }

    private JSONObject serializeEntity(Entity entity) {
        JSONObject object = new JSONObject();
        String[] typeRepr = entity.getType().toString().split("/");

        if (typeRepr.length > 1) {
            object.put("prov:type", typeRepr[typeRepr.length - 1]);
        }

        object.put("prov:value", entity.getValue().toString());
        return object;
    }

    private JSONObject serializeActivity() {

        return null;
    }
}
