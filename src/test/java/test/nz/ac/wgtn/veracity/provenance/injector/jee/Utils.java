package test.nz.ac.wgtn.veracity.provenance.injector.jee;

import nz.ac.wgtn.veracity.provenance.injector.jee.rt.DataKind;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Misc utils uses across tests.
 * @author jens dietrich
 */
public class Utils {

    static List<MethodSpec> extractInvocations(JSONObject json) {
        JSONArray invocationData = json.getJSONArray(DataKind.invokedMethods.name());
        List<MethodSpec> methods = new ArrayList<>(invocationData.length());
        for (int i=0;i<invocationData.length();i++) {
            String def = invocationData.getString(i);
            String[] parts = def.split("::");
            MethodSpec methodSpec = new MethodSpec(parts[0],parts[1].substring(0,parts[1].indexOf('(')),parts[1].substring(parts[1].indexOf('(')));
            methods.add(methodSpec);
        }
        return methods;
    }


}


