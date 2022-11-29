package nz.ac.wgtn.veracity.provenance.injector.rt;

import nz.ac.wgtn.veracity.provenance.injector.model.ProvenanceEvent;

import java.util.List;
import java.util.Set;

/**
 * Data structure to collect method invocation logs to compute coverage and similar data.
 * Note that this is a singleton, and the assumption is that all instrumented code has access to the same instance.
 * @author jens dietrich
 */

public abstract class InvocationTracker {

    // public to make this easier to test
    public static InvocationTracker DEFAULT = new ThreadLocalInvocationTracker();

    /**
     * Starts invocation tracking, and returns a unique id.
     * This id can be used to pick up records, e.g. this can be returned as a header to the client.
     * Filters can set headers only before the chain is invoked, therefore this is done in startInvocationTracking.
     * @return
     */
    public abstract String startInvocationTracking();

    /**
     * Finishes invocation tracking.
     * @return
     */
    public abstract void finishInvocationTracking() ;

    public abstract void track(ProvenanceEvent data) ;


    /**
     * Pick up the invocation logs, logs for this id should be cleared.
     */
    public abstract List<ProvenanceEvent> pickup(String id);

    public abstract Set<String> getAvailableTickets() ;

}
