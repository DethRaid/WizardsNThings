package wnt.view;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialog;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogResultValidator;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import wnt.controller.Controller;
import wnt.model.Ability;
import wnt.model.Area;
import wnt.model.Enemy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Susan Lunn
 */
public class GameView {
    public static Controller controller = new Controller();


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
    private static BasicWindow areaWindow;
    private static ActionListDialog mainMenu;
    private static boolean enemyPhase = false;



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
    private void handleMainMenu(){
        new ActionListDialogBuilder()
                .setTitle("WizardsNThings")
                .setDescription("Live out your fantasy as a wizard in this new\nand exciting text based dungeon crawler.")
                .addAction("New Game", this::startNewGame)
                .addAction("Previous Saves", this::listPreviousSaves)
                .build()
                .showDialog(gui);

    }

    /**
     * Creates a new player using inputted string and starts the game
     */
    private void startNewGame() {
        try {
            gui.updateScreen();
        } catch(IOException e){}

        TextInputDialogResultValidator validator = new TextInputDialogResultValidator() {
            @Override
            public String validate(String content) {
                Boolean valid = controller.getAllPlayers().contains(content);
                if(valid){
                    return "Player name already exists, please chose another name.";
                }
                return null;
            }
        };
        String player = new TextInputDialogBuilder()
                .setTitle("New Player Name")
                .setDescription("Enter your player's name")
                .setValidator(validator)
                .build()
                .showDialog(gui);
        if(player == null){
            handleMainMenu();
        }
        controller.createNewPlayer(player);
        renderArea();
    }

    private void startGame(String player){
        controller.setCurrentPlayer(player);
        renderArea();
    }

    private void listPreviousSaves(){
        BasicWindow playerWindow = new BasicWindow("Previous Saves");
        ActionListBox list = new ActionListBox();
        controller.getAllPlayers().forEach((player) -> {
            list.addItem(player, () -> {
                startGame(player);
            });
        });
        list.addItem("Cancel", this::handleMainMenu);
        playerWindow.setComponent(list);
        gui.addWindowAndWait(playerWindow);
    }

    /**
     * Renders the entire area, including description and player actions
     */
    private void renderArea(){
        renderAreaDescription();
    }

    /**
     * Renders an area's enemies and description
     */
    private void renderAreaDescription(){
        Label title = new Label(controller.getCurrentArea().name);
        Panel titlePane = new Panel();
        titlePane.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        titlePane.addComponent(title);

        Label description = new Label(controller.getCurrentArea().description);
        long alive = controller.getAllEnemies().values().stream().filter(d -> !d.isDead).count();
        Label enemies = alive == 0 ? new Label(" ") : new Label("Before you stands " + alive + " enemies!");

        Panel panel = Panels.grid(3,
                description,
                new Separator(Direction.VERTICAL),
                new Label("Name: " + controller.getCurrentPlayerName()),

                enemies,
                new Separator(Direction.VERTICAL),
                new Label("Health: " + controller.getHP()),


                new Label(""),
                new Separator(Direction.VERTICAL),
                new Label("Exp: " + controller.getExperience() + "/1000"),

                new Label(""),
                new Separator(Direction.VERTICAL),
                new Label("Level: " + controller.getLevel()));

        controller.getAllEnemies().forEach((id, enemy) -> {
            panel.addComponent(new Label(enemy.name + " - " + enemy.currentHealth + " current HP"));
            panel.addComponent(new Separator(Direction.VERTICAL));
            panel.addComponent(new Label(""));
        });
        panel.addComponent(new Separator(Direction.HORIZONTAL));
        titlePane.addComponent(panel);


        areaWindow = new BasicWindow("WizardsNThings");
        areaWindow.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        areaWindow.setComponent(titlePane);
        gui.addWindow(areaWindow);
        gui.setActiveWindow(areaWindow);

        if(alive == 0){
            renderTreasureDescription();
        }
        else{
            renderPlayerOptionsCombat();
        }
    }

    /**
     * Renders area treasure description
     */
    private void renderTreasureDescription(){
          new ActionListDialogBuilder()
                .setTitle("Reward")
                .setDescription("A treasure chest springs open before you, do you wish to change weapons?")
                .addAction("Change to " + controller.getTreasure(), () -> {
                    controller.changeWeapon();
                    renderPlayerOptionsRoom();
                })
                .addAction("Keep current " + controller.getCurrentWeapon(), this::renderPlayerOptionsRoom)
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
                .addAction("Attack with " + controller.getCurrentWeapon() + " - "
                        + controller.rawDamage() + " damage", this::selectPlayerTarget)
                .addAction("Select Ability" , this::selectPlayerAbility)
                .setCanCancel(false)
                .build()
                .showDialog(gui);

    }

    /**
     * Selects enemy for player to attack
     */
    public void selectPlayerTarget(){
        BasicWindow playerWindow = new BasicWindow("Select Target");
        Map<Integer, Enemy> enemies = controller.getAllEnemies();
        ActionListBox list = new ActionListBox();
        enemies.forEach((id, enemy) -> {
            list.addItem(enemy.name + " " + id, () -> {
                controller.Attack(id);
                enemyPhase = true;
                playerWindow.setVisible(false);
                renderArea();
            });
        });
        playerWindow.setComponent(list);
        gui.addWindowAndWait(playerWindow);
    }

    /**
     * Select player ability during combat phase
     */
    public void selectPlayerAbility() {
        BasicWindow playerWindow = new BasicWindow("Select Abilities");
        List<Ability> abilities = controller.getAbilities();
        ActionListBox list = new ActionListBox();
        abilities.forEach((ability) -> {
            String abilityDescription = "";
            if(ability.healthHealed > 0) {
               abilityDescription = ability.name + " - +" + ability.healthHealed + "HP to player";
            }
            else{
               abilityDescription = ability.name + " - " + ability.damage + " damage to all enemies";
            }
            list.addItem(abilityDescription, () -> {
               controller.castAbility(ability);
               enemyPhase = true;
               playerWindow.setVisible(false);
               renderArea();
            });
        });
        list.addItem("Cancel", this::renderPlayerOptionsCombat);
        playerWindow.setComponent(list);
        gui.addWindowAndWait(playerWindow);
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
        if(areas.size() == 0){
            System.exit(0);
        }
        new ActionListDialogBuilder()
                .setTitle("Pick the next direction")
                .setDescription("A door opens before you. Chose wisely...")
                .addAction("North - " + areas.get(0).name, () -> {
                    controller.setCurrentArea(areas.get(0));
                    renderArea();
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
