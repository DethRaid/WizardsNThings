package wnt.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static wnt.model.DAOBase.getDBConnection;


/**
 * The player of a game
 *
 * @author ddubois
 * @since 7/11/17.
 */
public class Player extends Observable implements ISaveable {
    private static final String CHECK_FOR_PLAYER = "SELECT * FROM player WHERE player.name = ?;";
    private static final String SAVE_PLAYER = "UPDATE player " +
            "SET strength = ?, defence = ?, experience = ?, currentHealth = ?, maxHealth = ?, weapon_id = ?, currentArea_id = ? " +
            "WHERE name = ?;";
    private static final String CREATE_PLAYER = "INSERT INTO player(strength, defence, experience, currentHealth, maxHealth, weapon_id, currentArea_id, name) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SAVE_PLAYER_ABILITY =
            "INSERT INTO player_ability(player_name, ability_id) VALUES (?, ?);";

    public String name;
    public short strength;
    public short defence;
    public int experience;
    public int currentHealth;
    public int maxHealth;
    public Weapon weapon;
    public Area currentArea;
    public List<Ability> abilities = new ArrayList<>();

    private AbilityDAO abilityDAO = new AbilityDAO();

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
        boolean playerExists;
        try(Connection connection = getDBConnection();
            PreparedStatement checkPlayerStatement = connection.prepareStatement(CHECK_FOR_PLAYER)) {
            checkPlayerStatement.setString(1, name);
            ResultSet rs = checkPlayerStatement.executeQuery();
            playerExists = rs.next();

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
            savePlayerStatement.setInt(6, weapon != null ? weapon.id : 0);
            savePlayerStatement.setInt(7, currentArea != null ? currentArea.id : 0);
            savePlayerStatement.setString(8, name);
            savePlayerStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not save player " + name, e);
        }

        for(Ability ability : abilities) {
            if(!abilityDAO.playerAbilityIsSaved(name, ability.id)) {
                try(Connection connection = getDBConnection();
                    PreparedStatement statement = connection.prepareStatement(SAVE_PLAYER_ABILITY)) {
                    statement.setString(1, name);
                    statement.setInt(2, ability.id);
                    statement.execute();
                    connection.commit();

                } catch(SQLException e) {
                    throw new RuntimeException("Could not save ability " + ability.name, e);
                }
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;

        final Player player = (Player) o;

        if (strength != player.strength) return false;
        if (defence != player.defence) return false;
        if (experience != player.experience) return false;
        if (currentHealth != player.currentHealth) return false;
        if (maxHealth != player.maxHealth) return false;
        if (!name.equals(player.name)) return false;
        if (weapon != null ? !weapon.equals(player.weapon) : player.weapon != null) return false;
        if (currentArea != null ? !currentArea.equals(player.currentArea) : player.currentArea != null) return false;
        return abilities != null ? abilities.equals(player.abilities) : player.abilities == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) strength;
        result = 31 * result + (int) defence;
        result = 31 * result + experience;
        result = 31 * result + currentHealth;
        result = 31 * result + maxHealth;
        result = 31 * result + (weapon != null ? weapon.hashCode() : 0);
        result = 31 * result + (currentArea != null ? currentArea.hashCode() : 0);
        result = 31 * result + (abilities != null ? abilities.hashCode() : 0);
        return result;
    }
}
