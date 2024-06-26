package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.approv.jbind.EntityCreation;
import nz.ac.wgtn.veracity.approv.jbind.EntityRef;
import nz.ac.wgtn.veracity.provenance.injector.model.Activity;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_BOOL;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_BYTE;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_CHAR;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_DOUBLE;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_FLOAT;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_INTEGER;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_LONG;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.C_SHORT;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_BOOL;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_BYTE;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_CHAR;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_DOUBLE;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_FLOAT;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_INTEGER;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_LONG;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.D_SHORT;
import static nz.ac.wgtn.veracity.provenance.injector.util.Consts.M_VO;

/**
 * Class responsible for injecting bytecode into subject classes should their methods contain instructions that are
 * associated with a provenance activity.
 */
public class InvocationTrackingInjector extends MethodVisitor {

    private static final String RECORD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/String;)V";
    private final MethodVisitor visitor;
    private final String callingClass;
    private final String ownMethodName;     //DEBUG
    private final String ownMethodDescriptor;   //DEBUG
    private final boolean trackMethodReturn;
    private final EntityRef targetEntityRef;    // Only used if trackMethodReturn == true; doesn't yet handle EntityRef.ARG

    private final String taint;

    public InvocationTrackingInjector(MethodVisitor visitor, String callingClass, String ownMethodName, String ownMethodDescriptor, boolean trackMethodReturn, EntityRef targetEntityRef, String taint) {
        super(Opcodes.ASM9, visitor);
        this.visitor = visitor;
        this.callingClass = callingClass;
        this.ownMethodName = ownMethodName;
        this.ownMethodDescriptor = ownMethodDescriptor;
        this.trackMethodReturn = trackMethodReturn;
        this.targetEntityRef = targetEntityRef;
        this.taint = taint;

        if (trackMethodReturn) {
            System.out.println("InvocationTrackingInjector ctor called with trackMethodReturn=" + trackMethodReturn + " for class " + callingClass + "!");  //DEBUG
        }
    }

    /**
     * Visits a method instruction of an instrumented class. If there are provenance activities assosciated with this
     * method instruction then inject bytecode that will record the invocation of the subject method`
     *
     * @param opcode      the opcode of the type instruction to be visited. This opcode is either
     *                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     * @param owner       the internal name of the method's owner class (see {@link
     *                    Type#getInternalName()}).
     * @param name        the method's name.
     * @param descriptor  the method's descriptor (see {@link Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        //DEBUG
        boolean debugLog = false;
        if (name.endsWith("dummyStaticMethod")) {
            debugLog = true;
            System.out.println("Visiting method call instruction in dummyStaticMethod()! Call is to " + owner + "." + name + "() with desc " + descriptor + ", callingClass=" + callingClass);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        Set<URI> activities = Bindings.getActivities(owner.replace('/', '.'), name, descriptor);
        Set<EntityCreation> entities = Bindings.getEntityCreations(owner.replace('/', '.'), name, descriptor);

        //DEBUG
        if (debugLog) {
            System.out.println("Activities: " + activities);
            System.out.println("Entities: " + entities);
        }

        if (!activities.isEmpty() && !owner.equals(this.callingClass)) {
            if (debugLog) {
                System.out.println("Looks like we're definitely gonna add a call to recordActivity()!");    //DEBUG
            }

            boolean injectDummy = true;

            // Constructors (which are named "<init>") technically have void return type, but the top-of-stack will
            // nevertheless contain the target object that we want (due to an earlier DUP).
            Type returnType = name.equals("<init>") ? Type.getType(Object.class) : Type.getReturnType(descriptor);

            if (!returnType.equals(Type.VOID_TYPE) && !entities.isEmpty()) {

                System.out.printf("DEBUG: Called method: %s (desc: %s)%n", name, descriptor);
                System.out.printf("DEBUG: Non void type of: %s%n", returnType);

                boxReturnValue(visitor, returnType);
                injectDummy = false;
            }

            if (injectDummy) {
                visitor.visitInsn(Opcodes.ACONST_NULL);
            }

            //TODO: Change this instruction to create a new string array to prevent costly joining operations during instrumentation
            visitor.visitLdcInsn(activities.stream().map(URI::getFragment).collect(Collectors.joining(";")));

            // Static call to instrumentation classes.
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    InvocationTrackingInjector.class.getName().replace('.', '/'),
                    "recordActivity",
                    RECORD_DESCRIPTOR,
                    false);

            if (debugLog) {
                System.out.println("Definitely added in a call to recordActivity()! injectDummy=" + injectDummy);   //DEBUG
            }
        }
    }

    /**
     * This method is invoked conditionally to capture the return value of a method, if instructed to do so. This is
     * primarily used to capture the return value of method instructions when an entity creation is present, and we
     * want to associate the entity with an object.
     *
     * @param opcode Instruction opcode
     */
    @Override
    public void visitInsn(int opcode) {
        if (!this.trackMethodReturn) {
            super.visitInsn(opcode);
            return;
        }

        if (targetEntityRef == EntityRef.RETURN) {
            switch (opcode) {
                case Opcodes.LRETURN:
                    visitor.visitInsn(Opcodes.DUP2);
                    visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_LONG, M_VO, D_LONG, false);
                    invokeMethodArgumentCollector(visitor);
                    break;
                case Opcodes.DRETURN:
                    visitor.visitInsn(Opcodes.DUP2);
                    visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_DOUBLE, M_VO, D_DOUBLE, false);
                    invokeMethodArgumentCollector(visitor);
                    break;
                case Opcodes.IRETURN:
                    visitor.visitInsn(Opcodes.DUP);
                    visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_INTEGER, M_VO, D_INTEGER, false);
                    invokeMethodArgumentCollector(visitor);
                    break;
                case Opcodes.FRETURN:
                    visitor.visitInsn(Opcodes.DUP);
                    visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_FLOAT, M_VO, D_FLOAT, false);
                    invokeMethodArgumentCollector(visitor);
                    break;
                case Opcodes.ARETURN:
                    visitor.visitInsn(Opcodes.DUP);
                    invokeMethodArgumentCollector(visitor);
                    break;
            }
        } else if (targetEntityRef == EntityRef.THIS) {
            switch (opcode) {
                case Opcodes.LRETURN:
                case Opcodes.DRETURN:
                case Opcodes.IRETURN:
                case Opcodes.FRETURN:
                case Opcodes.ARETURN:
                case Opcodes.RETURN:        // This is what a constructor uses to return
                    visitor.visitVarInsn(Opcodes.ALOAD, 0);
                    invokeMethodArgumentCollector(visitor);
                    System.out.println("The just-inserted call to captureTarget() was for 'this', not the return value!");   //DEBUG
                    break;
            }
        } else {
            throw new RuntimeException("Not yet handling EntityRef.ARG");
        }

        super.visitInsn(opcode);
    }

    private void invokeMethodArgumentCollector(MethodVisitor visitor) {
        visitor.visitLdcInsn(this.taint);
        injectCallToCaptureTarget(visitor);

        System.out.printf("Inserted call to captureTarget() before RETURN inside %s.%s (descriptor: %s). Taint/identifier: %s%n", callingClass, ownMethodName, ownMethodDescriptor, taint);
    }

    static public void injectCallToCaptureTarget(MethodVisitor visitor) {
        visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                InvocationTrackingInjector.class.getName().replace('.', '/'),
                "captureTarget",
                "(Ljava/lang/Object;Ljava/lang/String;)V",
                false
        );
    }

    private void boxReturnValue(MethodVisitor visitor, Type returnType) {
        switch (returnType.getSort()) {
            case Type.BOOLEAN:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_BOOL, M_VO, D_BOOL, false);
                break;
            case Type.CHAR:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_CHAR, M_VO, D_CHAR, false);
                break;
            case Type.BYTE:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_BYTE, M_VO, D_BYTE, false);
                break;
            case Type.SHORT:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_SHORT, M_VO, D_SHORT, false);
                break;
            case Type.INT:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_INTEGER, M_VO, D_INTEGER, false);
                break;
            case Type.FLOAT:
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_FLOAT, M_VO, D_FLOAT, false);
                break;
            case Type.LONG:
                visitor.visitInsn(Opcodes.DUP2);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_LONG, M_VO, D_LONG, false);
                break;
            case Type.DOUBLE:
                visitor.visitInsn(Opcodes.DUP2);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_DOUBLE, M_VO, D_DOUBLE, false);
                break;
            case Type.ARRAY:
                throw new UnsupportedOperationException("Array capturing not yet supported");
            case Type.OBJECT:
                visitor.visitInsn(Opcodes.DUP);
                break;
        }
    }


    /**
     * Records an invocation of a method instruction that is associated with a particular method, storing it in the
     * invocation tracker
     */
    public static void recordActivity(Object capturedReturn, String activityTypes) {
        System.out.println("recordActivity(capturedReturn=" + capturedReturn + (capturedReturn == null ? "" : " (type: " + capturedReturn.getClass() + ")") + ", activityTypes=" + activityTypes + ")! Stacktrace:");   //DEBUG
        new Throwable().printStackTrace(System.out);  //DEBUG
        System.out.println("recordActivity(): end of stacktrace."); //DEBUG
        List<Activity> activities = Arrays.stream(activityTypes.split(";"))
                .map(Activity::create)
                .collect(Collectors.toList());
        Invocation inv = Invocation.create(activities);
        AssociationCacheRegistry.getCache().cacheInvocation(inv, capturedReturn);
    }

    /**
     * This method is used to capture a target value.
     *
     * @param target The target value to be captured
     * @param taint  The unique taint given to the target value, which is used when associating entities with the target
     *               with a respective entity.
     */
    public static void captureTarget(Object target, String taint) {
        AssociationCacheRegistry.getCache().cacheEntity(taint, null, target);
        System.out.printf("DEBUG: captureTarget(): Got target of type: %s%n", target.getClass().getName());
        System.out.printf("DEBUG: captureTarget(): Return value is: %s%n", target);
    }
}
