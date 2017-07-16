package controller;

import model.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Jef1771 on 7/15/17.
 */
public class Controller {

    //DAO's
    PlayerDAO playerDAO;
    AreaDAO areaDAO;
    EnemyDAO enemyDAO;
    WeaponDAO weaponDAO;
    TreasureDAO treasureDAO;


    Map<Integer, Enemy> enemies;
    Area area;
    Player currentPlayer;

    public Controller(){
        playerDAO = new PlayerDAO();
        areaDAO = new AreaDAO();
        enemyDAO = new EnemyDAO();
        weaponDAO = new WeaponDAO();
        treasureDAO = new TreasureDAO();
    }

    public void setCurrentPlayer(String name){
        currentPlayer = playerDAO.getPlayer(name);
        area = currentPlayer.currentArea;
        populateEnemies();
    }

    public void setCurrentArea(Area a){
        area = a;
        currentPlayer.currentArea = a;
        populateEnemies();
    }

    public void createNewPlayer(String name){
        currentPlayer = playerDAO.createPlayer(name);
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
     * Get a Map of all the enemies names in the current Area
     *
     *
     *
     * @return List of all enemies names
     */
    public Map<Integer, String> getAllEnemies(){
        Map<Integer, String> enemyNames = new HashMap<>();
        enemies.forEach((id, enemy) -> {
            enemyNames.put(id, enemy.name);
        });
        return enemyNames;
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
     * Get the current weapon the current player is using
     *
     * @return the player's weapon
     */
    public String getCurrentWeapon(){
        return currentPlayer.weapon.name;
    }

    /**
     * Get all the abilities of a player
     * @return
     */
    public Set<String> getAbilities(){
        return currentPlayer.abilities.keySet();
    }

    /**
     * Get 4 possible areas for when a player is travelling to a new room
     *
     * @return a list of the possible areas
     */
    public List<Area> getPossibleAreas(){
        return AreaDAO.getAreasInLevelRange(getPlayerLevel(), 4);
    }

    public String getTreasure(){
        return area.treasure.name;
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
                enemy.changeHealth(calculatePlayerDamage(enemy));
            }
            currentPlayer.changeHealth(calculateEnemyDamage(enemy));
        });
    }

    /**
     * Cast a players ability
     *
     * @param abilityName - the name of the ability
     */
    public void castAbility(String abilityName){
        Ability ability = currentPlayer.abilities.get(abilityName);
        if(ability.damage > 0){
            enemies.values().forEach((enemy -> {
                enemy.changeHealth((ability.damage) * -1);
            }));
        }
        else if(ability.healthHealed > 0){
            currentPlayer.changeHealth(ability.healthHealed);
        }
    }

    public void changeWeapon(){
        currentPlayer.weapon = area.treasure.weapon;
    }


    //CONTROLLER INITIALIZATION HELPERS

    /**
     * Calculate the Player's level
     *
     * Currently 1 level equals 10 experience points
     *
     * @return current level
     */
    private int getPlayerLevel(){
        return (currentPlayer.experience/10);
    }

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

    private Enemy createNewEnemy(Enemy e){
        Enemy newEnemy = new Enemy();
        newEnemy.level = e.level;
        newEnemy.curHealth = e.curHealth;
        newEnemy.isDead = e.isDead;
        newEnemy.name = e.name;
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
