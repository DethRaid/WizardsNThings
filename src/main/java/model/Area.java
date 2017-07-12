package model;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class Area {
    public String name;
    public String description;
    public Treasure treasure;
    public List<Enemy> enemies;

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
