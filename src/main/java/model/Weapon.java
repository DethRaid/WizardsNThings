package model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Observable;

import static model.DAOBase.prepareStatement;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class Weapon extends Observable {
    private static final String SAVE =
            "INSERT INTO weapon(name, damage, attack_speed) VALUES (?, ?, ?);";

    public int id;
    public String name;
    public int damage;
    public int attackSpeed;

    public void save() {
        try {
            PreparedStatement saveStatement = prepareStatement(SAVE);
            saveStatement.setString(1, name);
            saveStatement.setInt(2, damage);
            saveStatement.setInt(3, attackSpeed);

            saveStatement.execute();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save weapon " + name, e);
        }
    }
}
