package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ddubois
 * @since 7/28/17.
 */
public class EnemyDAOTest extends DaoTestBase {
    private EnemyDAO enemyDAO;

    @Before
    public void setUp() throws Exception {
        enemyDAO = new EnemyDAO();
        EnemyDAO.createTable();
    }

    @Test
    public void saveEnemy() {
        Enemy enemy = new Enemy();
        enemy.name = "goblin";
        enemy.level = 1;
        enemy.currentHealth = 20;
        enemy.isDead = false;

        enemy.save();

        Enemy testEnemy = enemyDAO.getEnemy("goblin");

        Assert.assertEquals(enemy, testEnemy);
    }
}