package filetransfer.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

import filetransfer.Application;

public class AppWindow extends JFrame
{
    /**
     * unique version of this swing component.
     */
    private static final long serialVersionUID = 1L;

    /**
     * initial width of the application window.
     */
    private static final int WINDOW_WIDTH = 500;

    /**
     * initial height of the application window.
     */
    private static final int WINDOW_HEIGHT = 300;

    /**
     * title of the application window shown in the title bar.
     */
    private static final String WINDOW_TITLE = "Eric's COMP 7005 Assignment 1";

    private final Application app;

    /**
     * creates and configures the application window, and initializes instance
     *   data.
     */
    public AppWindow()
    {
        // create and configure application window
        super(WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new MyContentPane());
        setSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        setJMenuBar(new MyMenuBar());
        setVisible(true);

        // initialize instance data
        app = new Application();
    }

    /**
     * main method...
     *
     * @param args unused command line arguments.
     */
    public static void main(String[] args)
    {
        new AppWindow();
    }

    // private interface: inner classes

    private class MyMenuBar extends JMenuBar
    {
        public MyMenuBar()
        {
            // create the menu & add it to the application window
            JMenu serverOptionsMenu = new JMenu("Server Options");
            add(serverOptionsMenu);

            // create the start server menu item
            JMenuItem startMenuItem = new JMenuItem("Start Server");
            startMenuItem.addActionListener(event -> app.promptStartServer(AppWindow.this));
            serverOptionsMenu.add(startMenuItem);

            // create the stop server menu item
            JMenuItem stopMenuItem = new JMenuItem("Stop Server");
            stopMenuItem.addActionListener(event -> app.stopServer(AppWindow.this));
            serverOptionsMenu.add(stopMenuItem);
        }
    }

    private class MyContentPane extends JPanel
    {
        public MyContentPane()
        {
            super();

            // set the panel's layout manager & add components to the panel
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            add(new LabeledScrollPane("Local Files:"));
            add(new LabeledScrollPane("Remote Files:"));

            // todo: remove the code below, and put it into the appropriate place (a view holder....)
            // example of how to create a context menu!
            JPopupMenu contextMenu = new JPopupMenu();
            contextMenu.add(new JMenuItem("Item 1"));
            contextMenu.add(new JMenuItem("Item 2"));
            contextMenu.add(new JMenuItem("Item 3"));
            setComponentPopupMenu(contextMenu);
        }
    }
}
