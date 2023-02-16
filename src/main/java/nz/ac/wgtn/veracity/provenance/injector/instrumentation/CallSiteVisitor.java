package nz.ac.wgtn.veracity.provenance.injector.instrumentation;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CallSiteVisitor extends ClassVisitor {

    private String currentClass = null;

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
        return new InvocationTrackingInjector(visitor, this.currentClass, name, descriptor);
    }
}
