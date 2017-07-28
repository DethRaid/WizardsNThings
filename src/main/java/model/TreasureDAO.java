package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfaces with the Treasure table
 *
 * @author ddubois
 * @since 14-Jul-17
 */
public class TreasureDAO extends DAOBase {
    private static final String GET_TREASURE = "SELECT * FROM treasure WHERE treasure.id = ?;";
    private static final String CREATE_TABLE =
            "CREATE TABLE treasure(" +
             "id     INT     NOT NULL    PRIMARY KEY" +
            ",name  VARCHAR(255)    NOT NULL" +
            ",weapon_id INT NOT NULL" +
            ");";
    public static final String DELETE_TREASURE =
            "DELETE * FROM treasure WHERE treasure.id = ?;";

    private final WeaponDAO weaponDAO = new WeaponDAO();

    public static void createTable() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement createTableStatement = connection.prepareStatement(CREATE_TABLE);
            createTableStatement.execute();
            connection.commit();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create treasure table", e);
        }
    }

    public Treasure getTreasure(int treasureId) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement getTreasureStatement = connection.prepareStatement(GET_TREASURE);
            ResultSet treasureSet = getTreasureStatement.executeQuery();

            Treasure treasure = new Treasure();
            treasure.id = treasureSet.getInt("id");
            treasure.name = treasureSet.getString("name");
            treasure.weapon = weaponDAO.getWeapon(treasureSet.getInt("weapon_id"));

            return treasure;

        } catch(SQLException e) {
            throw new RuntimeException("Could not get treasure with id " + treasureId, e);
        }
    }

    public void deleteTreasure(Treasure toDelete) {
        deleteTreasure(toDelete.id);
    }

    private void deleteTreasure(final int id) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_TREASURE);
            statement.setInt(1, id);
            statement.execute();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not delete treasure " + id, e);
        }
    }
}
