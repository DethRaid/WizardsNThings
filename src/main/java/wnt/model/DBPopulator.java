package wnt.model;

import java.util.*;

/**
 * Puts a bunch of stuff in the database
 *
 * @author ddubois
 * @since 29-Jul-17
 */
public class DBPopulator {
    private static int NUM_ENEMIES = 250;

    private static List<String> adjectives = new ArrayList<>();
    private static List<String> enemyNames = new ArrayList<>();
    private static List<String> weaponNames = new ArrayList<>();
    private static List<String> heroNames = new ArrayList<>();
    private static List<String> areaNames = new ArrayList<>();
    private static List<String> abilityNames = new ArrayList<>();

    public static void iReallyWantFreeFunctions() {
        initAdjectives();
        initEnemyNames();
        initWeaponNames();
        initHeroNames();
        initAreaNames();
        initAbilityNames();

        Random rand = new Random();

        List<Enemy> enemies = new ArrayList<>();

        for(String enemyName : enemyNames) {
            for(String adjective : adjectives) {
                String name = adjective + " " + enemyName;
                Enemy enemy = new Enemy(name, rand.nextInt(50) + 1);

                enemy.save();
                enemies.add(enemy);
            }
        }

        List<Weapon> weapons = new ArrayList<>();
        int id = 1;
        for(String weaponName : weaponNames) {
            for(String adjective : adjectives) {
                Weapon weapon = new Weapon();
                weapon.name = adjective + " " + weaponName;
                weapon.id = id;
                weapon.damage = rand.nextInt(100) + 1;
                weapon.attackSpeed = rand.nextInt(5) + 1;
                weapon.save();
                weapons.add(weapon);

                id++;
            }
        }

        List<Treasure> treasures = new ArrayList<>();
        id = 1;
        for(Weapon weapon : weapons) {
            Treasure treasure = new Treasure();
            treasure.id = id;
            treasure.weapon = weapon;
            treasure.name = weapon.name;
            treasure.save();
            treasures.add(treasure);

            id++;
        }

        List<Area> areas = new ArrayList<>();
        id = 0;
        for(String adjective : adjectives) {
            for(String areaName : areaNames) {
                Area area = new Area();
                area.id = id;
                area.name = adjective + " " + areaName;

                int numEnemies = rand.nextInt(3) + 1;
                Map<Enemy, Integer> enemyCounts = new HashMap<>();
                for(int i = 0; i < numEnemies; i++) {
                    int enemyIndex = rand.nextInt(enemies.size());
                    Enemy enemy = enemies.get(enemyIndex);
                    enemyCounts.put(enemy, rand.nextInt(5) + 1);
                }

                area.enemies = enemyCounts;
                area.description = "Really scary";

                int treasureIndex = rand.nextInt(treasures.size());
                area.treasure = treasures.get(treasureIndex);

                area.save();

                areas.add(area);
                id++;
            }
        }

        List<Ability> abilities = new ArrayList<>();
        id = 1;
        for(String abilityName : abilityNames) {
            Ability ability = new Ability();
            ability.id = id;
            ability.name = abilityName;
            ability.damage = rand.nextInt(150);
            ability.numTargets = rand.nextInt(5);
            ability.healthHealed = rand.nextBoolean() ? 0 : rand.nextInt(50);
            ability.description = "Super powerful";
            ability.levelAvailableToPlayer = (ability.damage + ability.healthHealed) * ability.numTargets / 100;
            ability.save();
            abilities.add(ability);

            id++;
        }

        for(String heroName : heroNames) {
            Player player = new Player();
            player.name = heroName;
            player.experience = rand.nextInt(50) * 1000;
            player.strength = (short) (rand.nextInt(100) + 1);
            player.defence = (short) (rand.nextInt(100) + 1);
            player.maxHealth = rand.nextInt(5000) + 1;
            player.experience = Math.max(player.strength, player.defence) / 2 * 1000;
            player.currentHealth = rand.nextInt(player.maxHealth) + 1;

            int areaIndex = rand.nextInt(areas.size());
            player.currentArea = areas.get(areaIndex);

            int weaponIndex = rand.nextInt(weapons.size());
            player.weapon = weapons.get(weaponIndex);

            for(int i = 0; i < player.experience / 1000; i++) {
                int abilityIndex = rand.nextInt(abilities.size());
                Ability ability = abilities.get(abilityIndex);
                player.abilities.add(ability);
            }

            player.save();

        }
    }

