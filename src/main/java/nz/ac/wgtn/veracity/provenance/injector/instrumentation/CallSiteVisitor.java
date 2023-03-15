package nz.ac.wgtn.veracity.provenance.injector.instrumentation;


import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.approv.jbind.EntityCreation;
import nz.ac.wgtn.veracity.approv.jbind.EntityRef;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import nz.ac.wgtn.veracity.provenance.injector.util.URIGenerator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.net.URI;
import java.util.Set;

public class CallSiteVisitor extends ClassVisitor {

    private String currentClass = null;

    private static final String RECORD_PARAMS_DESCRIPTOR = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V";


    protected CallSiteVisitor(ClassWriter writer) {
        super(Opcodes.ASM9, writer);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.currentClass = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        System.out.println("Visiting" + " " + this.currentClass + " " + name + " " + descriptor);

        Set<EntityCreation> createEntities = Bindings.getEntityCreations(this.currentClass.replace('/', '.'), name, descriptor);
        if (!createEntities.isEmpty()) {
            System.out.println("It has been done!");
            createEntities.forEach(entity -> {
                if (entity.getRef() == EntityRef.ARG) {
                    Type[] argTypes = Type.getArgumentTypes(descriptor);

                    // For index boosting non-static invocations
//                    int offset = (opcode == Opcodes.INVOKESTATIC) ? 0 : 1;
                    int varTableIndex = entity.getRefIndex();
                    Type arg = argTypes[varTableIndex];
                    System.out.println(arg);
                    boxAndStore(visitor, this.currentClass, name, descriptor, entity, arg, varTableIndex + 1);
                }
            });
        }

        return new InvocationTrackingInjector(visitor, this.currentClass, name, descriptor);
//        return new ArgumentCaptureInjector(visitor, access, name, descriptor, signature);


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
    private void boxAndStore(MethodVisitor visitor, String callingClass, String callingMethod, String callingDescriptor, EntityCreation entity, Type type, int index) {
        visitor.visitLdcInsn(callingClass);
        visitor.visitLdcInsn(callingMethod);
        visitor.visitLdcInsn(callingDescriptor);
        visitor.visitLdcInsn(entity.getEntity().toString());
        switch(type.getSort()) {
            case Type.BOOLEAN:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                break;
            case Type.CHAR:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                break;
            case Type.BYTE:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                break;
            case Type.SHORT:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                break;
            case Type.INT:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            case Type.FLOAT:
                visitor.visitVarInsn(Opcodes.FLOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                break;
            case Type.LONG:
                visitor.visitVarInsn(Opcodes.LLOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                break;
            case Type.DOUBLE:
                visitor.visitVarInsn(Opcodes.DLOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                break;
            case Type.ARRAY:
                throw new UnsupportedOperationException("Array capturing not yet supported");
            case Type.OBJECT:
                visitor.visitVarInsn(Opcodes.ALOAD, index);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected type %s", type));

        }

        visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                                     InvocationTrackingInjector.class.getName().replace('.', '/'),
                                     "recordParameter",
                                     RECORD_PARAMS_DESCRIPTOR,
                                     false);
    }
}
