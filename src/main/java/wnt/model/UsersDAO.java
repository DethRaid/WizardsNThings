package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author ddubois
 * @since 7/30/17.
 */
public class UsersDAO extends DAOBase {
    private static final String ADMIN_USERNAME  = "Chairman_Mao";
    private static final String ADMIN_PASSWORD  = "'ReeducationCampsFTW'";
    private static final String PLAYER_USERNAME = "JohnQPublic";
    private static final String PLAYER_PASSWORD = "'BlindToTheHorrorsOfCapitalism'";

    private static final String ADMIN_ROLE      = "WNT_ADMIN";
    private static final String PLAYER_ROLE     = "WNT_PLAYER";

    public static void createUsers() {
        try(Connection connection = getDBConnection()) {
            connection.prepareStatement("CREATE USER IF NOT EXISTS " + ADMIN_USERNAME + " PASSWORD " + ADMIN_PASSWORD).execute();
            connection.prepareStatement("CREATE USER IF NOT EXISTS " + PLAYER_USERNAME + " PASSWORD " + PLAYER_PASSWORD).execute();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not create DB users", e);
        }
    }

    public static void createPermissions() {
        try(Connection connection = getDBConnection()) {
            connection.prepareStatement("CREATE ROLE IF NOT EXISTS " + ADMIN_ROLE).execute();
            connection.prepareStatement("CREATE ROLE IF NOT EXISTS " + PLAYER_ROLE).execute();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not create roles", e);
        }

        try(Connection connection = getDBConnection()) {
            connection.prepareStatement("GRANT " + ADMIN_ROLE + " TO " + ADMIN_USERNAME).execute();
            connection.prepareStatement("GRANT " + PLAYER_ROLE + " TO " + PLAYER_USERNAME).execute();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could nto grant roles", e);
        }

        try(Connection connection = getDBConnection()) {
            connection.prepareStatement("GRANT INSERT ON ability        TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT INSERT ON area           TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT INSERT ON area_enemies   TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT INSERT ON enemy          TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT INSERT ON treasure       TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT INSERT ON weapon         TO WNT_ADMIN;").execute();

            connection.prepareStatement("GRANT SELECT ON ability        TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON area           TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON area_enemies   TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON cleared_areas  TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON enemy          TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON player         TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON player_ability TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON treasure       TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT SELECT ON weapon         TO WNT_ADMIN;").execute();

            connection.prepareStatement("GRANT UPDATE ON ability        TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT UPDATE ON area           TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT UPDATE ON area_enemies   TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT UPDATE ON enemy          TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT UPDATE ON treasure       TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT UPDATE ON weapon         TO WNT_ADMIN;").execute();

            connection.prepareStatement("GRANT DELETE ON ability        TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON area           TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON area_enemies   TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON cleared_areas  TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON enemy          TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON player         TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON player_ability TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON treasure       TO WNT_ADMIN;").execute();
            connection.prepareStatement("GRANT DELETE ON weapon         TO WNT_ADMIN;").execute();



            connection.prepareStatement("GRANT SELECT ON ability        TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON area           TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON area_enemies   TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON cleared_areas  TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON enemy          TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON player         TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON player_ability TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON treasure       TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT SELECT ON weapon         TO WNT_PLAYER;").execute();

            connection.prepareStatement("GRANT INSERT ON player         TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT INSERT ON player_ability TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT INSERT ON cleared_areas  TO WNT_PLAYER;").execute();
            connection.prepareStatement("GRANT UPDATE ON player         TO WNT_PLAYER;").execute();

        } catch (SQLException e) {
            throw new RuntimeException("Could not grant permissions", e);
        }
    }
}
