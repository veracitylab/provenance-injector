package nz.ac.wgtn.veracity.provenance.injector.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class will be a tuple of URI's containing the calling method, the method instruction and the
 * associated activity.
 */
public class Invocation {
    private final URI caller;
    private final URI invocation;
    private final Collection<URI> activities;

    private final Instant generationTime;

    private Invocation(URI caller, URI invocation, Collection<URI> activities) {
        this.caller = caller;
        this.invocation = invocation;
        this.activities = activities;
        this.generationTime = Instant.now();
    }

    /**
     * Creates a new invocation object
     *
     * @param caller     caller of the method with provenance activities
     * @param invocation method with provenance activities bound
     * @param activities activities related to the method invocation
     * @return Invocation object as a 3-URI tuple
     */
    public static Invocation fromMethodIsn(URI caller, URI invocation, Collection<URI> activities) {
        return new Invocation(caller, invocation, activities);
    }


    public String toJSON() {
        JSONObject json = new JSONObject();
        json.put("caller", this.caller);
        json.put("invocation", this.invocation);
        json.put("activities", this.activities);
        return json.toString();
    }

    public List<String> getActivities() {
        return activities.stream().map(activity -> {
            JSONObject json = new JSONObject();
            json.put("id", activity);
            String[] repr = activity.toString().split("/");
            String typeRepr = repr[repr.length - 1];
            JSONObject type = new JSONObject();
            type.put("prov:type", typeRepr);

            JSONArray attrs = new JSONArray();
            attrs.put(type);
            json.put("attributes", attrs);
            json.put("endTime", this.generationTime.toString());
            return json.toString();
        }).collect(Collectors.toList());
    }
}
