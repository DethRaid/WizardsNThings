package view;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialog;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author ddubois
 * @since 7/11/17.
 */
public class GameView {

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
                        // Do 1st thing...
                    }
                })
                .addAction("Previous Saves", new Runnable() {
                    @Override
                    public void run() {
                        // Do 2nd thing...
                    }
                })
                .build()
                .showDialog(gui);

    }

    public static void main(String[] args) throws IOException{
        GameView wizardsGame = new GameView();
        wizardsGame.openMainMenu();
    }
}
