package wnt.model;

import java.sql.Connection;
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
            "CREATE TABLE IF NOT EXISTS weapon(" +
             "id            IDENTITY" +
            ",name          VARCHAR(255)    NOT NULL" +
            ",damage        INT             NOT NULL" +
            ",attack_speed  INT             NOT NULL" +
            ");" +
                    "CREATE UNIQUE INDEX IF NOT EXISTS IDX_WEAPON_ID ON weapon(id);" +
                    "CREATE UNIQUE INDEX IF NOT EXISTS IDX_WEAPON_ATTACK_SPEED ON weapon(attack_speed);" +
                    "CREATE UNIQUE INDEX IF NOT EXISTS IDX_WEAPON_DAMAGE ON weapon(damage);";

    private static String GET_RANDOM_BAD_WEAPON =
            "SELECT * FROM weapon WHERE weapon.id IN (" +
                    "SELECT id FROM weapon " +
                    "ORDER BY weapon.attack_speed + weapon.damage DESC " +
                    "LIMIT 5)" +
                    "ORDER BY RAND()" +
                    "LIMIT 1;";

    /**
     * Creates the weapon table
     *
     * <p>If the weapon table exists, then this method does nothing</p>
     */
    public static void createTable() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement statement = connection.prepareStatement(CREATE_TABLE);
            statement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create table weapon", e);
        }
    }

    public Weapon getWeapon(int id) {
        if(id == 0) {
            return null;
        }
        try(Connection connection = getDBConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_WEAPON_BY_ID);
            statement.setInt(1, id);
            ResultSet weaponSet = statement.executeQuery();

            if(weaponSet.next()) {
                return makeWeapon(weaponSet);
            }
            throw new RuntimeException("Could not get weapon with ID " + id + ": ResultSet#next returned false");

        } catch(SQLException e) {
            throw new RuntimeException("Could not get weapon with ID " + id, e);
        }
    }

    /**
     * Looks at all the weapons in our database and returns one of the worst five weapons
     *
     * <p>"Worst" is defined as a combination of attack speed and weapon damage. These two values are summed and their
     * sum is our measure of how good a weapon is</p>
     *
     * @return One of the worst five weapons
     */
    public Weapon getStartingWeapon() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement findWeaponStatement = connection.prepareStatement(GET_RANDOM_BAD_WEAPON);
            ResultSet rs = findWeaponStatement.executeQuery();
            if(rs.next()) {
                return makeWeapon(rs);
            }

            throw new RuntimeException("Could not get a bad weapon");

        } catch(SQLException e) {
            throw new RuntimeException("Could not get a bad weapon", e);
        }
    }

    private Weapon makeWeapon(ResultSet weaponSet) throws SQLException {
        Weapon weapon = new Weapon();
        weapon.id = weaponSet.getInt("id");
        weapon.name = weaponSet.getString("name");
        weapon.damage = weaponSet.getInt("damage");
        weapon.attackSpeed = weaponSet.getInt("attack_speed");

        return weapon;
    }
}
