package model;

import java.util.List;

/**
 * The player of a game
 *
 * @author ddubois
 * @since 7/11/17.
 */
public class Player {
    public String name;
    public byte strength;
    public byte defence;
    public int experience;
    public int currentHealth;
    public int maxHealth;
    public Weapon weapon;
    public Area currentArea;

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
    }
}
