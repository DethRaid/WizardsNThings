package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static model.DAOBase.getDBConnection;
import static org.junit.Assert.*;

/**
 * @author ddubois
 * @since 7/28/17.
 */
public class TreasureDAOTest extends DaoTestBase {
    private TreasureDAO treasureDAO;

    @Before
    public void setUp() throws Exception {
        treasureDAO = new TreasureDAO();
        WeaponDAO.createTable();
    }

    @Test
    public void createTable() throws Exception {
        TreasureDAO.createTable();
        ensureTableExists("treasure");
    }

    @Test
    public void saveTreasure() throws Exception {
        Treasure treasure = new Treasure();
        Weapon weapon = new Weapon();
        weapon.id = 100;
        weapon.attackSpeed = 3;
        weapon.damage = 4;
        weapon.name = "Sword of Slaying";
        weapon.save();
        weapon = new WeaponDAO().getWeapon(1);
        treasure.id = 1;
        treasure.name = "Treasure of St Aldrin";
        treasure.weapon = weapon;

        treasureDAO.deleteTreasure(treasure);

        treasure.save();

        Treasure treasure1 = treasureDAO.getTreasure(1);
        Assert.assertEquals(treasure, treasure1);
    }
}