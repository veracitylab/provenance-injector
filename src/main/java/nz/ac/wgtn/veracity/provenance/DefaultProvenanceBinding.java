package nz.ac.wgtn.veracity.provenance;

import java.util.EnumSet;

/**
 * Default provenance mapping to be used.
 * @author jens dietrich
 */
public abstract class DefaultProvenanceBinding extends ProvenanceBinding {

    public EnumSet<ProvenanceKind> inferProvenanceKindFromMethodInvocation (String declaringClass, String methodName, String descriptor) {
        declaringClass = declaringClass.replace('/','.');
        if (declaringClass.equals("java.lang.Runtime") && methodName.equals("exec")) {
            return EnumSet.of(ProvenanceKind.SYSTEM_COMMAND_EXECUTION);
        }

        // note that this is not very accurate as we do not instrument callsites (with virtual calls)
        // but executions (with devirtualised calls)
        // so we rely on implementation of java.sql.Statement naming classes *Statement

        // @TODO check descriptors to avoid some FPs
        if (declaringClass.endsWith("Statement")) {
            if (methodName.equals("execute") || methodName.equals("executeBatch")) {
                return EnumSet.of(ProvenanceKind.DATABASE_WRITE, ProvenanceKind.DATABASE_READ);
            }
            else if (methodName.equals("executeQuery")) {
                return EnumSet.of(ProvenanceKind.DATABASE_READ);
            }
            else if (methodName.equals("executeUpdate")) {
                return EnumSet.of(ProvenanceKind.DATABASE_WRITE);
            }
        }
        // TODO @PreparedStatement, @CallableStatement , hibernate & co, mongo, neo4j

        return EnumSet.of(ProvenanceKind.NONE);
    }

}
