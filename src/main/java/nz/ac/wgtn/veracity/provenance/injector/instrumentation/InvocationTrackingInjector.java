package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import nz.ac.wgtn.veracity.provenance.injector.tracker.InvocationTracker;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.util.URIGenerator;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for injecting bytecode into subject classes should their methods contain instructions that are
 * associated with a provenance activity.
 */
public class InvocationTrackingInjector extends MethodVisitor {

    private static final String RECORD_DESCRIPTOR = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    private final MethodVisitor visitor;
    private final String callingClass;
    private final String callingMethod;

    private final String callingDescriptor;

    private final boolean trackMethodReturn;

    private final String taint;

    public InvocationTrackingInjector(MethodVisitor visitor, String callingClass, String callingMethod, String callingDescriptor, boolean trackMethodReturn, String taint) {
        super(Opcodes.ASM9, visitor);
        this.visitor = visitor;
        this.callingClass = callingClass;
        this.callingMethod = callingMethod;
        this.callingDescriptor = callingDescriptor;
        this.trackMethodReturn = trackMethodReturn;
        this.taint = taint;
    }

    /**
     * Visits a method instruction of an instrumented class. If there are provenance activities assosciated with this
     * method instruction then inject bytecode that will record the invocation of the subject method`
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either
     *     INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     * @param owner the internal name of the method's owner class (see {@link
     *     Type#getInternalName()}).
     * @param name the method's name.
     * @param descriptor the method's descriptor (see {@link Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        Set<URI> activities = Bindings.getActivities(owner.replace('/', '.'), name, descriptor);

        if (!activities.isEmpty() && !owner.equals(this.callingClass)) {
            visitor.visitLdcInsn(activities.stream().map(URI::toString).collect(Collectors.joining(";")));
            visitor.visitLdcInsn(this.callingClass);
            visitor.visitLdcInsn(this.callingMethod);
            visitor.visitLdcInsn(this.callingDescriptor);
            visitor.visitLdcInsn(owner);
            visitor.visitLdcInsn(name);
            visitor.visitLdcInsn(descriptor);

            // Static call to instrumentation classes.
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    InvocationTrackingInjector.class.getName().replace('.', '/'),
                    "recordActivity",
                    RECORD_DESCRIPTOR,
                    false);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(int opcode) {
        if(!this.trackMethodReturn) {
            super.visitInsn(opcode);
            return;
        }

        switch (opcode) {
            case Opcodes.LRETURN:
                visitor.visitInsn(Opcodes.DUP2);
                visitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Long",
                        "valueOf",
                        "(J)Ljava/lang/Long;",
                        false
                );
                invokeCollector(visitor);
                break;
            case Opcodes.DRETURN:
                visitor.visitInsn(Opcodes.DUP2);
                visitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Double",
                        "valueOf",
                        "(D)Ljava/lang/Double;",
                        false);
                invokeCollector(visitor);
                break;
            case Opcodes.IRETURN:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false
                );
                invokeCollector(visitor);
                break;
            case Opcodes.FRETURN:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Float",
                        "valueOf",
                        "(F)Ljava/lang/Float;",
                        false
                );
                invokeCollector(visitor);
                break;
            case Opcodes.ARETURN:
                visitor.visitInsn(Opcodes.DUP);
                invokeCollector(visitor);
                break;

        }
        super.visitInsn(opcode);
    }

    private void invokeCollector(MethodVisitor visitor) {
        visitor.visitLdcInsn(this.taint);
        visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                InvocationTrackingInjector.class.getName().replace('.','/'),
                "captureTarget",
                "(Ljava/lang/Object;Ljava/lang/String;)V",
                false
        );
    }

    /**
     * Records an invocation of a method instruction that is associated with a particular method, storing it in the
     * invocation tracker
     *
     * @param callingClass class calling a provenance activity method
     * @param callingMethod method of the calling class calling the activity method
     * @param callingDescriptor descriptor of the method calling the activity method
     * @param invOwner activity method class
     * @param invMethod activity method name
     * @param invDescriptor activity method descriptor
     */
    public static void recordActivity(String activities, String callingClass, String callingMethod, String callingDescriptor,
                                      String invOwner, String invMethod, String invDescriptor) {
        Set<URI> detectedActivities =  Arrays.stream(activities.split(";")).map(URI::create).collect(Collectors.toSet());
        URI caller = URIGenerator.createMethodDescriptor(callingClass, callingMethod, callingDescriptor);
        URI invoked = URIGenerator.createMethodDescriptor(invOwner, invMethod, invDescriptor);
        Invocation inv = Invocation.fromMethodIsn(caller, invoked, detectedActivities);
        InvocationTracker tracker = InvocationTracker.getInstance();
        tracker.addInvocation(inv);
    }

    /**
     * This method is used to capture a target value.
     * @param target The target value to be captured
     * @param taint The unique taint given to the target value, which is used when associating entities with
     *                   the target with a respective entity.
     */
    public static void captureTarget(Object target, String taint) {
        EntityTracker.getInstance().addItem(taint, null, target);
        System.out.printf("DEBUG: Got target of type: %s%n", target.getClass().getName());
        System.out.printf("DEBUG: Return value is: %s%n", target);
    }
}
