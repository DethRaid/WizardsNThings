package wmt.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import wnt.model.Weapon;
import wnt.model.WeaponDAO;

/**
 * @author ddubois
 * @since 16-Jul-17
 */
public class WeaponDAOTest {
    private WeaponDAO weaponDAO;

    @Before
    public void setup() {
        weaponDAO = new WeaponDAO();
        weaponDAO.createTable();
    }

    @Test
    public void saveWeaponTest() {
        Weapon weapon = new Weapon();
        weapon.name = "Sword of Slaying";
        weapon.id = 0;
        weapon.attackSpeed = 3;
        weapon.damage = 4;

        weapon.save();
    }

    @Test
    public void getWeaponTest() {
        Weapon weapon = weaponDAO.getWeapon(1);
        Assert.assertEquals(weapon.name, "Sword of Slaying");
        System.out.println(weapon.name);
    }

    @Test
    public void testGetStartingWeapon() {
        Weapon weapon = weaponDAO.getStartingWeapon();
        Assert.assertEquals(weapon.name, "Sword of Slaying");
        Assert.assertEquals(weapon.attackSpeed, 3);
        Assert.assertEquals(weapon.damage, 4);
    }
}
