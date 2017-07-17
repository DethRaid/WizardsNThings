package view;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialog;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import model.*;
import controller.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Susan Lunn
 */
public class GameView {
    public static Controller controller;


    /*
     * Lanterna's terminal.
     */
    private static Terminal terminal;

    /*
     * A layer to put on the top of the Terminal object, which is a kind of a
     * screen buffer.
     */
    private static Screen screen;

    /*
     * The main GUI on the screen
     */
    private static MultiWindowTextGUI gui;
    private static BasicWindow menuWindow;



    /**
     * Instantiates a new GameView object.
     */
    private GameView() throws IOException
    {
        // Create a new terminal. See https://code.google.com/p/lanterna/wiki/UsingTerminal
        // for reference.
        terminal = new DefaultTerminalFactory().createTerminal();

        screen = new TerminalScreen(terminal);
        screen.setCursorPosition(null);		// Hack to hide cursor.
        screen.startScreen();				// terminal enters in private mode,

    }

    /**
     * Creates the main menu and takes action on the selected option.
     */
    private void openMainMenu() throws IOException
    {

        renderMainMenu();

        screen.refresh();

        handleMainMenu();

        terminal.exitPrivateMode();
    }

    /**
     * Renders main description text
     * @throws IOException
     */
    private void renderMainMenu() throws IOException{
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        panel.addComponent(new Label("Live out your fantasy as a wizard in this new\nand exciting text based dungeon crawler."));
        panel.addComponent(new Label("Press <Esc> to begin"));

        menuWindow = new BasicWindow("WizardsNThings");
        menuWindow.setHints(Arrays.asList(Window.Hint.CENTERED));
        menuWindow.setComponent(panel);
        menuWindow.setCloseWindowWithEscape(true);

        // create and start gui
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(menuWindow);
    }

    /**
     * Presents the menu dialog box for new game or listing previous saves
     * @throws IOException
     */
    private void handleMainMenu() throws IOException{
        new ActionListDialogBuilder()
                .setTitle("WizardsNThings")
                .setDescription("Live out your fantasy as a wizard in this new\nand exciting text based dungeon crawler.")
                .addAction("New Game", new Runnable() {
                    @Override
                    public void run() {
                        startNewGame();
                        return;
                    }
                })
                .addAction("Previous Saves", new Runnable() {
                    @Override
                    public void run() {
                        listPreviousSaves();
                    }
                })
                .build()
                .showDialog(gui);

    }

    /**
     * Creates a new player using inputted string and starts the game
     */
    private void startNewGame() {
        String player = new TextInputDialogBuilder()
                .setTitle("New Player Name")
                .setDescription("Enter your player's name")
                .build()
                .showDialog(gui);
        screen.clear();
        // set current area -> call to controller ( pick first area in list of areas)
        // go create a player in controller and have them send it back
    }

    private void listPreviousSaves(){
        // find list of players from database

    }

    /**
     * Renders the entire area, including description and player actions
     */
    private void renderArea(){

    }

    /**
     * Renders an area's enemies and description
     */
    private void renderAreaDescription(){

    }

    /**
     * Renders area treasure description
     */
    private void renderTreasureDescription(){
          new ActionListDialogBuilder()
                .setTitle("Reward")
                .setDescription("A treasure chest springs open before you, do you wish to change weapons?")
                .addAction("Change to " + controller.getTreasure(), new Runnable() {
                    @Override
                    public void run() {
                        controller.changeWeapon();
                        renderPlayerOptionsRoom();
                    }
                })
                .addAction("Keep current " + controller.getCurrentWeapon(), new Runnable() {
                    @Override
                    public void run() {
                        renderPlayerOptionsRoom();
                    }
                })
                .setCanCancel(false)
                .build()
                .showDialog(gui);
    }

    /**
     * Render's a player's options during combat phase
     * Options are:
     *
     * 1) Attack with weapon
     * 2) Select ability
     */
    private void renderPlayerOptionsCombat(){
        new ActionListDialogBuilder()
                .setTitle("Combat Phase")
                .setDescription("Defeat the enemies to move forward!")
                .addAction("Attack with " + controller.getCurrentWeapon(), new Runnable() {
                    @Override
                    public void run() {
                        selectPlayerTarget();
                    }
                })
                .addAction("Select Ability", new Runnable() {
                    @Override
                    public void run() {
                        selectPlayerAbility();
                    }
                })
                .build()
                .showDialog(gui);
    }

    /**
     * Selects enemy for player to attack
     */
    public void selectPlayerTarget(){
        Map<Integer, String> enemies = controller.getAllEnemies();
        ActionListBox list = new ActionListBox();
        enemies.forEach((id, enemy) -> {
            list.addItem(enemy + " " + id, new Runnable() {
                @Override
                public void run() {
                    controller.Attack(id);
                    // renderArea();
                }
            });
        });

    }

    /**
     * Select player ability during combat phase
     */
    public void selectPlayerAbility() {
        Set<String> abilities = controller.getAbilities();
        ActionListBox list = new ActionListBox();
        abilities.forEach((name) -> {
            list.addItem(name, new Runnable() {
                @Override
                public void run() {
                   controller.castAbility(name);
                    // renderArea()
                }
            });
        });
        list.addItem("Cancel", new Runnable() {
            @Override
            public void run() {
                renderPlayerOptionsCombat();
            }
        });
    }

    /**
     * Select direction player can move in after combat phase
     *
     * 1) Move North
     * 2) Move South
     * 3) Move West
     * 4) Move East
     */
    private void renderPlayerOptionsRoom() {
        // obtain list of areas from controller, pick the top four
        List<Area> areas = controller.getPossibleAreas();
        new ActionListDialogBuilder()
                .setTitle("Pick the next direction")
                .setDescription("Around you, four doors open. Chose wisely...")
                .addAction("North - " + areas.get(0).description, new Runnable() {
                    @Override
                    public void run() {
                        controller.setCurrentArea(areas.get(0));
                        renderArea();
                    }
                })
                .addAction("South - " + areas.get(1).description, new Runnable() {
                    @Override
                    public void run() {
                        controller.setCurrentArea(areas.get(1));
                        renderArea();
                    }
                })
                .addAction("West - " + areas.get(2).description, new Runnable() {
                    @Override
                    public void run() {
                        // set currArea as cleared
                        controller.setCurrentArea(areas.get(2));
                        renderArea();
                    }
                })
                .addAction("East - " + areas.get(3).description, new Runnable() {
                    @Override
                    public void run() {
                        // set currArea as cleared
                        controller.setCurrentArea(areas.get(3));
                        renderArea();
                    }
                })
                .setCanCancel(false)
                .build()
                .showDialog(gui);

    }


    public static void main(String[] args) throws IOException{
        GameView wizardsGame = new GameView();
        wizardsGame.openMainMenu();
    }
}
