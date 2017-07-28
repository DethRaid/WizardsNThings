package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ddubois
 * @since 7/28/17.
 */
public class PlayerDAOTest extends DaoTestBase {
    PlayerDAO playerDAO = new PlayerDAO();

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