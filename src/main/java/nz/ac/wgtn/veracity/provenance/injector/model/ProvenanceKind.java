package nz.ac.wgtn.veracity.provenance.injector.model;

/**
 * Simple hard coded ontology of things to observe.
 * @author jens dietrich
 */
public enum ProvenanceKind {
    DATABASE_READ, DATABASE_WRITE, NETWORK_READ, NETWORK_WRITE, NETWORK_ACCESS,SYSTEM_COMMAND_EXECUTION,
    AI_QUERY_RULE,
    NONE
}
