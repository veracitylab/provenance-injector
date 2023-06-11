package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import nz.ac.wgtn.veracity.provenance.injector.tracker.EntityTracker;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TargetCollectingInjector extends MethodVisitor {

    private final MethodVisitor visitor;
    private final String identifier;

    public TargetCollectingInjector(MethodVisitor visitor, String identifier) {
        super(Opcodes.ASM9, visitor);
        this.visitor = visitor;
        this.identifier = identifier;
    }

    @Override
    public void visitInsn(int opcode) {
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
        visitor.visitLdcInsn(this.identifier);
        visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                TargetCollectingInjector.class.getName().replace('.','/'),
                "captureTarget",
                "(Ljava/lang/Object;Ljava/lang/String;)V",
                false
        );
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
