package model;

import java.util.Observable;

/**
 * @author ddubois
 * @since 14-Jul-17
 */
public class Weapon extends Observable {
    public int id;
    public String name;
    public int damage;
    public int attackSpeed;
}
