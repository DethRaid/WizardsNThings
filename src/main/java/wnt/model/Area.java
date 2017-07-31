package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import static wnt.model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class Area extends Observable implements ISaveable {
    private static final String SET_CLEARED =
            "INSERT INTO cleared_areas(area_id, player_id) values (?, ?);";

    private static final String SAVE =
            "INSERT INTO area(id, name, description, treasure_id, is_cleared) VALUES (?, ?, ?, ?,?);";

    private static final String SAVE_AREA_ENEMIES =
            "INSERT INTO area_enemies(enemy_name, area_id, count) VALUES (?, ?, ?);";

    public int id;
    public String name;
    public String description;
    public Treasure treasure;
    public boolean isCleared;

    // Map from enemy to the number of enemies in the area
    public Map<Enemy, Integer> enemies = new HashMap<>();

    @Override
    public void save() {
        try(Connection connection = getDBConnection();
            PreparedStatement saveStatement = connection.prepareStatement(SAVE)) {
            saveStatement.setInt(1, id);
            saveStatement.setString(2, name);
            saveStatement.setString(3, description);
            saveStatement.setInt(4, treasure.id);
            saveStatement.setBoolean(5, isCleared);

            saveStatement.execute();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not save area " + name, e);
        }

        try(Connection connection = getDBConnection();
            PreparedStatement statement = connection.prepareStatement(SAVE_AREA_ENEMIES)) {

            for(Map.Entry<Enemy, Integer> entry : enemies.entrySet()) {
                statement.setString(1, entry.getKey().name);
                statement.setInt(2, id);
                statement.setInt(3, entry.getValue());

                statement.execute();
            }

            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save the enemies in this area", e);
        }
    }
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
            isCleared = true;

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
