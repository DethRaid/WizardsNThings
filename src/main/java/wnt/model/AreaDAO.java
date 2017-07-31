package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class AreaDAO extends DAOBase {
    private static final String GET_ALL_AREAS   = "SELECT * FROM area;";
    private static final String GET_AREA_BY_ID = "SELECT * FROM area WHERE area.id = ? NOT IN (SELECT area.id FROM cleared_areas);";
    private static final String GET_ENEMIES_IN_AREA = "SELECT enemy_name, count FROM area_enemies WHERE area_enemies.area_id = ?;";
    private static final String GET_NUMBER_CLEARED_ROOMS = "SELECT COUNT(area_id) as num FROM cleared_areas WHERE cleared_area.player_id = ?;";
    private static final String GET_ALL_CLEARED_AREAS = "SELECT * FROM cleared_areas WHERE cleared_area.player_id = ?;";

    private static final String CREATE_AREA_TABLE =
            "CREATE TABLE IF NOT EXISTS area(" +
                "id             INT             NOT NULL    PRIMARY KEY" +
               ",name           VARCHAR(255)    NOT NULL" +
               ",description    VARCHAR(512)    NOT NULL" +
               ",treasure_id    INT             NOT NULL" +
               ",FOREIGN KEY (treasure_id) REFERENCES treasure(id)" +
                ");" +
                    "CREATE UNIQUE INDEX IF NOT EXISTS IDX_AREA_ID ON area(id DESC);";

    private static final String CREATE_AREA_ENEMIES_TABLE =
            "CREATE TABLE IF NOT EXISTS area_enemies(" +
             "enemy_name    VARCHAR(128)    NOT NULL" +
            ",area_id       INT             NOT NULL" +
            ",count         INT             NOT NULL" +
            ",FOREIGN KEY (enemy_name) REFERENCES enemy(name)" +
            ",FOREIGN KEY (area_id) REFERENCES area(id)" +
            ");";

    private static final String CREATE_CLEARED_AREAS_TABLE =
            "CREATE TABLE IF NOT EXISTS cleared_areas(" +
             "area_id   INT             NOT NULL" +
            ",player_id VARCHAR(128)    NOT NULL" +
            ",FOREIGN KEY (area_id) REFERENCES area(id)" +
            ",FOREIGN KEY (player_id) REFERENCES player(name)" +
            ");" +
                    "CREATE UNIQUE INDEX IF NOT EXISTS IDX_CLEARED_AREAS_PLAYER_ID ON cleared_areas(player_id);";

    private final TreasureDAO treasureDAO = new TreasureDAO();
    private final EnemyDAO enemyDAO = new EnemyDAO();

    public static void createTables() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement createAreaTableStatement = connection.prepareStatement(CREATE_AREA_TABLE);
            createAreaTableStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create area_enemies table", e);
        }

        try(Connection connection = getDBConnection()) {
            PreparedStatement createAreaEnemiesTable = connection.prepareStatement(CREATE_AREA_ENEMIES_TABLE);
            createAreaEnemiesTable.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create the area enemies table", e);
        }
    }

    public static void createClearedAreasTable() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement createClearedAreasTable = connection.prepareStatement(CREATE_CLEARED_AREAS_TABLE);
            createClearedAreasTable.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create the cleared area enemies table", e);
        }
    }

    public Area getArea(final int areaId) {
        if(areaId == 0) {
            return null;
        }

        try(Connection connection = getDBConnection();
            PreparedStatement getAreaStatement = connection.prepareStatement(GET_AREA_BY_ID)) {
            getAreaStatement.setInt(1, areaId);

            ResultSet areaResult = getAreaStatement.executeQuery();
            if(!areaResult.next()) {
                throw new RuntimeException("Could not get area with ID " + areaId);
            }

            return makeArea(areaResult);

        } catch(SQLException e) {
            throw new RuntimeException("Could not retrieve area with id " + areaId, e);
        }
    }

    public int getNumClearedAreas(Player player) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_NUMBER_CLEARED_ROOMS);
            statement.setString(1, player.name);
            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return rs.getInt("num");
            }
            throw new RuntimeException("Could not get number of cleared areas for player " + player.name);

        } catch(SQLException e) {
            throw new RuntimeException("Could not get number of cleared rooms to player " + player.name, e);
        }
    }

    public List<Area> getAreasInLevelRange(int maxLevel, int numAreas, String playerId) {
        try(Connection connection = getDBConnection()){
            PreparedStatement statement = connection.prepareStatement(GET_ALL_AREAS);
            PreparedStatement clearedRooms = connection.prepareStatement(GET_ALL_CLEARED_AREAS);
            clearedRooms.setString(1, playerId);

            ResultSet rs = statement.executeQuery();
            ResultSet clearedrs = clearedRooms.executeQuery();
            List<Area> areas = new ArrayList<>();
            while(rs.next()) {
                areas.add(makeArea(rs));
            }

            return areas.stream()
                    .filter(area -> area.getLevel() <= maxLevel)
                    .limit(numAreas)
                    .collect(Collectors.toList());

        } catch(SQLException e) {
            throw new RuntimeException("Could not get areas with level " + maxLevel, e);
        }
    }

    public Map<Enemy, Integer> getEnemiesInArea(int areaId) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement getEnemiesInAreaStatement = connection.prepareStatement(GET_ENEMIES_IN_AREA);
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

    private Area makeArea(ResultSet areaResult) throws SQLException {
        Area area = new Area();
        area.id = areaResult.getInt("id");
        area.name = areaResult.getString("name");
        area.description = areaResult.getString("description");

        area.treasure = treasureDAO.getTreasure(areaResult.getInt("treasure_id"));

        area.enemies = getEnemiesInArea(area.id);
        return area;
    }
}
