package nz.ac.wgtn.veracity.provenance.injector;

import nz.ac.wgtn.veracity.provenance.injector.model.ProvenanceKind;

import java.util.EnumSet;

/**
 * Default provenance mapping to be used.
 * @author jens dietrich
 */
public abstract class DefaultProvenanceBinding extends ProvenanceBinding {

    public EnumSet<ProvenanceKind> inferProvenanceKindFromMethodInvocation (String declaringClass, String methodName, String descriptor) {
        declaringClass = declaringClass.replace('/','.');
        boolean isConstructor = methodName.equals("<init>");

        EnumSet<ProvenanceKind> provenance = EnumSet.noneOf(ProvenanceKind.class);
        if (declaringClass.equals("java.lang.Runtime") && methodName.equals("exec")) {
            provenance.add(ProvenanceKind.SYSTEM_COMMAND_EXECUTION);
        }

        // note that this is not very accurate as we do not instrument callsites (with virtual calls)
        // but executions (with devirtualised calls)
        // so we rely on implementation of java.sql.Statement naming classes *Statement

        // @TODO check descriptors to avoid some FPs
        if (declaringClass.endsWith("Statement")) {  // includes PreparedStatement
            if (methodName.equals("execute") || methodName.equals("executeBatch")) {
                provenance.add(ProvenanceKind.DATABASE_WRITE);
                provenance.add(ProvenanceKind.DATABASE_READ);
            }
            else if (methodName.equals("executeQuery")) {
                provenance.add(ProvenanceKind.DATABASE_READ);
            }
            else if (methodName.equals("executeUpdate")) {
                provenance.add(ProvenanceKind.DATABASE_WRITE);
            }
        }

        // TODO @PreparedStatement, @CallableStatement , hibernate & co, mongo, neo4j

        if (declaringClass.startsWith("weka.classifiers.rules.")) {
            provenance.add(ProvenanceKind.AI_QUERY_RULE);
        }

        if (declaringClass.equals("java.net.URL") && isConstructor) {
            provenance.add(ProvenanceKind.NETWORK_ACCESS);
        }

        if (provenance.isEmpty()) {
            provenance.add(ProvenanceKind.NONE);
        }
        return provenance;
    }

}
