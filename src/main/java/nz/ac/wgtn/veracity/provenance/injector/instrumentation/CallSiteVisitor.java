package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import java.util.Collection;

public class CallSiteVisitor extends ClassVisitor {

    private Collection<String> callsites = null;
    private String currentClass = null;
    private String currentMethod = null;

    protected CallSiteVisitor(Collection<String> callsites) {
        super(Opcodes.ASM9);
        this.callsites = callsites;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.currentClass = name.replace('/', '.');
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.currentMethod = this.currentClass + "::" + name + descriptor;
        return new MethodVisitor(Opcodes.ASM9) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                String callsite = String.format("%s -> %s::%s%s", currentMethod, owner ,name, descriptor);
                callsites.add(callsite);
            }
        };
    }
}
