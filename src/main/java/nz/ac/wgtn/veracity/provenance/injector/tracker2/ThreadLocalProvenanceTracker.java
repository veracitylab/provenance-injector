package nz.ac.wgtn.veracity.provenance.injector.tracker2;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Describes a datastore that can be used to track (meta)data of a computation collected by an instrumentation.
 * @param <T>  -- the kind of data that is being collected
 */
public class ThreadLocalProvenanceTracker<T> implements ProvenanceTracker<T> {


    private ThreadLocal<List<T>> trackedData = new ThreadLocal<>();
    private ThreadLocal<String> id = new ThreadLocal<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private Map<String,List<T>> outbox = Collections.synchronizedMap(new LinkedHashMap<>());;

    public String start() {
        String ID = "" + ID_GENERATOR.incrementAndGet();
        this.id.set(ID);
        this.trackedData.set(new ArrayList<>());
        return ID;
    }

    public void finish() {
        this.outbox.put(id.get(),Collections.unmodifiableList(trackedData.get()));
        this.trackedData.set(null);
        this.id.set(null);
    }

    public void track(T data) {
        trackedData.get().add(data);
    }

    public List<T> pickup(String id) {
        List<T> data = outbox.get(id);
        if (data==null) {
            throw new IllegalArgumentException("no data available for key " + id);
        }
        return data;
    }

    /**
     * Clear all recorded provenance data for the respective id.
     * Return true if data was cleared, false otherwise.
     */
    public boolean cull(String id) {
        return outbox.remove(id)!=null;
    }

}
