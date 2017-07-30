package wnt.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A base class with functionality common to DAOs
 *
 * @author ddubois
 * @since 7/11/17.
 */
public abstract class DAOBase {
    private static final String DB_DRIVER       = "org.h2.Driver";
    private static final String DB_CONNECTION   = "jdbc:h2:~/wizards_n_things.trace.db;" +
            "INIT=CREATE SCHEMA IF NOT EXISTS wizards_n_things\\;" +
            "SET SCHEMA wizards_n_things;";
    private static final String DB_USER         = "";
    private static final String DB_PASSWORD     = "";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    public static Connection getDBConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        connection.setAutoCommit(true);
        return connection;
    }
}
