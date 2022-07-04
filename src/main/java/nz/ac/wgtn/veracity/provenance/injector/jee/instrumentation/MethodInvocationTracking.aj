package nz.ac.wgtn.veracity.provenance.injector.jee.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.jee.rt.DataKind;
import nz.ac.wgtn.veracity.provenance.injector.jee.rt.Util;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

/**
 * Aspect to record calls of interest.
 * @author jens dietrich
 */
public aspect MethodInvocationTracking {

    @Before("execution(* *.*(..)) && !within(nz.ac.wgtn.veracity.provenance.injector.jee.rt.*) && !within(nz.ac.wgtn.veracity.provenance.injector.jee.instrumentation.*)")
    public void trackUnsafeSystemSinks(JoinPoint joinPoint) {
        System.out.println("TRACKING: " + joinPoint);
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String descr = Util.getDescriptor(method);
        nz.ac.wgtn.veracity.provenance.injector.jee.rt.InvocationTracker.DEFAULT.track(DataKind.invokedMethods,className + "::"+ methodName + descr);
    }

}