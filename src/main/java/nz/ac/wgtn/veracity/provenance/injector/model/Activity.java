package nz.ac.wgtn.veracity.provenance.injector.model;

import nz.ac.wgtn.veracity.provenance.injector.serializer.InternalId;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public class Activity {

    private final String id;
    private final String type;
    private final Instant endTime;

    private Activity(String type, String id, Instant endTime) {
        this.id = id;
        this.type = type;
        this.endTime = endTime;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public static Activity create(String type) {
        return new Activity(type, UUID.randomUUID().toString(), Instant.now());
    }

    public static Activity create(String type, Instant endTime) {
        return new Activity(type, UUID.randomUUID().toString(), endTime);
    }

    public static Activity create(String type, String id) {
        return new Activity(type, id, Instant.now());
    }

    public static Activity create(String type, String id, Instant endTime) {
        return new Activity(type, id, endTime);
    }
}
