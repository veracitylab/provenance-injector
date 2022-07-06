package nz.ac.wgtn.veracity.provenance;

/**
 * Simple hard coded ontology of things to observe.
 * @author jens dietrich
 */
public enum ProvenanceKind {
    DATABASE_READ, DATABASE_WRITE, NETWORK_READ, NETWORK_WRITE, SYSTEM_COMMAND_EXECUTION,
    NONE
}
