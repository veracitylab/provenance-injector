package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

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

    public void encode(Map<DataKind, List<Object>> data, PrintWriter out) {

        JSONObject json = new JSONObject();  // check whether we can just use json.put(new JSONObject(data));
        for (DataKind kind:data.keySet()) {
            String key = kind.name();
            List values = data.get(kind);
            if (values==null) values = Collections.emptyList();
            json.put(key,new JSONArray(values));
        }
        out.println(json);
//        out.println("{");
//        boolean f1 = true;
//        for (DataKind kind:data.keySet()) {
//            if (f1) {
//                f1 = false;
//            }
//            else {
//                out.print(',');
//            }
//            String key = kind.name();
//            out.print("\t\"");
//            out.print(key);
//            out.print("\":[");
//            List values = data.get(kind);
//            if (values==null) values = Collections.emptyList();
//            boolean f2 = true;
//            for (Object value:values) {
//                if (f2) {
//                    f2 = false;
//                }
//                else {
//                    out.print(',');
//                }
//                if (value instanceof Map) {
//                    out.print(flatten(value));
//                } else {
//                    out.print('\"');
//                    out.print(flatten(value));  // assume that we do not have to sanitise this, TODO !
//                    out.print('\"');
//                }
//            }
//            out.print("]");
//        }
//        out.println("}");

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
