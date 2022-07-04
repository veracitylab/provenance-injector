package nz.ac.wgtn.veracity.provenance.injector.jee.rt;

/**
 * Constants used for the feedback mechanism.
 * @author jens dietrich
 */
public class Constants {

    public static final String PROVENANCE_HEADER = "provenance";

    public static final String PROVENANCE_PATH_TOKEN = "/__provenance";


    // to be used in path to pick up non-application invocations
    public static final String SYSTEM_INVOCATIONS_TICKET = "systeminvocations";

    // request parameter name for requests to pick up non-application invocations
    // the value is the comma-separated list of application package prefixes
    public static final String SYSTEM_INVOCATIONS_APPLICATION_PACKAGE_PREFIXES_PARAMETER = "applicationpackages";

}
