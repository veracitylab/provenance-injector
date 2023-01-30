package nz.ac.wgtn.veracity.provenance.injector.sampleclasses;

import java.sql.*;

public class SomeDatabaseClass {

    public void someDatabaseMethod() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
    }
}
