package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import static model.DAOBase.getDBConnection;


/**
 * The player of a game
 *
 * @author ddubois
 * @since 7/11/17.
 */
public class Player extends Observable implements ISaveable {
    private static final String CHECK_FOR_PLAYER = "SELECT * FROM player WHERE player.name = ?;";
    private static final String SAVE_PLAYER = "UPDATE player " +
            "SET strength = ?, defence = ?, experience = ?, currentHealth = ?, maxHealth = ?, weapon_id = ?, currentArea_id = ?)" +
            "WHERE name = ?;";

    private static final String CREATE_PLAYER = "INSERT INTO player(strength, defence, experience, currentHealth, maxHealth, weapon_id, currentArea_id, name) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    public String name;
    public short strength;
    public short defence;
    public int experience;
    public int currentHealth;
    public int maxHealth;
    public Weapon weapon;
    public Area currentArea;
    public Map<String, Ability> abilities;

    public Player() {}

    public Player(String name){
        this.name = name;
        this.strength = 5;
        this.defence = 5;
        this.experience = 0;
        //Starting weapon will be set in the controller
        this.weapon = null;
        this.maxHealth = 10;
        this.currentHealth = 10;
        //Starting area will be set in the controller
        this.currentArea = null;
        this.abilities = new HashMap<>();
    }

    /**
     * Changes the player's health by the specified amount
     * <p>If the change would make the player's health less than zero, then a `playerDead` event is fired. If the change
     * would make the player's health greater than zero, then the player's health is set to maxHealth</p>
     *
     * @param healthChange The amount to change the health by
     */
    public void changeHealth(int healthChange) {
        currentHealth = Math.min(currentHealth + healthChange, maxHealth);

        if(currentHealth <= 0) {
            // TODO: Fire player dead event
        }

        setChanged();
        notifyObservers(this);
    }


    /**
     * Saves the given player to the database
     */
    @Override
    public void save() {
        // Check if the player exists. If so add a row, otherwise update a row
        boolean playerExists = false;
        try(Connection connection = getDBConnection()) {
            PreparedStatement checkPlayerStatement = connection.prepareStatement(CHECK_FOR_PLAYER);
            ResultSet rs = checkPlayerStatement.executeQuery();
            playerExists = !rs.wasNull();

        } catch(SQLException e) {
            throw new RuntimeException("Could not check if player " + name + " exists", e);
        }

        try(Connection connection = getDBConnection()) {
            PreparedStatement savePlayerStatement;
            if(playerExists) {
                savePlayerStatement = connection.prepareStatement(SAVE_PLAYER);
            } else {
                savePlayerStatement = connection.prepareStatement(CREATE_PLAYER);
            }

            savePlayerStatement.setShort(1, defence);
            savePlayerStatement.setShort(2, defence);
            savePlayerStatement.setInt(3, experience);
            savePlayerStatement.setInt(4, currentHealth);
            savePlayerStatement.setInt(5, maxHealth);
            savePlayerStatement.setInt(6, weapon.id);
            savePlayerStatement.setInt(7, currentArea.id);
            savePlayerStatement.setString(8, name);
            savePlayerStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save player " + name, e);
        }
    }
}
