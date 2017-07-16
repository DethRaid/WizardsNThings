package model;

import java.util.Observable;

/**
 * @author ddubois
 * @since 15-Jul-17
 */
public class Enemy extends Observable {
    public String name;
    public int level;
    public int curHealth;
    public boolean isDead;

    public int getStrength() {
        return level * 2;
    }

    public int getDefense() {
        return level * 2;
    }

    public int getMaxHealth() {
        return level * 100;
    }
}
