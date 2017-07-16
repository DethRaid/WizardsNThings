package model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class AreaDAO extends DAOBase {
    private static final String GET_AREA_BY_ID = "SELECT * FROM area WHERE area.id = ?;";
    private static final String GET_ENEMIES_IN_AREA = "SELECT enemy_id, count FROM area_enemies WHERE area_enemies.area_id = ?;";

    private static final String CREATE_AREA_TABLE =
            "CREATE TABLE area(" +
                "id             INT             NOT NULL    PRIMARY KEY" +
               ",name           VARCHAR(255)    NOT NULL" +
               ",description    VARCHAR(512)    NOT NULL" +
               ",treasure_id    INT             NOT NULL" +
                ");";

    private static final String CREATE_AREA_ENEMIES_TABLE =
            "CREATE TABLE area_enemies(" +
             "enemy_name    VARCHAR(128)    NOT NULL" +
            ",area_id       INT             NOT NULL" +
            ");";

    private static final String CREATE_CLEARED_AREAS_TABLE =
            "CREATE TABLE cleared_areas(" +
             "area_id   INT     NOT NULL" +
            ",player_id INT     NOT NULL" +
            ");";

    private final TreasureDAO treasureDAO = new TreasureDAO();
    private final EnemyDAO enemyDAO = new EnemyDAO();

    public static void createTables() {
        try {
            PreparedStatement createAreaTableStatement = prepareStatement(CREATE_AREA_TABLE);
            createAreaTableStatement.execute();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create area_enemies table", e);
        }

        try {
            PreparedStatement createAreaEnemiesTable = prepareStatement(CREATE_AREA_ENEMIES_TABLE);
            createAreaEnemiesTable.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create the area enemies table", e);
        }
    }

    public Area getArea(final int areaId) {
        try {
            PreparedStatement getAreaStatement = prepareStatement(GET_AREA_BY_ID);
            PreparedStatement getEnemiesInAreaStatement = prepareStatement(GET_ENEMIES_IN_AREA);

            ResultSet areaResult = getAreaStatement.executeQuery();
            if(!areaResult.next()) {
                throw new RuntimeException("Could not get area with ID " + areaId);
            }

            Area area = new Area();
            area.id = areaResult.getInt("id");
            area.name = areaResult.getString("name");
            area.description = areaResult.getString("description");

            area.treasure = treasureDAO.getTreasure(areaResult.getInt("treasure_id"));

            area.enemies = getEnemiesInArea(areaId);

            return area;

        } catch(SQLException e) {
            throw new RuntimeException("Could not retrieve area with id " + areaId, e);
        }
    }

    public List<Area> getAreasInLevelRange(int maxLevel, int numAreas) {
        // TODO
        throw new NotImplementedException();
    }

    public Map<Enemy, Integer> getEnemiesInArea(int areaId) {
        try {
            PreparedStatement getEnemiesInAreaStatement = prepareStatement(GET_ENEMIES_IN_AREA);
            getEnemiesInAreaStatement.setInt(1, areaId);
            ResultSet rs = getEnemiesInAreaStatement.executeQuery();

            Map<Enemy, Integer> enemies = new HashMap<>();
            while(rs.next()) {
                Enemy enemy = enemyDAO.getEnemy(rs.getString("enemy_name"));
                Integer count = rs.getInt("count");
                enemies.put(enemy, count);
            }

            return enemies;

        } catch(SQLException e) {
            throw new RuntimeException("Could not get the enemies in area " + areaId, e);
        }
    }
}
