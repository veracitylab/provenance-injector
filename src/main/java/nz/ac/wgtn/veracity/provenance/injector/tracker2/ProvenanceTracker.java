package nz.ac.wgtn.veracity.provenance.injector.tracker2;

import java.util.List;

/**
 * Describes a datastore that can be used to track (meta)data of a computation collected by an instrumentation.
 * @param <T>  -- the kind of data that is being collected
 */
public interface ProvenanceTracker <T> {

    /**
     * Starts tracking, and returns a unique session id.
     * This id can be used to pick up provenance records.
     * @return
     */
    String start();

    /**
     * Finishes tracking for the current session.
     * @return
     */
    void finish() ;

    void track(T data) ;

    /**
     * Pick up the invocation logs, entries for this id should be cleared.
     * Throws an IllegalArgumentException if this is not a valid id.
     * If no data has been recorded for the respective id, this will return an empty list.
     */
    List<T> pickup(String id);

    /**
     * Clear all recorded provenance data for the respective id.
     * Return true if data was cleared, false otherwise.
     */
    boolean cull(String id);

}
