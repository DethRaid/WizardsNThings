package model;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class Ability {
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
}
