package model;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class Area {
    public int id;
    public String name;
    public String description;
    public Treasure treasure;

    // Map from enemy to the number of enemies in the area
    public Map<Enemy, Integer> enemies;

    /**
     * Returns the level of this area
     *
     * <p>The level of the room is computed with 2 ^ (enemies.size() / 2), which is roughly equivalent to DND CRs</p>
     *
     * @return The level of the room
     */
    public int getLevel() {

        return 0;
    }
}
