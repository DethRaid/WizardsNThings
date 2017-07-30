package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Observable;

import static wnt.model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class Weapon extends Observable implements ISaveable {
    private static final String SAVE =
            "INSERT INTO weapon(name, damage, attack_speed) VALUES (?, ?, ?);";

    public int id;
    public String name;
    public int damage;
    public int attackSpeed;

    @Override
    public void save() {
        try(Connection connection = getDBConnection();
                PreparedStatement saveStatement = connection.prepareStatement(SAVE)) {
            saveStatement.setString(1, name);
            saveStatement.setInt(2, damage);
            saveStatement.setInt(3, attackSpeed);

            saveStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save weapon " + name, e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon)) return false;

        final Weapon weapon = (Weapon) o;

        if (id != weapon.id) return false;
        if (damage != weapon.damage) return false;
        if (attackSpeed != weapon.attackSpeed) return false;
        return name.equals(weapon.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + damage;
        result = 31 * result + attackSpeed;
        return result;
    }
}
