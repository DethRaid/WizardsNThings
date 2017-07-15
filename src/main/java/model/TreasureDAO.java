package model;

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

    private final WeaponDAO weaponDAO = new WeaponDAO();

    public static void createTable() {
        try {
            PreparedStatement createTableStatement = prepareStatement(CREATE_TABLE);
            createTableStatement.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create treasure table", e);
        }
    }

    public Treasure getTreasure(int treasureId) {
        try {
            PreparedStatement getTreasureStatement = prepareStatement(GET_TREASURE);
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
}
