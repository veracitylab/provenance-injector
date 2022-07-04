package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Data structure to collect method invocation logs to compute coverage and similar data.
 * - Methods are recorded using the classname, methodname and descriptor (but not classloader and signature at the moment).
 * - Methods are tracked using a set, e.g. duplicated are removed, and the invocation order is not tracked.
 * - Invocations are tracked by thread using ThreadLocal. The idea is to init and release this with a filter.
 *   Unfortunately, this means that some methods invoked by other thread (e.g. if a parallel stream is used to process a request) are
 *   not tracked. This is a tradeoff between recall and precision.
 * - note that this is a singleton, and the assumption is that all instrumented code has access to the same instance.
 * @author jens dietrich
 */

public class ThreadLocalInvocationTracker extends InvocationTracker {

    private ThreadLocal<Map<DataKind,List<Object>>> trackedData = new ThreadLocal<>();
    private ThreadLocal<String> id = new ThreadLocal<>();

    public static final int MAX_OUTBOX_SIZE = 10_000;
    public static final int OUTBOX_EVICTION_BATCH_SIZE = 1_000;

    // when the request handling ends, recorded method (specs) are moved into a special pool,
    // waiting to be picked up
    // TODO: timeout mechanism to avoid memory leaks
    private Map<String,Map<DataKind,List<Object>>> outbox = null;

    public ThreadLocalInvocationTracker() {
        // use data structure that allows eviction of old data once a certain max size is exceeded
        this.outbox = Collections.synchronizedMap(new LinkedHashMap<>()); //new ConcurrentHashMap<>();
    }

    private static final AtomicLong ID_GENERATOR = new AtomicLong();


    @Override
    public String startInvocationTracking() {
        String ID = "" + ID_GENERATOR.incrementAndGet();
        this.id.set(ID);
        this.trackedData.set(new HashMap<>());
        return ID;
    }

    @Override
    public void track(DataKind kind, Object data) {

        // if (kind==DataKind.exceptions) System.out.println("Tracking " + kind + " -- " + data);
        Map<DataKind,List<Object>> tracked = trackedData.get();
        if (tracked!=null) { // can be null if invocation happens outside request handling
            List<Object> trackedForThisKind = tracked.computeIfAbsent(kind, k -> new ArrayList<>());
            if (!trackedForThisKind.contains(data)) // TODO: turn List<Object> to Set<Object>?
                trackedForThisKind.add(data);
        }
        if (tracked==null && kind==DataKind.invokedMethods) {
            SystemInvocationTracker.trackSystemInvocation((String)data);
        }
    }

    @Override
    public synchronized void finishInvocationTracking() {

        // maintenance of outbox to prior to tracking
        // TODO could use shaded version of guava maps if this is slow
        if (this.outbox.size()>MAX_OUTBOX_SIZE) {
            Iterator<String> ids = this.outbox.keySet().iterator();
            // using LinkedHashSet will ensure oldest entries are removed
            int count = 0;
            while (ids.hasNext() && count < OUTBOX_EVICTION_BATCH_SIZE) {
                count = count + 1;
                ids.next();
                ids.remove();
            }
        }

        this.outbox.put(id.get(),Collections.unmodifiableMap(trackedData.get()));
        this.trackedData.set(null);

    }

    // pick up the logs, clear memory
    @Override
    public Map<DataKind,List<Object>> pickup(String id) {
        //System.out.println("InvocationTracker:\tInvocationTracker:\t Pickup " + id);
        return outbox.remove(id);
    }

    @Override
    public Set<String> getAvailableTickets() {
        return Collections.unmodifiableSet(this.outbox.keySet());
    }

}
