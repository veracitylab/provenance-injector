package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

/**
 * Constants representing the kind of data to be tracked.
 * Each instance is associated with a schema -- if this is empty, each data value is a single string value.
 * Otherwise, this is encoded as a (flat) map, with the schema elements as names.
 * @author jens dietrich
 */
public enum DataKind {
    invokedMethods
}
