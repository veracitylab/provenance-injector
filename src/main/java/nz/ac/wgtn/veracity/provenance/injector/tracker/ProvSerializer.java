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

    public static String serializeActivities(Collection<URI> activities) {
        JSONObject serializedActivities = new JSONObject();

        activities.forEach(activity -> {
            JSONObject serialized = serializeActivity(activity);
            serializedActivities.put(activity.toString(), serialized);
        });

        JSONObject container = new JSONObject();
        container.put("activity", serializedActivities);

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

    private static JSONObject serializeActivity(URI activity) {
        JSONObject object = new JSONObject();
        String[] typeRepr = activity.toString().split("/");

        if (typeRepr.length > 1) {
            object.put("prov:type", typeRepr[typeRepr.length - 1]);
        }

        return object;
    }
}
