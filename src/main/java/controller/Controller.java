package controller;

import model.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Jef1771 on 7/15/17.
 */
public class Controller {

    //DAO's
    PlayerDAO playerDAO = new PlayerDAO();
    AreaDAO areaDAO = new AreaDAO();
    EnemyDAO enemyDAO = new EnemyDAO();
    WeaponDAO weaponDAO = new WeaponDAO();
    TreasureDAO treasureDAO = new TreasureDAO();


    //Instances
    Map<Integer, Enemy> enemies;
    Area area;
    Player currentPlayer;

    /**
     * Constructor
     */
    public Controller(){
        playerDAO = new PlayerDAO();
        playerDAO.createTable();
        areaDAO = new AreaDAO();
        areaDAO.createTables();
        enemyDAO = new EnemyDAO();
        enemyDAO.createTable();
        weaponDAO = new WeaponDAO();
        weaponDAO.createTable();
        treasureDAO = new TreasureDAO();
        treasureDAO.createTable();
        AbilityDAO abilityDAO = new AbilityDAO();
        abilityDAO.createTables();
        if(!enemyDAO.hasExistingData()){
            DBPopulator.iReallyWantFreeFunctions();
        }
    }

    public void createEnemies(){
        Enemy enemy = new Enemy();
        enemy.name = "goblin";
        enemy.level = 1;
        enemy.currentHealth = 20;
        enemy.isDead = false;

        enemy.save();

        enemy = new Enemy();
        enemy.name = "spider";
        enemy.level = 1;
        enemy.currentHealth = 20;
        enemy.isDead = false;

        enemy.save();

        enemy = new Enemy();
        enemy.name = "orc";
        enemy.level = 1;
        enemy.currentHealth = 20;
        enemy.isDead = false;
        enemy.save();
    }

    public void createAreas(){

    }

    public void createWeapons(){
        Weapon weapon = new Weapon();
        weapon.name = "Sword of Slaying";
        weapon.id = 0;
        weapon.attackSpeed = 3;
        weapon.damage = 4;

        weapon.save();

        Treasure treasure = new Treasure();
        treasure.id = 1;
        treasure.name = "Treasure of St Aldrin";
        treasure.weapon = weapon;

        treasureDAO.deleteTreasure(treasure);

        treasure.save();

        Area area = new Area();
        area.id = 0;
        area.name = "Swamp";
        area.description = "Muggy and hot, with the smell of decay and slow moving things.";
        area.treasure = treasure;
        area.enemies.put(enemyDAO.getEnemy("spider"), 1);
        area.save();

        area = new Area();
        area.id = 1;
        area.name = "Wasteland";
        area.description = "Sand stretches before you, it is hot and oppressive";
        area.treasure = treasure;
        area.enemies.put(enemyDAO.getEnemy("goblin"), 1);
        area.save();

        area = new Area();
        area.id = 2;
        area.name = "Frozen Tundra";
        area.description = "Icicles form on your hair and you feel a bone chilling cold. Snow dots the landscape";
        area.treasure = treasure;
        area.enemies.put(enemyDAO.getEnemy("orc"), 1);
        area.save();

        area = new Area();
        area.id = 3;
        area.name = "Volcano";
        area.description = "Fire and magma explodes around you. The acrid smell of sulfer assaults your senses.";
        area.treasure = treasure;
        enemies = new HashMap<>();
        area.enemies.put(enemyDAO.getEnemy("orc"), 1);
        area.save();
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
        //TODO - Set the players' current weapon
        currentPlayer.currentArea = areaDAO.getAreasInLevelRange(getLevel(), 1).get(0);
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
    public int getHP(){
        return currentPlayer.currentHealth;
    }

    /**
     * Get the current level of the player
     * Level is calculated by total XP / 1000
     * If the above calculation is less than 0, return 1
     * @return Player's level
     */
    public int getLevel(){
        int level = (currentPlayer.experience / 1000);
        return (level > 1) ? level : 1;
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
    public int getPlayerHealth(){
        return currentPlayer.currentHealth;
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
        return areaDAO.getAreasInLevelRange(getLevel(), 4);
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
     * @param abilityName - the name of the ability
     */
    public void castAbility(String abilityName){
        Ability ability = currentPlayer.abilities.get(abilityName);
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
        Enemy newEnemy = new Enemy();
        newEnemy.level = e.level;
        newEnemy.currentHealth = e.currentHealth;
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
