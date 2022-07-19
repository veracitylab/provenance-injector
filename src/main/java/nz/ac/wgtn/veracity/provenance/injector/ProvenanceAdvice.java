package nz.ac.wgtn.veracity.provenance.injector;

import net.bytebuddy.asm.Advice;
import nz.ac.wgtn.veracity.provenance.injector.rt.DataKind;
import nz.ac.wgtn.veracity.provenance.injector.rt.InvocationTracker;
import nz.ac.wgtn.veracity.provenance.injector.rt.Util;

import java.lang.reflect.Method;

public class ProvenanceAdvice {
    @Advice.OnMethodEnter
    public static void intercept(@Advice.AllArguments Object[] args, @Advice.Origin Method method) {
        var className = method.getDeclaringClass().getName();
        var methodName = method.getName();
        var descr = Util.getDescriptor(method);
        InvocationTracker.DEFAULT.track(DataKind.invokedMethods,className + "::"+ methodName + descr);
    }
}
