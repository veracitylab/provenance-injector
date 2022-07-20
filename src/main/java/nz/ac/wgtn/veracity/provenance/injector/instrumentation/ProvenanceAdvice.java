package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import net.bytebuddy.asm.Advice;
import nz.ac.wgtn.veracity.provenance.injector.InstrumentationUtil;

import java.lang.reflect.Method;

public class ProvenanceAdvice {
    @Advice.OnMethodEnter
    public static void intercept(@Advice.AllArguments Object[] args, @Advice.Origin Method method) {
        var className = method.getDeclaringClass().getName();
        var methodName = method.getName();
        var descr = InstrumentationUtil.getDescriptor(method);
        InstrumentationUtil.trackMethodInvocation(className, methodName, descr);
    }
}
