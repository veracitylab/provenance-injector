package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.provenance.injector.InvocationTracker;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;

import java.net.URI;
import java.util.*;

public class InvocationTrackingInjector extends MethodVisitor {
    private final MethodVisitor visitor;
    private final String callingClass;
    private final String callingMethod;

    private final String callingDescriptor;

    private static Set<URI> detectedActivities = null;

    public InvocationTrackingInjector(MethodVisitor visitor, String callingClass, String callingMethod, String callingDescriptor) {
        super(Opcodes.ASM9, visitor);
        this.visitor = visitor;
        this.callingClass = callingClass;
        this.callingMethod = callingMethod;
        this.callingDescriptor = callingDescriptor;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        Set<URI> activities = Bindings.getActivities(owner.replace('/', '.'), name, descriptor);
        if (!activities.isEmpty()) {
            detectedActivities = activities;
            visitor.visitLdcInsn(this.callingClass);
            visitor.visitLdcInsn(this.callingMethod);
            visitor.visitLdcInsn(this.callingDescriptor);
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    InvocationTrackingInjector.class.getName().replace('.', '/'),
                    "recordActivity",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                    false);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
    public static void recordActivity(String callingClass, String callingMethod, String callingDescriptor) {
        String accessor = callingClass.replace('/', '.') + "::" + callingMethod;
        //TODO Replace with invocation tuple
        Invocation inv = Invocation.fromDescriptor(callingClass, callingMethod, callingDescriptor);
        InvocationTracker tracker = InvocationTracker.getInstance();
        tracker.addInvocation(accessor, detectedActivities);
    }
}
