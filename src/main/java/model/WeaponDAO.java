package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class WeaponDAO extends DAOBase {
    private static String GET_WEAPON_BY_ID  = "SELECT * FROM weapon WHERE weapon.id = ?;";
    private static String CREATE_TABLE  =
            "CREATE TABLE weapon(" +
             "id            INT             NOT NULL    PRIMARY KEY" +
            ",name          VARCHAR(255)    NOT NULL" +
            ",damage        INT             NOT NULL" +
            ",attack_speed  INT             NOT NULL" +
            ");";

    public static void createTable() {
        try {
            PreparedStatement statement = prepareStatement(CREATE_TABLE);
            statement.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create table weapon", e);
        }
    }

    public Weapon getWeapon(int id) {
        try {
            PreparedStatement statement = prepareStatement(GET_WEAPON_BY_ID);
            statement.setInt(1, id);
            ResultSet weaponSet = statement.executeQuery();

            Weapon weapon = new Weapon();
            weapon.id = weaponSet.getInt("id");
            weapon.name = weaponSet.getString("name");
            weapon.damage = weaponSet.getInt("damage");
            weapon.attackSpeed = weaponSet.getInt("attack_speed");

            return weapon;

        } catch(SQLException e) {
            throw new RuntimeException("Could not get weapon with ID " + id, e);
        }
    }
}
