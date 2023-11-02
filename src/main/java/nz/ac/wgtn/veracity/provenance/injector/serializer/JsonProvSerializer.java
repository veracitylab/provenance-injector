package nz.ac.wgtn.veracity.provenance.injector.serializer;

import nz.ac.wgtn.veracity.provenance.injector.model.Activity;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.json.JSONArray;
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
    public String serialize(Collection<Invocation> invocations) {
        JSONObject wrapper = new JSONObject();
        JSONArray items = new JSONArray();
        invocations.stream()
                .map(this::serializeInvocation)
                .forEach(items::put);

        wrapper.put("invocations", items);
        return wrapper.toString();
    }

    private JSONObject serializeInvocation(Invocation invocation) {
        JSONObject entities = serializeEntities(invocation.getAssociatedEntities());
        JSONObject activities = serializeActivities(invocation.getActivities());
        //TODO: Generations
        //TODO: Usages

        JSONObject inv = new JSONObject();
        inv.put("entity", entities);
        inv.put("activity", activities);
        //TODO: Generations
        //TODO: Usages
        return inv;
    }

    private JSONObject serializeEntity(Entity entity) {
        JSONObject object = new JSONObject();
        object.put("prov:type", entity.getType());
        object.put("prov:value", entity.getValue().toString());

        return object;
    }

    private JSONObject serializeEntities(Collection<Entity> entities) {
        JSONObject wrapper = new JSONObject();
        entities.forEach(entity -> wrapper.put(entity.getId().toString(), serializeEntity(entity)));

        return wrapper;
    }

    private JSONObject serializeActivities(Collection<Activity> activities) {
        JSONObject wrapper = new JSONObject();
        activities.forEach(activity -> wrapper.put(activity.getId(), serializeActivity(activity)));

        return wrapper;
    }

    private JSONObject serializeActivity(Activity activity) {
        JSONObject object = new JSONObject();

        object.put("prov:type", activity.getType());
        object.put("prov:endTime", activity.getEndTime().toString());

        return object;
    }
}
