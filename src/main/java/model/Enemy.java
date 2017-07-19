package model;

import java.util.Observable;

/**
 * @author ddubois
 * @since 15-Jul-17
 */
public class Enemy extends Observable {
    public String name;
    public int level;
    public int currentHealth;
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

    public void changeHealth(int healthChange) {
        currentHealth = Math.min(currentHealth + healthChange, getMaxHealth());

        if(currentHealth <= 0) {
            // TODO: Fire player dead event
        }

        setChanged();
        notifyObservers(this);
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "name='" + name + '\'' +
                '}';
    }
}
