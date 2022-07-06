package nz.ac.wgtn.veracity.provenance;

import nz.ac.wgtn.veracity.provenance.injector.jee.rt.InvocationTracker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Instrumentation-related utilities.
 * @author jens dietrich
 */
public class InstrumentationUtil {

    public static void trackMethodInvocation(String className, String  methodName, String descriptor) {
        EnumSet<ProvenanceKind> kind = ProvenanceBinding.DEFAULT.inferProvenanceKindFromMethodInvocation(className,methodName,descriptor);
        if (kind.size()>0) {
            if (kind.size()>1 || !kind.contains(ProvenanceKind.NONE)) {
                Map<String,Object> location = new HashMap<>();
                location.put("className",className);
                location.put("methodName",methodName);
                location.put("descriptor",descriptor);
                for (ProvenanceKind k:kind) {
                    ProvenanceEvent event = new ProvenanceEvent();
                    event.setLocation(location);
                    event.setLocationKind(ProvenanceLocationKind.METHOD);
                    event.setKind(k);
                    InvocationTracker.DEFAULT.track(event);
                }
            }
        }
    }

    static boolean isArray(Object obj) {
        return obj!=null && obj.getClass().isArray();
    }

    public static String getDescriptor(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class paramType : method.getParameterTypes()) {
            sb.append(getJVMInternalName(paramType));
        }
        sb.append(')');
        sb.append(getJVMInternalName(method.getReturnType()));
        return sb.toString();
    }

    public static String getDescriptor(Constructor constructor) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class paramType : constructor.getParameterTypes()) {
            sb.append(getJVMInternalName(paramType));
        }
        sb.append(')');
        sb.append('V');
        return sb.toString();
    }
    public static String getJVMInternalName(Class cl) {
        if (cl==Void.TYPE)
            return "V";
        else if (cl==Integer.TYPE)
            return "I";
        else if (cl==Byte.TYPE)
            return "B";
        else if (cl==Long.TYPE)
            return "J";
        else if (cl==Double.TYPE)
            return "D";
        else if (cl==Float.TYPE)
            return "F";
        else if (cl== Character.TYPE)
            return "C";
        else if (cl==Short.TYPE)
            return "S";
        else if (cl==Boolean.TYPE)
            return "Z";
        else if (cl.isArray()) {
            return "[" + getJVMInternalName(cl.getComponentType());
        }
        else return "L" + cl.getName().replace('.','/') + ";";
    }
}
