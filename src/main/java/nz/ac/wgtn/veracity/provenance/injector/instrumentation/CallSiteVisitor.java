package nz.ac.wgtn.veracity.provenance.injector.instrumentation;

import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import nz.ac.wgtn.veracity.approv.jbind.Execution;


import java.util.Collection;
import java.util.HashSet;

public class CallSiteVisitor extends ClassVisitor {

    private final Collection<Execution> executions;
    private String currentClass = null;
    private String currentMethod = null;

    protected CallSiteVisitor() {
        super(Opcodes.ASM9);
        this.executions = new HashSet<>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.currentClass = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.currentMethod = this.currentClass + "::" + name + descriptor;
        return new MethodVisitor(Opcodes.ASM9) {

            @Override
            public void visitLineNumber(int line, Label start) {
                System.out.printf("Line number: %s, Label: %s%n", line, start);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                Execution execution = new Execution();
                //TODO: Support both "/" and "." delimiters
                execution.setOwner(owner.replace("/", "."));
                execution.setDescriptor(descriptor);
                execution.setName(name);
                executions.add(execution);
                System.out.printf("MI - Opcode: %s, Descriptor: %s%n", opcode, descriptor);
            }

            @Override
            public void visitVarInsn(final int opcode, final int varIndex) {
                System.out.printf("LV - Opcode: %s, idx: %s%n", opcode, varIndex);
            }
        };
    }

    public String getCurrentMethod() {
        return this.currentMethod;
    }

    public Collection<Execution> getExecutions() {
        return this.executions;
    }
}
