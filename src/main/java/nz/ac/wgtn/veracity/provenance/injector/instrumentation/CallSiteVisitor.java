package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.approv.jbind.Bindings;
import nz.ac.wgtn.veracity.approv.jbind.EntityCreation;
import nz.ac.wgtn.veracity.approv.jbind.EntityRef;
import nz.ac.wgtn.veracity.provenance.injector.model.Entity;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
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

public class CallSiteVisitor extends ClassVisitor {

    private static final String RECORD_PARAMS_DESCRIPTOR = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V";
    private String currentClass = null;

    protected CallSiteVisitor(ClassWriter writer) {
        super(Opcodes.ASM9, writer);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.currentClass = name;
        if (name.startsWith("nz") || name.endsWith("/URL")) {
            System.out.println("CallSiteVisitor: visiting class " + name);  //DEBUG
        }
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        String taint = UUID.randomUUID().toString();
        AtomicBoolean captureReturnValue = new AtomicBoolean(false);

        Set<EntityCreation> createEntities = Bindings.getEntityCreations(this.currentClass.replace('/', '.'), name, descriptor);

        if (currentClass.startsWith("nz") || currentClass.endsWith("/URL")) {
            System.out.printf("visitMethod(name=%s, descriptor=%s) in class %s. createEntities=[%s].%n", name, descriptor, currentClass, createEntities.stream().map((EntityCreation ec) -> ec.getEntity().toString()).collect(Collectors.joining(", ")));
        }

        EntityRef entityRefType = EntityRef.RETURN;        //HACK: Not handling the fact that there could be multiple entities so multiple types
        if (!createEntities.isEmpty()) {
            for (EntityCreation entity: createEntities) {
                if (entity.getSourceRef() == EntityRef.ARG) {
                    Type[] argTypes = Type.getArgumentTypes(descriptor);

                    // For index boosting non-static invocations
                    int offset = (access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
                    int varTableIndex = entity.getSourceRefIndex();
                    Type arg = argTypes[varTableIndex];
                    boxAndStore(visitor, entity, arg, varTableIndex + offset, taint);
                    System.out.printf("Inserted call to recordParameter() at start of %s.%s (descriptor: %s). Taint/identifier: %s%n", currentClass, name, descriptor, taint);

                    entityRefType = entity.getTargetRef();
//                    if (entity.getTargetRef() == EntityRef.RETURN) {
                        captureReturnValue.set(true);
//                    } else if (entity.getTargetRef() == EntityRef.THIS) {
//                        visitor.visitVarInsn(Opcodes.ALOAD, 0);     // Load "this"
//                        visitor.visitLdcInsn(taint);
//                        InvocationTrackingInjector.injectCallToCaptureTarget(visitor);
//                        System.out.printf("Inserted call to captureTarget() just after recordParameter() insertion in %s.%s (descriptor: %s) to capture 'this'. Taint/identifier: %s%n", currentClass, name, descriptor, taint);
//                    }
                }
            }
        }

        return new InvocationTrackingInjector(visitor, this.currentClass, name, descriptor, captureReturnValue.get(), entityRefType, taint);
    }


    /**
     * Records a parameter and generates a new entity from said parameter.
     *
     * @param param recorded parameter
     */
    public static void recordParameter(String entityType, String identifier, Object param) {
        System.out.println("recordParameter(entityType=" + entityType + ", identifier=" + identifier + ", param=" + param + (param == null ? "" : " (type: " + param.getClass() + ")") + ")! Stacktrace:");   //DEBUG
        new Throwable().printStackTrace(System.out);  //DEBUG
        System.out.println("recordParameter(): end of stacktrace."); //DEBUG
        Entity entity = Entity.create(entityType, param);
        AssociationCacheRegistry.getCache().cacheEntity(identifier, entity, null);
    }


    /**
     * Utility method that boxes primitives and injects bytecode that will record the value using the recordParameter method. Boxing was used to have singular collection
     * method for recording. The values are obtained from the local variables, which is where method arguments are
     * located. The index is required to prevent "off-by-one" errors when recording values from static methods, as
     * dynamic methods store a reference to "this" in index 0.
     */
    private void boxAndStore(MethodVisitor visitor, EntityCreation entity, Type type, int index, String identifier) {
        visitor.visitLdcInsn(entity.getEntity().getFragment());
        visitor.visitLdcInsn(identifier);

        switch (type.getSort()) {
            case Type.BOOLEAN:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_BOOL, M_VO, D_BOOL, false);
                break;
            case Type.CHAR:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_CHAR, M_VO, D_CHAR, false);
                break;
            case Type.BYTE:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_BYTE, M_VO, D_BYTE, false);
                break;
            case Type.SHORT:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_SHORT, M_VO, D_SHORT, false);
                break;
            case Type.INT:
                visitor.visitVarInsn(Opcodes.ILOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_INTEGER, M_VO, D_INTEGER, false);
                break;
            case Type.FLOAT:
                visitor.visitVarInsn(Opcodes.FLOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_FLOAT, M_VO, D_FLOAT, false);
                break;
            case Type.LONG:
                visitor.visitVarInsn(Opcodes.LLOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_LONG, M_VO, D_LONG, false);
                break;
            case Type.DOUBLE:
                visitor.visitVarInsn(Opcodes.DLOAD, index);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, C_DOUBLE, M_VO, D_DOUBLE, false);
                break;
            case Type.ARRAY:
                throw new UnsupportedOperationException("Array capturing not yet supported");
            case Type.OBJECT:
                visitor.visitVarInsn(Opcodes.ALOAD, index);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected type %s", type));

        }

        visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                CallSiteVisitor.class.getName().replace('.', '/'),
                "recordParameter",
                RECORD_PARAMS_DESCRIPTOR,
                false
        );
    }
}
