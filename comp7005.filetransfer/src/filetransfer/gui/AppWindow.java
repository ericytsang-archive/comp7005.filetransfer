package filetransfer.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import filetransfer.logic.ClientApp;
import filetransfer.logic.ServerApp;

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

    private final ServerApp serverApp;
    private final ClientApp clientApp;

    /**
     * creates and configures the application window, and initializes instance
     *   data.
     */
    public AppWindow()
    {
        super(WINDOW_TITLE);

        // initialize instance data
        serverApp = new ServerApp();
        clientApp = new ClientApp();

        // create and configure application window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new MyContentPane());
        setSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        setJMenuBar(new MyMenuBar());
        setVisible(true);
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
            // create the server menu & add it to the application window
            JMenu serverOptionsMenu = new JMenu("Server");
            add(serverOptionsMenu);

            // create the start server menu item & add it to the server menu
            JMenuItem startMenuItem = new JMenuItem("Start");
            startMenuItem.addActionListener(event -> serverApp.promptStartServer(AppWindow.this));
            serverOptionsMenu.add(startMenuItem);

            // create the stop server menu item & add it to the server menu
            JMenuItem stopMenuItem = new JMenuItem("Stop");
            stopMenuItem.addActionListener(event -> serverApp.stopServer(AppWindow.this));
            serverOptionsMenu.add(stopMenuItem);

            // create the client menu & add it to the application window
            JMenu clientOptionsMenu = new JMenu("Client");
            add(clientOptionsMenu);

            // create the connect to server menu item & add it to the server menu
            JMenuItem connectMenuItem = new JMenuItem("Connect...");
            connectMenuItem.addActionListener(event -> clientApp.promptConnect(AppWindow.this));
            clientOptionsMenu.add(connectMenuItem);
        }
    }

    private class MyContentPane extends JPanel
    {
        public MyContentPane()
        {
            super();

            // set the panel's layout manager & add components to the panel
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            add(new LabeledScrollPane(clientApp.getLocalListAdapter()));
            add(new LabeledScrollPane(clientApp.getRemoteListAdapter()));
        }
    }
}
