package nz.ac.wgtn.veracity.provenance.injector.jee.instrumentation;

import nz.ac.wgtn.veracity.provenance.InstrumentationUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Aspect to record calls of interest.
 * @author jens dietrich
 */
public aspect MethodInvocationTracking {

    @Before("execution(* *.*(..)) && !within(nz.ac.wgtn.veracity.provenance..*)")
    public void trackMethodInvocation(JoinPoint joinPoint) {
        System.out.println("TRACKING: " + joinPoint);
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String descr = InstrumentationUtil.getDescriptor(method);

        InstrumentationUtil.trackMethodInvocation(className,methodName,descr);
    }


    @Before("execution(*.new(..)) && !within(nz.ac.wgtn.veracity.provenance..*)")
    public void trackConstructorInvocation(JoinPoint joinPoint) {
        System.out.println("TRACKING: " + joinPoint);
        Signature signature = joinPoint.getSignature();
        ConstructorSignature constructorSignature = (ConstructorSignature) signature;
        Constructor constructor = constructorSignature.getConstructor();
        String className = constructor.getDeclaringClass().getName();
        String descr = InstrumentationUtil.getDescriptor(constructor);

        InstrumentationUtil.trackMethodInvocation(className,"<init>",descr);
    }
}