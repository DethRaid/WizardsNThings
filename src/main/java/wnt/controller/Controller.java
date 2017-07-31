package wnt.controller;

import wnt.model.*;

import java.util.*;

/**
 * Created by Jef1771 on 7/15/17.
 */
public class Controller {

    //DAO's
    private PlayerDAO playerDAO = new PlayerDAO();
    private AreaDAO areaDAO = new AreaDAO();
    private EnemyDAO enemyDAO = new EnemyDAO();
    private WeaponDAO weaponDAO = new WeaponDAO();
    private TreasureDAO treasureDAO = new TreasureDAO();


    //Instances
    private Map<Integer, Enemy> enemies;
    private Area area;
    private Player currentPlayer;

    /**
     * Constructor
     */
    public Controller(){
        WeaponDAO.createTable();
        TreasureDAO.createTable();
        EnemyDAO.createTable();
        AreaDAO.createTables();
        PlayerDAO.createTable();
        AbilityDAO.createTables();
        AreaDAO.createClearedAreasTable();

        UsersDAO.createUsers();
        UsersDAO.createPermissions();

        playerDAO = new PlayerDAO();
        areaDAO = new AreaDAO();
        enemyDAO = new EnemyDAO();
        weaponDAO = new WeaponDAO();
        treasureDAO = new TreasureDAO();
        if(!enemyDAO.hasExistingData()){
            DBPopulator.iReallyWantFreeFunctions();
        }
    }


    /**
     * Set the current player to the player with the given name
     * @param name - the name of the player
     */
    public void setCurrentPlayer(String name){
        currentPlayer = playerDAO.getPlayer(name);
        area = currentPlayer.currentArea;
        populateEnemies();
    }

    /**
     * Set the current area to the given area
     * @param a - the area to set
     */
    public void setCurrentArea(Area a){
        if(area != null){area.setAreaAsCleared(currentPlayer.name);}
        area = a;
        currentPlayer.currentArea = a;
        populateEnemies();
    }


    /**
     * Create a new player and save them in the DB
     *
     * @param name the name of the new player
     */
    public void createNewPlayer(String name){
        currentPlayer = new Player(name);
        currentPlayer.weapon = weaponDAO.getStartingWeapon();
        currentPlayer.currentArea = areaDAO.getAreasInLevelRange(Integer.parseInt(getLevel()), 1).get(0);
        currentPlayer.save();
        area = currentPlayer.currentArea;
        populateEnemies();
    }

    //VIEW USE THESE METHODS TO GET INFO YOU NEED

    /**
     * Get a list of all the player names in the DB
     *
     * @return List of all player names
     */
    public List<String> getAllPlayers(){
        return playerDAO.getNamesOfAllPlayers();
    }

    /**
     * Get the current area that the player is in
     * @return the Area
     */
    public Area getCurrentArea(){
        return currentPlayer.currentArea;
    }

    /**
     * Get the players current health
     * @return players' health
     */
    public String getHP(){
        return Integer.toString(currentPlayer.currentHealth);
    }

    /**
     * Get the current level of the player
     * Level is calculated by total XP / 1000
     * If the above calculation is less than 0, return 1
     * @return Player's level
     */
    public String getLevel(){
        int level = (currentPlayer.experience / 1000);
        return (level > 1) ? Integer.toString(level) : "1";
    }

    /**
     * Get current player's experience
     * @return
     */
    public String getExperience(){
        return Integer.toString(currentPlayer.experience);
    }

    /**
     * Get a Map of all the enemies names in the current Area
     *
     * @return List of all enemies names
     */
    public Map<Integer, Enemy> getAllEnemies(){
        return enemies;
    }

    /**
     * Get the current player's name
     *
     * @return String of player name
     */
    public String getCurrentPlayerName(){
        return currentPlayer.name;
    }

    /**
     * Get the player's current health
     *
     * @return player's current health
     */
    public String getPlayerHealth(){
        return Integer.toString(currentPlayer.currentHealth);
    }

