package model;

import java.util.List;

/**
 * Provides access to the players table
 *
 * @author ddubois
 * @since 7/11/17.
 */
public class PlayerDAO {
    private static final String GET_ALL_PALYERS_QUERY   = "SELECT name FROM players;";
    private static final String GET_PLAYER_BY_NAME      = "SELECT * FROM players WHERE player.name = ?;";
    private static final String SAVE_PLAYER_STATEMENT   = "INSERT INTO players (name, strength, defence, experience, currentHealth, maxHealth, weapon_id, currentArea_id) VALUES (?,?,?,?,?,?,?,?)";

    /**
     * Saves the given player to the database
     */
    public void save(Player player) {

    }

    public List<String> getNamesOfAllPlayers() {

    }

    public Player loadPlayer(String playerName) {

    }
}
