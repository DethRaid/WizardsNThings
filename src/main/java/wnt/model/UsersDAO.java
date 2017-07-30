package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author ddubois
 * @since 7/30/17.
 */
public class UsersDAO extends DAOBase {
    private static final String CREATE_USER     = "CREATE USER IF NOT EXISTS ? PASSWORD ?;";
    private static final String CREATE_ROLE     = "CREATE ROLE IF NOT EXISTS ?;";
    private static final String GRANT_ROLE      = "GRANT ? TO ?;";

    private static final String ADMIN_USERNAME  = "Chairman_Mao";
    private static final String ADMIN_PASSWORD  = "ReeducationCampsFTW";
    private static final String PLAYER_USERNAME = "JohnQPublic";
    private static final String PLAYER_PASSWORD = "BlindToTheHorrorsOfCapitalism";

    private static final String ADMIN_ROLE      = "WNT_ADMIN";
    private static final String PLAYER_ROLE     = "WNT_PLAYER";

    public static void createUsers() {
        try(Connection connection = getDBConnection();
            PreparedStatement statement = connection.prepareStatement(CREATE_USER)) {
            statement.setString(1, ADMIN_USERNAME);
            statement.setString(2, ADMIN_PASSWORD);
            statement.execute();

            statement.setString(1, PLAYER_USERNAME);
            statement.setString(2, PLAYER_PASSWORD);
            statement.execute();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not create DB users", e);
        }
    }

    public static void createPermissions() {
        try(Connection connection = getDBConnection();
            PreparedStatement statement = connection.prepareStatement(CREATE_ROLE)) {
            statement.setString(1, ADMIN_ROLE);
            statement.execute();

            statement.setString(1, PLAYER_ROLE);
            statement.execute();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not create roles", e);
        }

        try(Connection connection = getDBConnection();
            PreparedStatement statement = connection.prepareStatement(GRANT_ROLE)) {
            statement.setString(1, ADMIN_ROLE);
            statement.setString(2, ADMIN_USERNAME);
            statement.execute();

            statement.setString(1, PLAYER_ROLE);
            statement.setString(2, PLAYER_USERNAME);
            statement.execute();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could nto grant roles", e);
        }

        try(Connection connection = getDBConnection()) {
            connection.prepareStatement("GRANT SELECT ON * TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT INSERT ON * TO WNT_ADMIN;").execute();
            connection.prepareStatement("REVOKE INSERT ON player FROM WNT_ADMIN;").execute();
            connection.prepareStatement("REVOKE INSERT ON player_ability FROM WNT_ADMIN;").execute();

        } catch (SQLException e) {
            throw new RuntimeException("Could not grant permissions", e);
        }
    }
}
