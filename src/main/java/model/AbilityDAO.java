package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class AbilityDAO extends DAOBase {
    private static String CREATE_ABILITY_TABLE =
            "CREATE TABLE IF NOT EXISTS ability(" +
             "id INT PRIMARY KEY NOT NULL" +
            ",name VARCHAR(128) NOT NULL" +
            ",damage INT NOT NULL" +
            ",num_targets INT NOT NULL" +
            ",health_healed INT NOT NULL" +
            ",description VARCHAR(512) NOT NULL" +
            ",level_available_to_player INT NOT NULL" +
            ");";

    private static String CREATE_PLAYER_ABILITY_TABLE =
            "CREATE TABLE IF NOT EXISTS player_ability(" +
             "player_name INT NOT NULL" +
            ",ability_id INT NOT NULL" +
            ");";

    private static String GET_ABILITIES_FOR_PLAYER = "SELECT * FROM ability WHERE ability.id IN (" +
            "SELECT ability_id FROM player_ability WHERE player_ability.player_name = ?" +
            ");";
    private static String GET_ABILITY = "SELECT * FROM ability WHERE ability.id = ?";
    private static String GET_ABILITY_WITH_LEVEL = "SELECT * FROM ability WHERE ability.level_available_to_player = ?;";

    public static void createTables() {
        try(Connection connection = getDBConnection()) {
            PreparedStatement createAbilitytableStatement = connection.prepareStatement(CREATE_ABILITY_TABLE);
            createAbilitytableStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create ability table", e);
        }

        try(Connection connection = getDBConnection()) {
            PreparedStatement createPlayerAbilityTableStatement = connection.prepareStatement(CREATE_PLAYER_ABILITY_TABLE);
            createPlayerAbilityTableStatement.execute();
            connection.commit();

        } catch(SQLException e) {
            throw new RuntimeException("Could not create player_ability table", e);
        }
    }

    public Ability getAbility(int abilityId) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement getAbilityStatement = connection.prepareStatement(GET_ABILITY);
            getAbilityStatement.setInt(0, abilityId);
            ResultSet abilitySet = getAbilityStatement.executeQuery();

            return new Ability(abilitySet);

        } catch(SQLException e) {
            throw new RuntimeException("Could not get ability " + abilityId, e);
        }
    }

    public Map<String, Ability> getAllAbilitiesForPlayer(Player player) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement getAbilitiesForPlayerStatement = connection.prepareStatement(GET_ABILITIES_FOR_PLAYER);
            getAbilitiesForPlayerStatement.setString(1, player.name);
            ResultSet abilitiesSet = getAbilitiesForPlayerStatement.executeQuery();

            Map<String, Ability> abilities = new HashMap<>();
            while(abilitiesSet.next()) {
                Ability ability = new Ability(abilitiesSet);
                abilities.put(ability.name, ability);
            }

            return abilities;

        } catch(SQLException e) {
            throw new RuntimeException("Could not get abilities for player " + player.name, e);
        }
    }

    public List<Ability> getAbilitiesWithLevel(int level) {
        try(Connection connection = getDBConnection()) {
            PreparedStatement getAbilitiesStatement = connection.prepareStatement(GET_ABILITY_WITH_LEVEL);
            ResultSet rs = getAbilitiesStatement.executeQuery();

            List<Ability> abilities = new ArrayList<>();
            while(rs.next()) {
                abilities.add(new Ability(rs));
            }

            return abilities;

        } catch(SQLException e) {
            throw new RuntimeException("Could not get abilities with level " + level, e);
        }
    }
}
