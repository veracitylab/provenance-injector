package nz.ac.wgtn.veracity.provenance.injector.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Describes a datastore that can be used to track (meta)data of a computation collected by an instrumentation.
 * Tracked invocations that occur outside of any request ({@link #start()}-{@link #finish()} pair) can be
 * picked up using the bucket ID {@link #NO_ACTIVE_REQUEST_ID} -- see {@link #finish()}.
 * @param <T>  -- the kind of data that is being collected
 */
public class ThreadLocalProvenanceTracker<T> implements ProvenanceTracker<T> {

    // Class invariants:
    // - noActiveRequestTrackedData.get() and trackedData.get() are never null (@NonNull omitted to minimise deps)
    // - trackedData:
    //     - initially, and after calling finish(), references noActiveRequestTrackedData's contents
    //     - start() allocates a new list
    // - id.get() == null iff trackedData.get() == noActiveRequestTrackedData.get(). Indicates "outside of any request".

    private final ThreadLocal</*@NonNull*/ List<T>> noActiveRequestTrackedData = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal</*@NonNull*/ List<T>> trackedData = ThreadLocal.withInitial(noActiveRequestTrackedData::get);
    private final ThreadLocal<String> id = new ThreadLocal<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private final Map<String,List<T>> outbox = Collections.synchronizedMap(new LinkedHashMap<>());

    public String start() {
        if (id.get() != null) {
            throw new RuntimeException("ThreadLocalProvenanceTracker.start() called a second time on thread without intervening finish()");
        }

        String generatedId = "" + ID_GENERATOR.incrementAndGet();
        System.out.println("ThreadLocalProvenanceTracker.start() called! Returning id " + generatedId);  //DEBUG
        this.id.set(generatedId);
        this.trackedData.set(new ArrayList<>());
        return generatedId;
    }

    /**
     * Call once per {@link #start()} call to make in-request tracked invocations available for {@link #pickup(String)}.
     * Also, for any thread in which tracked invocations occur outside of any request
     * ({@link #start()}-{@link #finish()} pair), call at most once to make those invocations available for
     * {@link #pickup(String)} under the ID {@link #NO_ACTIVE_REQUEST_ID}.
     * Note: For these non-request invocations, calling {@code { finish(); pickup(); cull(); }}
     * must be synchronised across threads.
     * TODO: There is currently no way to determine which threads have outstanding non-request invocations.
     */
    public void finish() {
        System.out.println("ThreadLocalProvenanceTracker.finish() called for ID " + getNiceId() + "! Will move " + trackedData.get().size() + " invocations to outbox.");  //DEBUG
        this.outbox.put(getNiceId(), Collections.unmodifiableList(trackedData.get()));
        if (trackedData.get() == noActiveRequestTrackedData.get()) {
            // We are outside of any request.
            noActiveRequestTrackedData.set(new ArrayList<>());  // Don't touch the list in the outbox
        }
        trackedData.set(noActiveRequestTrackedData.get());
        this.id.remove();
    }

    /**
     * Called from instrumented code at any time.
     * Note: There is no guarantee that {@link #start()} has been called beforehand.
     * E.g., tracked invocations may occur before or between requests. All such invocations will be added to the
     * {@link #NO_ACTIVE_REQUEST_ID} bucket.
     * @param data
     */
    public void track(T data) {
        System.out.println("ThreadLocalProvenanceTracker.track(" + data + ") called for ID " + getNiceId() + "!");  //DEBUG
        trackedData.get().add(data);
        System.out.println("ThreadLocalProvenanceTracker.track(): There are now " + trackedData.get().size() + " invocations tracked so far for ID " + getNiceId() + ".");  //DEBUG
    }

    public List<T> pickup(String id) {
        List<T> data = outbox.get(id);
        if (data==null) {
            throw new IllegalArgumentException("no data available for key " + id);
        }
        System.out.println("ThreadLocalProvenanceTracker.pickup(" + id + ") called! Will send " + data.size() + " invocations back.");  //DEBUG
        return data;
    }

    /**
     * Clear all recorded provenance data for the respective id.
     * Return true if data was cleared, false otherwise.
     */
    public boolean cull(String id) {
        System.out.println("ThreadLocalProvenanceTracker.cull(" + id + ") called! Removing " + (outbox.containsKey(id) ? "" + outbox.get(id).size() : "<NULL>") + " invocations from outbox.");  //DEBUG
        return outbox.remove(id)!=null;
    }

    private String getNiceId() {
        return id.get() != null ? id.get() : NO_ACTIVE_REQUEST_ID;
    }
}
