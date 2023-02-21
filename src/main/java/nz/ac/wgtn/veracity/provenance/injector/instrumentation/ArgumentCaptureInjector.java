//package nz.ac.wgtn.veracity.provenance.injector.instrumentation;
//
//import nz.ac.wgtn.veracity.approv.jbind.Bindings;
//import nz.ac.wgtn.veracity.approv.jbind.EntityCreation;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//import org.objectweb.asm.Type;
//
//import java.util.Collection;
//
//public class ArgumentCaptureInjector extends MethodVisitor{
//
//    private final MethodVisitor visitor;
//    private final String methodName;
//    private final String methodDecriptor;
//    private final String methodSignature;
//
//    public ArgumentCaptureInjector(MethodVisitor visitor, int access, String name, String descriptor, String signature) {
//        super(Opcodes.ASM9, visitor);
//        this.visitor = visitor;
//        this.methodName = name;
//        this.methodDecriptor = descriptor;
//        this.methodSignature = signature;
//    }
//
//    @Override
//    public void visitCode() {
//
//        Type[] argTypes = Type.getArgumentTypes(this.methodDecriptor);
//        int[] argLocals = new int[argTypes.length];
//        for (int i = 0; i < argTypes.length; i++) {
//            argLocals[i] = newLocal(argTypes[i]);
//            mv.visitVarInsn(argTypes[i].getOpcode(Opcodes.ILOAD), i + 1);
//            mv.visitVarInsn(argTypes[i].getOpcode(Opcodes.ISTORE), argLocals[i]);
//        }
//
//        for( int i = 0; i < argTypes.length; i++) {
//            System.out.println("Argument " + i + " = " + argTypes[i].getClassName() + ":" + argLocals[i]);
//        }
//
//        super.visitCode();
//    }
//
//    private static int newLocal(Type type) {
//        switch (type.getSort()) {
//            case Type.BOOLEAN:
//            case Type.CHAR:
//            case Type.BYTE:
//            case Type.SHORT:
//            case Type.INT:
//                return Opcodes.ISTORE;
//            case Type.LONG:
//                return Opcodes.LSTORE;
//            case Type.FLOAT:
//                return Opcodes.FSTORE;
//            case Type.DOUBLE:
//                return Opcodes.DSTORE;
//            case Type.ARRAY:
//            case Type.OBJECT:
//                return Opcodes.ASTORE;
//            default:
//                throw new IllegalArgumentException("Invalid argument type: " + type);
//        }
//    }
//}
