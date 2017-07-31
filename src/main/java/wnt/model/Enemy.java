package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Observable;

import static wnt.model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 15-Jul-17
 */
public class Enemy extends Observable implements ISaveable {
    private static final String SAVE =
            "INSERT INTO enemy(name, level) VALUES (?, ?);";

    public String name;
    public int level;
    public transient int currentHealth;
    public transient boolean isDead;

    public Enemy(String name, int level) {
        this.name = name;
        this.level = level;

        currentHealth = getMaxHealth();
        isDead = false;
    }

    public int getStrength() {
        return level * 2;
    }

    public int getDefense() {
        return level * 2;
    }

    public int getMaxHealth() {
        return level * 100;
    }

    public void changeHealth(int healthChange) {
        currentHealth = Math.min(currentHealth + healthChange, getMaxHealth());

        if(currentHealth <= 0) {
            // TODO: Fire player dead event
            isDead = true;
        }

        setChanged();
        notifyObservers(this);
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Enemy)) return false;

        final Enemy enemy = (Enemy) o;

        return level == enemy.level && name.equals(enemy.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + level;
        result = 31 * result + currentHealth;
        result = 31 * result + (isDead ? 1 : 0);
        return result;
    }

    @Override
    public void save() {
        try(Connection connection = getDBConnection();
             PreparedStatement saveStatement = connection.prepareStatement(SAVE)) {
            saveStatement.setString(1, name);
            saveStatement.setInt(2, level);
            saveStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save enemy " + name, e);
        }
    }
}
