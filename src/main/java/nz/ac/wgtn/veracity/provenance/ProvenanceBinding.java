package nz.ac.wgtn.veracity.provenance;

import java.util.EnumSet;

/**
 * Infer provenance from observations made during executions.
 * Singleton -- special instances can be installed for testing, or constructed from files.
 * @author jens dietrich
 */
public abstract class ProvenanceBinding {

    public static ProvenanceBinding DEFAULT = null;

    public void install() {
        DEFAULT = this;
    }

    public EnumSet<ProvenanceKind> inferProvenanceKindFromMethodInvocation (String declaringClass,String methodName,String descriptor) {
        return EnumSet.of(ProvenanceKind.NONE);
    }

}
