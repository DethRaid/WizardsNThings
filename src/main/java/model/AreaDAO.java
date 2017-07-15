package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class AreaDAO extends DAOBase {
    private static final String GET_AREA_BY_ID = "SELECT * FROM area WHERE area.id = ?;";
    private static final String GET_ENEMIES_IN_AREA = "SELECT enemy_id FROM area_enemies WHERE area_enemies.area_id = ?;";

    private static final String CREATE_AREA_TABLE =
            "CREATE TABLE area(" +
                "id             INT             NOT NULL    PRIMARY KEY" +
               ",name           VARCHAR(255)    NOT NULL" +
               ",description    VARCHAR(512)    NOT NULL" +
               ",treasure_id    INT             NOT NULL" +
                ");";

    private static final String CREATE_AREA_ENEMIES_TABLE =
            "CREATE TABLE area_enemies(" +
             "enemy_id  INT     NOT NULL" +
            ",area_id   INT     NOT NULL" +
            ");";

    private final TreasureDAO treasureDAO = new TreasureDAO();
    private final EnemyDAO enemyDAO = new EnemyDAO();

    public void createTables() {
        try {
            PreparedStatement createAreaTableStatement = prepareStatement(CREATE_AREA_TABLE);
            createAreaTableStatement.execute();

            PreparedStatement createAreaEnemiesTable = prepareStatement(CREATE_AREA_ENEMIES_TABLE);
            createAreaEnemiesTable.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create area_enemies table", e);
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

            //area.enemies = getEnemiesInArea(areaId);
            return area;

        } catch(SQLException e) {
            throw new RuntimeException("Could not retrieve area with id " + areaId, e);
        }
    }

   // public List<Area> getAreasInLevelRange(int maxLevel, int numAreas) {

   // }

   // public Map<Enemy, Integer> getEnemiesInArea(int areaId) {

   // }

    public void setAreaAsCleared(int areaId, int playerId) {

    }
}
