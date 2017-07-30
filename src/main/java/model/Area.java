package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Observable;

import static model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class Area extends Observable {
    private static final String SET_CLEARED =
            "INSERT INTO cleared_areas(area_id, player_id) values (?, ?);";

    public int id;
    public String name;
    public String description;
    public Treasure treasure;

    // Map from enemy to the number of enemies in the area
    public Map<Enemy, Integer> enemies;

    /**
     * Returns the level of this area
     *
     * <p>The level of the room is computed with totalLevel * 2 ^ (enemies.size() / 2), which is roughly equivalent to
     * DND CRs</p>
     *
     * @return The level of the room
     */
    public int getLevel() {
        int totalLevel = 0;
        for(Enemy enemy : enemies.keySet()) {
            totalLevel += enemy.level;
        }

        return (int) (totalLevel * Math.pow(2.0, ((double)enemies.size()) / 2.0));
    }

    /**
     * Marks this area sa cleared by the given player
     *
     * @param playerName The name of the player who cleared this area
     */
    public void setAreaAsCleared(String playerName) {
        try(Connection connection = getDBConnection();
            PreparedStatement statement = connection.prepareStatement(SET_CLEARED)) {
            statement.setInt(1, id);
            statement.setString(2, playerName);
            statement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not set area " + id + " to be cleared by " + playerName, e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Area)) return false;

        final Area area = (Area) o;

        if (id != area.id) return false;
        if (!name.equals(area.name)) return false;
        if (!description.equals(area.description)) return false;
        if (!treasure.equals(area.treasure)) return false;
        return enemies.equals(area.enemies);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + treasure.hashCode();
        result = 31 * result + enemies.hashCode();
        return result;
    }
}
