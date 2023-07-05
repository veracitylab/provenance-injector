package nz.ac.wgtn.veracity.provenance.injector.model;

import org.json.JSONObject;

import java.net.URI;
import java.util.Collection;

/**
 * This class will be a tuple of URI's containing the calling method, the method instruction and the
 * associated activity.
 */
public class Invocation {
    private final URI caller;
    private final URI invocation;
    private final Collection<URI> activities;

    private Invocation(URI caller, URI invocation, Collection<URI> activities) {
        this.caller = caller;
        this.invocation = invocation;
        this.activities = activities;
    }

    /**
     * Creates a new invocation object
     *
     * @param caller caller of the method with provenance activities
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
}
