package nz.ac.wgtn.veracity.provenance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Datastructure to represent a single provenance event.
 * @author jens dietrich
 */
public class ProvenanceEvent {
    private ProvenanceLocationKind locationKind = ProvenanceLocationKind.UNKNOWN;
    private Map<String,Object> location = new HashMap<>(); // such as method coordinates
    private ProvenanceKind kind = ProvenanceKind.NONE;

    public ProvenanceLocationKind getLocationKind() {
        return locationKind;
    }

    public void setLocationKind(ProvenanceLocationKind locationKind) {
        this.locationKind = locationKind;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }

    public ProvenanceKind getKind() {
        return kind;
    }

    public void setKind(ProvenanceKind kind) {
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProvenanceEvent that = (ProvenanceEvent) o;
        return locationKind == that.locationKind && Objects.equals(location, that.location) && kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationKind, location, kind);
    }
}
