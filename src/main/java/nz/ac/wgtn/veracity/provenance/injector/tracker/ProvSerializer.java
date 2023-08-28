package nz.ac.wgtn.veracity.provenance.injector.tracker;

import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.Collection;
/**
 * This class is responsible for serializing Prov-DM objects and behaviours
 */
public class ProvSerializer {


    public static String serializeEntities(Collection<Entity> entities) {
        JSONObject serializedEntities = new JSONObject();

        entities.forEach(entity -> {
            JSONObject serialized = serializeEntity(entity);
            serializedEntities.put(entity.getId().toString(), serialized);
        });

        JSONObject container = new JSONObject();
        container.put("entity", serializedEntities);

        return container.toString();
    }

    private static JSONObject serializeEntity(Entity entity) {
        JSONObject object = new JSONObject();
        String[] typeRepr = entity.getType().toString().split("/");

        if (typeRepr.length > 1) {
            object.put("prov:type", typeRepr[typeRepr.length - 1]);
        }

        object.put("prov:value", entity.getValue().toString());

        return object;
    }

    public static String serializeActivity(URI activity) {
        JSONObject object = new JSONObject();
        object.put("id", activity);

        JSONArray attributes = new JSONArray();
        String[] typeRepr = activity.toString().split("/");

        if (typeRepr.length > 1) {
            JSONObject provType = new JSONObject();
            provType.put("prov:type", typeRepr[typeRepr.length - 1]);
            attributes.put(provType);
        }

        object.put("attributes", attributes);

        return object.toString();
    }

    public static String serializeInvocationAsUsage(Invocation invocation) {
        Collection<Entity> associatedEntities = invocation.getAssociatedEntities();
        JSONArray usages = new JSONArray();

        for(URI activity: invocation.getActivities()) {
            associatedEntities.forEach(entity -> {
                JSONObject usage = new JSONObject();
                usage.put("activity", activity);
                usage.put("entity", entity.getId());
                usage.put("time", invocation.getGenerationTime());
                usages.put(usage);
            });
        }

        return usages.toString();
    }
}
