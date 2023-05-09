package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TargetCollectingInjector extends MethodVisitor {

    private final MethodVisitor visitor;
    private final String clazz;
    private final String method;

    private final String descriptor;

    public TargetCollectingInjector(MethodVisitor visitor, String callingClass, String callingMethod, String callingDescriptor) {
        super(Opcodes.ASM9, visitor);
        this.visitor = visitor;
        this.clazz = callingClass;
        this.method = callingMethod;
        this.descriptor = callingDescriptor;
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);

        switch (opcode) {
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.DRETURN:
            case Opcodes.RETURN:
                System.out.println("A return has been had!");
        }
    }

}
