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
    private static final String GET_ALL_PLAYERS     = "SELECT name FROM players;";
    private static final String GET_PLAYER_BY_NAME  = "SELECT * FROM players WHERE player.name = ?;";
    private static final String SAVE_PLAYER         = "INSERT INTO players (name, strength, defence, experience, currentHealth, maxHealth, weapon_id, currentArea_id) VALUES (?,?,?,?,?,?,?,?)";
    private static final String CREATE_PLAYER_TABLE =
            "CREATE TABLE players(" +
             "name          varchar(255)    not null    primary key" +
            ",strength      smallint        not null" +
            ",defence       smallint        not null" +
            ",experience    int             nul null" +
            ",currentHealth int             not null" +
            ",maxHealth     int             not null" +
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
    }

    /**
     * Saves the given player to the database
     */
    public void save(Player player) {
        try {
            PreparedStatement savePlayerStatement = prepareStatement(SAVE_PLAYER);
            savePlayerStatement.setString(1, player.name);
            savePlayerStatement.setShort(2, player.defence);
            savePlayerStatement.setShort(3, player.defence);
            savePlayerStatement.setInt(4, player.experience);
            savePlayerStatement.setInt(5, player.currentHealth);
            savePlayerStatement.setInt(6, player.maxHealth);
            savePlayerStatement.setInt(7, player.weapon.id);
            savePlayerStatement.setInt(8, player.currentArea.id);
            savePlayerStatement.execute();
        } catch(SQLException e) {
            throw new RuntimeException("Could not save player " + player.name, e);
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

                int weaponId = rs.getInt("weaponId");
                player.weapon = weaponDAO.getWeapon(weaponId);

                int areaId = rs.getInt("areaId");
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
