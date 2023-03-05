package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.approv.jbind.EntityCreation;
import nz.ac.wgtn.veracity.approv.jbind.EntityPropagation;
import nz.ac.wgtn.veracity.approv.jbind.EntityRef;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import nz.ac.wgtn.veracity.provenance.injector.tracker.InvocationTracker;
import nz.ac.wgtn.veracity.provenance.injector.model.Invocation;
import nz.ac.wgtn.veracity.provenance.injector.util.URIGenerator;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.net.URI;
import java.util.*;

/**
 * Class responsible for injecting bytecode into subject classes should their methods contain instructions that are
 * associated with a provenance activity.
 */
public class InvocationTrackingInjector extends MethodVisitor {

    private static final String RECORD_DESCRIPTOR = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    private static final String RECORD_PARAMS_DESCRIPTOR = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V";
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
        Set<EntityCreation> createEntities = Bindings.getEntityCreations(owner.replace('/', '.'), name, descriptor);
        Set<EntityPropagation> propagations = Bindings.getEntityPropagations(owner.replace('/', '.'), name, descriptor);
        if (!createEntities.isEmpty() && owner.equals(this.callingClass)) {
            // Only add argument collection instrumentation in target implementations
            createEntities.forEach(entity -> {

                if (entity.getRef() == EntityRef.ARG) {
                    //TODO: Extract this into its own method to prevent bloating the lambda
                    Type[] argTypes = Type.getArgumentTypes(descriptor);

                    // For index boosting non-static invocations
                    int offset = (opcode == Opcodes.INVOKESTATIC) ? 0 : 1;
                    int varTableIndex = entity.getRefIndex() + offset;
                    Type arg = argTypes[varTableIndex];

                    // Perform boxing of primitives in order to use singular collector method
                    boxAndStore(owner, name, descriptor, entity, arg, varTableIndex);
                }
            });
        }

        if (!activities.isEmpty() && !owner.equals(this.callingClass)) {
            //TODO: Replace this with something better
            detectedActivities = activities;
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
    public static void recordActivity(String callingClass, String callingMethod, String callingDescriptor,
                                      String invOwner, String invMethod, String invDescriptor) {
        URI caller = URIGenerator.createMethodDescriptor(callingClass, callingMethod, callingDescriptor);
        URI invoked = URIGenerator.createMethodDescriptor(invOwner, invMethod, invDescriptor);
        Invocation inv = Invocation.fromMethodIsn(caller, invoked, detectedActivities);
        InvocationTracker tracker = InvocationTracker.getInstance();
        tracker.addInvocation(inv);
    }

    /**
     * Records a parameter and generates a new entity from said parameter.
     * @param param recorded parameter
     */
    public static void recordParameter(String callingClass, String callingMethod, String callingDescriptor, String entityDesc, Object param) {
        URI entitySource = URIGenerator.createMethodDescriptor(callingClass, callingMethod, callingDescriptor);
        Entity entity = Entity.from(entitySource, entityDesc, param);
        EntityTracker.getInstance().addEntity(entity);
        System.out.println("DEBUGGING: New entity detected");
    }

    /**
     * Utility method that boxes primitives and injects bytecode that will record the value using the recordParameter method.
     * Boxing was used to have singular collection method for recording.
     */
    private void boxAndStore(String callingClass, String callingMethod, String callingDescriptor, EntityCreation entity, Type type, int index) {
        this.visitor.visitLdcInsn(callingClass);
        this.visitor.visitLdcInsn(callingMethod);
        this.visitor.visitLdcInsn(callingDescriptor);
        this.visitor.visitLdcInsn(entity.getEntity().toString());
        switch(type.getSort()) {
            case Type.BOOLEAN:
                this.visitor.visitVarInsn(Opcodes.ILOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/Lang/Boolean;", false);
                break;
            case Type.CHAR:
                this.visitor.visitVarInsn(Opcodes.ILOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/Lang/Character;", false);
                break;
            case Type.BYTE:
                this.visitor.visitVarInsn(Opcodes.ILOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/Lang/Byte;", false);
                break;
            case Type.SHORT:
                this.visitor.visitVarInsn(Opcodes.ILOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/Lang/Short;", false);
                break;
            case Type.INT:
                this.visitor.visitVarInsn(Opcodes.ILOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/Lang/Integer;", false);
                break;
            case Type.FLOAT:
                this.visitor.visitVarInsn(Opcodes.FLOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/Lang/Float;", false);
                break;
            case Type.LONG:
                this.visitor.visitVarInsn(Opcodes.LLOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/Lang/Long;", false);
                break;
            case Type.DOUBLE:
                this.visitor.visitVarInsn(Opcodes.DLOAD, index);
                this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/Lang/Double;", false);
                break;
            case Type.ARRAY:
                throw new UnsupportedOperationException("Array capturing not yet supported");
            case Type.OBJECT:
                this.visitor.visitVarInsn(Opcodes.ALOAD, index);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected type %s", type));

        }

        this.visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                InvocationTrackingInjector.class.getName().replace('.', '/'),
                "recordParameter",
                RECORD_PARAMS_DESCRIPTOR,
                false);
    }
}
