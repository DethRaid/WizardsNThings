package model;

import java.util.Map;
import java.util.Observable;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class Area extends Observable {
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

    }
}
