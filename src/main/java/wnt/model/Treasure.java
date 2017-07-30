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
public class Treasure extends Observable implements ISaveable {
    private static final String SAVE =
            "INSERT INTO treasure(id, name, weapon_id) VALUES (?, ?, ?);";
    public int id;
    public String name;
    public Weapon weapon;

    @Override
    public void save() {
        try(Connection connection = getDBConnection();
            PreparedStatement saveStatement = connection.prepareStatement(SAVE)) {
            saveStatement.setInt(1, id);
            saveStatement.setString(2, name);
            saveStatement.setInt(3, weapon.id);

            saveStatement.execute();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Could not save treasure " + name, e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Treasure)) return false;

        final Treasure treasure = (Treasure) o;

        if (id != treasure.id) return false;
        if (!name.equals(treasure.name)) return false;
        return weapon.equals(treasure.weapon);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + weapon.hashCode();
        return result;
    }
}
