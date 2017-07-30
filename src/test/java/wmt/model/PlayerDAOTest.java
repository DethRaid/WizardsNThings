package wmt.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import wnt.model.*;

import java.util.List;

/**
 * @author ddubois
 * @since 7/28/17.
 */
public class PlayerDAOTest extends DaoTestBase {
    PlayerDAO playerDAO = new PlayerDAO();

    @Before
    public void setUp() {
        PlayerDAO.createTable();
        WeaponDAO.createTable();
        AbilityDAO.createTables();
        AreaDAO.createTables();
    }

    @Test
    public void createTable() throws Exception {
        PlayerDAO.createTable();
        ensureTableExists("player");
    }

    @Test
    public void getNamesOfAllPlayers() throws Exception {
        List<String> names = playerDAO.getNamesOfAllPlayers();

        // No clue what the data looks like, so I guess we can just test that the code executes
    }

    @Test
    public void savePlayer() throws Exception {
        Player player = new Player("Test Player");

        player.save();

        Player testPlayer = playerDAO.getPlayer("Test Player");

        Assert.assertEquals(player, testPlayer);
    }

}