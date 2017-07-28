package model;

import org.junit.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 7/28/17.
 */
public class DaoTestBase {
    public void ensureTableExists(String tableName) throws SQLException {
        try(Connection connection = getDBConnection()) {
            ResultSet rs = connection.getMetaData().getTables(null, null, tableName.toUpperCase(), null);

            Assert.assertTrue(rs.next());
        }
    }
}
