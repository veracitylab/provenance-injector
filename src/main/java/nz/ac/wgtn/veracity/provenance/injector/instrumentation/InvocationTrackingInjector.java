package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.approv.jbind.Bindings;
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

    public InvocationTrackingInjector(MethodVisitor visitor, String callingClass, String callingMethod, String callingDescriptor) {
        super(Opcodes.ASM9, visitor);
        this.visitor = visitor;
        this.callingClass = callingClass;
        this.callingMethod = callingMethod;
        this.callingDescriptor = callingDescriptor;
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
}
