package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to the players table
 *
 * @author ddubois
 * @since 7/11/17.
 */
public class PlayerDAO extends DAOBase {
    private static final String GET_ALL_PLAYERS     = "SELECT name FROM player;";
    private static final String GET_PLAYER_BY_NAME  = "SELECT * FROM players WHERE player.name = ?;";
    private static final String CREATE_PLAYER_TABLE =
            "CREATE TABLE players(" +
             "name              varchar(255)    not null    primary key" +
            ",strength          smallint        not null" +
            ",defence           smallint        not null" +
            ",experience        int             nul null" +
            ",currentHealth     int             not null" +
            ",maxHealth         int             not null" +
            ",weapon_id         INT             NOT NULL" +
            ",currentArea_id    INT             NOT NULL" +
            ");";

    private static String CREATE_PLAYER_ABILITY_TABLE =
            "CREATE TABLE player_abilities(" +
             "player_id INT NOT NULL" +
            ",ability_id INT NOT NULL" +
            ");";

    private WeaponDAO weaponDAO = new WeaponDAO();
    private AreaDAO areaDAO = new AreaDAO();
    private AbilityDAO abilityDAO = new AbilityDAO();

    public static void createTable() {
        try {
            PreparedStatement createTableStatement = prepareStatement(CREATE_PLAYER_TABLE);
            createTableStatement.execute();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create the players table", e);
        }

        try {
            PreparedStatement createAbilityTableStatement = prepareStatement(CREATE_PLAYER_ABILITY_TABLE);
            createAbilityTableStatement.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not create the player ability table", e);
        }
    }

    public List<String> getNamesOfAllPlayers() {
        try {
            PreparedStatement getNamesStatement = prepareStatement(GET_ALL_PLAYERS);
            ResultSet rs = getNamesStatement.executeQuery();

            List<String> names = new ArrayList<>();
            while(rs.next()) {
                names.add(rs.getString("name"));
            }

            return names;
        } catch (SQLException e) {
            throw new RuntimeException("Could not get the names of players", e);
        }
    }

    public Player getPlayer(String playerName) {
        try {
            PreparedStatement loadPlayerStatement = prepareStatement(GET_PLAYER_BY_NAME);
            loadPlayerStatement.setString(1, playerName);
            ResultSet rs = loadPlayerStatement.executeQuery();

            if(rs.next()) {
                Player player = new Player();

                player.name = rs.getString("name");
                player.strength = rs.getShort("strength");
                player.defence = rs.getShort("defence");
                player.experience = rs.getInt("experience");
                player.currentHealth = rs.getInt("currentHealth");
                player.maxHealth = rs.getInt("maxHealth");

                int weaponId = rs.getInt("weapon_id");
                player.weapon = weaponDAO.getWeapon(weaponId);

                int areaId = rs.getInt("currentArea_id");
                player.currentArea = areaDAO.getArea(areaId);

                player.abilities = abilityDAO.getAllAbilitiesForPlayer(player);

                return player;
            } else {
                throw new RuntimeException("Player name " + playerName + " does not exist");
            }

        } catch(SQLException e) {
            throw new RuntimeException("Could not load player " + playerName, e);
        }
    }
}
