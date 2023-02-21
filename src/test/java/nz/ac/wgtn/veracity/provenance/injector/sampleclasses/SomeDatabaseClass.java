package nz.ac.wgtn.veracity.provenance.injector.sampleclasses;

import java.sql.*;

public class SomeDatabaseClass {

    public void someDatabaseMethod() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");

        String url = "jdbc:h2:mem:test";

        Connection connection = DriverManager.getConnection(url, "sa", "");
    }


    public void anotherMethod(String beans) {
        System.out.println(beans);
    }
}
