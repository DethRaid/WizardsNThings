package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ddubois
 * @since 15-Jul-17
 */
public class EnemyDAO extends DAOBase {
    private static String CREATE_ENEMY_TABLE =
            "CREATE TABLE enemy(" +
                    "name VARCHAR(128)  NOT NULL PRIMARY KEY" +
                    ",level INT NOT NULL" +
                    ");";

    public static String GET_ENEMY = "SELECT * FROM enemy WHERE enemy.name = ?;";

    public static void createTable() {
        try {
            PreparedStatement createEnemeyTableStatement = prepareStatement(CREATE_ENEMY_TABLE);
            createEnemeyTableStatement.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create enemy table", e);
        }
    }

    public Enemy getEnemy(String enemyName) {
        try {
            PreparedStatement getEnemyStatement = prepareStatement(GET_ENEMY);
            getEnemyStatement.setString(1, enemyName);
            ResultSet enemySet = getEnemyStatement.executeQuery();

            if(enemySet.next()) {
                Enemy enemy = new Enemy();
                enemy.name = enemySet.getString("name");
                enemy.level = enemySet.getInt("level");

                return enemy;
            }

            throw new RuntimeException("Could not get enemy " + enemyName + ": No results returned");

        } catch(SQLException e) {
            throw new RuntimeException("Could not get enemy " + enemyName, e);
        }
    }
}
