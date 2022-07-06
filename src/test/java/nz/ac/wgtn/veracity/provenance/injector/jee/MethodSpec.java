package nz.ac.wgtn.veracity.provenance.injector.jee;

/**
 * Representation of methods encountered in invocations.
 * author jens dietrich
 */
public class MethodSpec {
    private String className = null;
    private String methodName = null;
    private String descriptor = null;

    public MethodSpec() {
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public MethodSpec(String className, String methodName, String descriptor) {
        this.className = className;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodSpec that = (MethodSpec) o;

        if (!className.equals(that.className)) return false;
        if (!methodName.equals(that.methodName)) return false;
        return descriptor.equals(that.descriptor);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + descriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodSpec{" +
            "className='" + className + '\'' +
            ", methodName='" + methodName + '\'' +
            ", descriptor='" + descriptor + '\'' +
            '}';
    }
}