    /**
     * Get the current weapon the current player is using
     *
     * @return the player's weapon
     */
    public String getCurrentWeapon(){
        return currentPlayer.weapon.name;
    }

    /**
     * Get all the abilities of a player
     * @return The names of all the abilities of the player
     */
    public List<Ability> getAbilities(){
        return currentPlayer.abilities;
    }

    /**
     * Get 4 possible areas for when a player is travelling to a new room
     *
     * @return a list of the possible areas
     */
    public List<Area> getPossibleAreas(){
        return areaDAO.getAreasInLevelRange(Integer.parseInt(getLevel()), 4);
    }

    /**
     * Get the name of the treasure in the current area
     *
     * @return the name of the treasure
     */
    public String getTreasure(){
        return area.treasure.name;
    }

    /**
     * Check if all of the enemies are dead
     *
     * @return true if all are dead, false otherwise
     */
    public boolean areEnemiesAllDead(){
        return (enemies.size() == 0);
    }



    //VIEW USE THESE METHODS TO COMPLETE A USER ACTION

    /**
     * Combat phase of player, attacking an enemy with their weapon
     *
     * After the damage is calculated to the enemy, all the enemies
     * in the area will then attack the player
     *
     * @param enemyId The target enemy
     */
    public void Attack(Integer enemyId){
        enemies.forEach((id, enemy) -> {
            if (id.equals(enemyId)){
                enemy.changeHealth(calculatePlayerDamage(enemy) * -1);
                if(enemy.isDead){
                    enemies.remove(id, enemy);
                    return;
                }
            }
            currentPlayer.changeHealth(calculateEnemyDamage(enemy) * -1);
        });
    }

    /**
     * Cast a players ability
     *
     * @param ability - the ability to cast
     */
    public void castAbility(Ability ability){
        if(ability.damage > 0){
            enemies.forEach((id ,enemy) -> {
                enemy.changeHealth((ability.damage) * -1);
                if(enemy.isDead){
                    enemies.remove(id, enemy);
                }
            });
        }
        else if(ability.healthHealed > 0){
            currentPlayer.changeHealth(ability.healthHealed);
        }
    }

    /**
     * Change the Player's current weapon to the weapon of the current area
     */
    public void changeWeapon(){
        currentPlayer.weapon = area.treasure.weapon;
    }


    //CONTROLLER INITIALIZATION HELPERS

    /**
     * Instantiate all of the enemies in the area and give them each a unique ID
     */
    private void populateEnemies(){
        enemies = new HashMap<>();
        int ID = 0;
        for(Map.Entry<Enemy, Integer> entry : area.enemies.entrySet()){
            for(int i = 0; i < entry.getValue(); i++){
                enemies.put(ID, createNewEnemy(entry.getKey()));
                ID += 1;
            }
        }
    }

    /**
     * Create a new instance of the given enemy
     * @param e - the enemy to copy (like a template)
     * @return - the new enemy
     */
    private Enemy createNewEnemy(Enemy e){
        Enemy newEnemy = new Enemy(e.name, e.level);
        newEnemy.currentHealth = e.currentHealth;
        newEnemy.isDead = e.isDead;
        return newEnemy;
    }

    //CONTROLLER COMBAT HELPER METHODS

    /**
     * Calculate the damage dealt by the player
     *
     * @return total damage done
     */
    private int calculatePlayerDamage(Enemy e){
        //TODO - Makes this more meaningful, such as account for defense and speed
        int rawDamage = (currentPlayer.strength + currentPlayer.weapon.damage + currentPlayer.weapon.attackSpeed);
        return ((rawDamage - e.getDefense()) * -1);
    }

    /**
     * Calculate the damage dealt by an enemy
     * @param e - the enemy
     * @return total damage done
     */
    private int calculateEnemyDamage(Enemy e){
        //TODO - Make this more meaningful, such as account for defense
        int rawDamage = e.getStrength();
        return (rawDamage * -1);
    }
}
