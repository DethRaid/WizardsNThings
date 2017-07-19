package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;

import static model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class Ability extends Observable implements ISaveable {
    public static String SAVE_ABILITY = "INSERT INTO ability(it, name, damage, num_targets, health_healed, description, level_available_to_player)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public int id;
    public String name;
    public int damage;
    public int numTargets;
    public int healthHealed;
    public String description;
    public int levelAvailableToPlayer;

    public Ability() {}

    public Ability(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        name = rs.getString("name");
        damage = rs.getInt("damage");
        numTargets = rs.getInt("num_targets");
        healthHealed = rs.getInt("health_healed");
        description = rs.getString("description");
        levelAvailableToPlayer = rs.getInt("level_available_to_player");
    }

    @Override
    public void save() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement saveStatement = connection.prepareStatement(SAVE_ABILITY);
            saveStatement.setInt(1, id);
            saveStatement.setString(2, name);
            saveStatement.setInt(3, damage);
            saveStatement.setInt(4, numTargets);
            saveStatement.setInt(5, healthHealed);
            saveStatement.setString(6, description);
            saveStatement.setInt(7, levelAvailableToPlayer);
            saveStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save ability " + name, e);
        }
    }
}
