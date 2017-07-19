import model.Weapon;
import model.WeaponDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        weapon.name = "Sword of slaying";
        weapon.id = 0;
        weapon.attackSpeed = 3;
        weapon.damage = 4;

        weapon.save();
    }

    @Test
    public void getWeaponTest() {
        Weapon weapon = weaponDAO.getWeapon(0);
        Assert.assertEquals(weapon.name, "Sword of Slaying");
        System.out.println(weapon.name);
    }
}