    private static void initAbilityNames() {
        abilityNames.add("Fireball");
        abilityNames.add("Lightning Bolt");
        abilityNames.add("Heals");
        abilityNames.add("Levitation");
        abilityNames.add("Accio");
        abilityNames.add("Balefire");
        abilityNames.add("Knowledge");
        abilityNames.add("Giving Birth");
        abilityNames.add("Yelling");
        abilityNames.add("Coding");
        abilityNames.add("The Room");
    }

    private static void initAreaNames() {
        areaNames.add("Swamp");
        areaNames.add("Castle");
        areaNames.add("Death Star");
        areaNames.add("Manhattan");
        areaNames.add("Grove");
        areaNames.add("Cleavland");
        areaNames.add("Hell");
        areaNames.add("Inner Sanctum");
        areaNames.add("Forest Kindgom");
        areaNames.add("Ruined City");
        areaNames.add("Desert");
        areaNames.add("North of the Wall");
        areaNames.add("Essos");
        areaNames.add("Sussix");
        areaNames.add("Brussles");
        areaNames.add("Long Island");
        areaNames.add("Short Island");
        areaNames.add("Reasonably Sized Island");
        areaNames.add("Flooded City");
        areaNames.add("Resistance Camp");
        areaNames.add("Sewers");
        areaNames.add("Colorado");
        areaNames.add("Nowhere");
    }

    private static void initHeroNames() {
        heroNames.add("John");
        heroNames.add("Josh");
        heroNames.add("Corey in the House");
        heroNames.add("Susan Lunn");
        heroNames.add("James Ferris");
        heroNames.add("David Dubois");
        heroNames.add("Joey");
        heroNames.add("Jodie");
        heroNames.add("Max");
        heroNames.add("Adam Jensen");
        heroNames.add("Snake Vargas");
        heroNames.add("USA");
        heroNames.add("Kim Jong Il");
        heroNames.add("Eddie Riggs");
        heroNames.add("Conan");
        heroNames.add("Karl Marx");
        heroNames.add("Vladimir Lenin");
        heroNames.add("Haskell");
        heroNames.add("Gypsy Danger");
        heroNames.add("King in da Norf");
    }

    private static void initWeaponNames() {
        weaponNames.add("Sword");
        weaponNames.add("Spear");
        weaponNames.add("Dagger");
        weaponNames.add("Lance");
        weaponNames.add("Crossbow");
        weaponNames.add("Staff");
        weaponNames.add("Heat-Seeking Missile");
        weaponNames.add("ICBM");
        weaponNames.add("Longbow");
        weaponNames.add("Mace");
        weaponNames.add("Chastity Belt");
        weaponNames.add("Flail");
        weaponNames.add("Justin Beiber");
        weaponNames.add("Incontinence");
    }

    private static void initEnemyNames() {
        enemyNames.add("Goblin");
        enemyNames.add("Troll");
        enemyNames.add("Hobgoblin");
        enemyNames.add("Ghost");
        enemyNames.add("Vampire");
        enemyNames.add("Zombie");
        enemyNames.add("Orc");
        enemyNames.add("Trolloc");
        enemyNames.add("Human");
        enemyNames.add("Rapper");
        enemyNames.add("Network Executive");
        enemyNames.add("White Walker");
        enemyNames.add("Snake");
        enemyNames.add("Honey Badger");
        enemyNames.add("Dragon");
        enemyNames.add("Pop star");
    }

    private static void initAdjectives() {
        adjectives.add("Strong");
        adjectives.add("Weak");
        adjectives.add("Puny");
        adjectives.add("Huge");
        adjectives.add("Hairy");
        adjectives.add("Smelly");
        adjectives.add("Gassy");
        adjectives.add("Damp");
        adjectives.add("Scaly");
        adjectives.add("Hulking");
        adjectives.add("Ghostly");
        adjectives.add("Concrete");
        adjectives.add("Stressed");
        adjectives.add("Confident");
        adjectives.add("Overconfident");
        adjectives.add("Timid");
        adjectives.add("Tired");
        adjectives.add("Well-Rested");
        adjectives.add("Straight-A Student");
        adjectives.add("Preppy");
        adjectives.add("Vampiric");
        adjectives.add("Goffic");
        adjectives.add("Ludacris");
        adjectives.add("Mediocre");
        adjectives.add("Wise");
        adjectives.add("Sharp");
        adjectives.add("Dull");
        adjectives.add("Bloodletting");
        adjectives.add("Shocking");
    }



}
