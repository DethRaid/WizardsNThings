package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Observable;

import static model.DAOBase.getDBConnection;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class Treasure extends Observable implements ISaveable {
    private static final String SAVE =
            "INSERT INTO treasure(id, name, weapon) VALUES (?, ?, ?);";
    public int id;
    public String name;
    public Weapon weapon;

    @Override
    public void save() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement saveStatement = connection.prepareStatement(SAVE);
            saveStatement.setInt(1, id);
            saveStatement.setString(2, name);
            saveStatement.setInt(3, weapon.id);

        } catch (SQLException e) {
            throw new RuntimeException("Could not save treasure " + name, e);
        }
    }
}
