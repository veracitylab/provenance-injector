package nz.ac.wgtn.veracity.provenance.injector.rt;

import nz.ac.wgtn.veracity.provenance.injector.model.ProvenanceEvent;
import nz.ac.wgtn.veracity.provenance.injector.model.ProvenanceKind;
import nz.ac.wgtn.veracity.provenance.injector.model.ProvenanceLocationKind;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON-based encoded. Uses a shaded version of json.org (to avoid conflicts).
 * @author jens dietrich
 */
public class JSONEncoder implements Encoder {

    public String getContentType() {
        return "application/json";
    }

    public static final String KEY_KIND = "kind";
    public static final String KEY_LOCATION_KIND = "locationKind";
    public static final String KEY_LOCATION = "location";

    public void encode(List<ProvenanceEvent> data, PrintWriter out) {
        JSONArray array = new JSONArray();
        for (ProvenanceEvent event:data) {
            JSONObject obj = new JSONObject();
            obj.put(KEY_KIND,event.getKind().toString());
            obj.put(KEY_LOCATION_KIND,event.getLocationKind().toString());
            JSONObject location = new JSONObject(event.getLocation());
            obj.put(KEY_LOCATION,location);
            array.put(obj);
        }
        out.println(array);
    }

    // Utility to be used for testing, keep here for consistenct
    public static ProvenanceEvent decodeObject(JSONObject obj) {
        ProvenanceEvent event = new ProvenanceEvent();
        event.setKind(ProvenanceKind.valueOf(obj.getString(KEY_KIND)));
        event.setLocationKind(ProvenanceLocationKind.valueOf(obj.getString(KEY_LOCATION_KIND)));
        event.setLocation(obj.getJSONObject(KEY_LOCATION).toMap());
        return event;
    }

    public static List<ProvenanceEvent> decodeArray(JSONArray arr) {
        List<ProvenanceEvent> events = new ArrayList<>(arr.length());
        for (int i=0;i<arr.length();i++) {
            events.add(decodeObject(arr.getJSONObject(i)));
        }
        return events;
    }

    private static String flatten(Object value) {
        if (value instanceof Collection) {
            return  ((List)value).stream()
                .map(v -> flatten(v))
                .collect(Collectors.joining("\\n"))
                .toString();
        } else if (value instanceof Map) {
            List<String> entries = new ArrayList<>();
            for(Object key: ((Map) value).keySet()) {
                String entry = "\"" + key.toString() + "\": ";
                entry += "[\"" + flatten(((Map) value).get(key)) + "\"]";
                entries.add(entry);
            }
            return "{" + entries.stream().collect(Collectors.joining(",\n")).toString() + "}";
        }
        else {
            return  Objects.toString(value) ;
        }
    }
}
