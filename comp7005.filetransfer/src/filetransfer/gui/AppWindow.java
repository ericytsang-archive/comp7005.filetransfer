package filetransfer.gui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import filetransfer.logic.ClientApp;
import filetransfer.logic.ServerApp;

/**
 * the application window, and entry point of the program.
 *
 * @file    AppWindow.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   AppWindow
 *
 * @date    2015-09-29T18:59:08-0800
 *
 * @author  Eric Tsang
 */
public class AppWindow extends JFrame
{
    /**
     * unique version of this swing component.
     */
    private static final long serialVersionUID = 1L;

    /**
     * initial width of the application window.
     */
    private static final int WINDOW_WIDTH = 640;

    /**
     * initial height of the application window.
     */
    private static final int WINDOW_HEIGHT = 480;

    /**
     * title of the application window shown in the title bar.
     */
    private static final String WINDOW_TITLE = "Eric's COMP 7005 Assignment 1";

    /**
     * reference to the window's server logic instance.
     */
    private final ServerApp serverApp;

    /**
     * reference to the window's client logic instance.
     */
    private final ClientApp clientApp;

    /**
     * creates, configures and shows the application window, and initializes
     *   instance data.
     *
     * @method  AppWindow
     *
     * @date    2015-09-29T18:50:03-0800
     *
     * @author  Eric Tsang
     *
     * @return  a new instance of AppWindow
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
     * @method  main
     *
     * @date    2015-09-29T18:53:43-0800
     *
     * @author  Eric Tsang
     *
     * @param   args unused command line arguments.
     */
    public static void main(String[] args)
    {
        new AppWindow();
    }

    // private interface: inner classes

    /**
     * defines the option menu options along the top of the application's
     *   window, as well as the menu items in each menu option.
     *
     * @class   MyMenuBar
     *
     * @date    2015-09-29T18:55:03-0800
     *
     * @author  Eric Tsang
     */
    private class MyMenuBar extends JMenuBar
    {
        public MyMenuBar()
        {
            // create the server menu & add it to the application window
            JMenu serverOptionsMenu = new JMenu("Server");
            serverOptionsMenu.setMnemonic('s');
            add(serverOptionsMenu);

            // create the start server menu item & add it to the server menu
            JMenuItem startMenuItem = new JMenuItem("Start");
            startMenuItem.setMnemonic('s');
            startMenuItem.addActionListener(event -> serverApp.promptStartServer(AppWindow.this));
            serverOptionsMenu.add(startMenuItem);

            // create the stop server menu item & add it to the server menu
            JMenuItem stopMenuItem = new JMenuItem("Stop");
            stopMenuItem.setMnemonic('t');
            stopMenuItem.addActionListener(event -> serverApp.stopServer(AppWindow.this));
            serverOptionsMenu.add(stopMenuItem);

            // create the client menu & add it to the application window
            JMenu clientOptionsMenu = new JMenu("Client");
            clientOptionsMenu.setMnemonic('c');
            add(clientOptionsMenu);

            // create the connect to server menu item & add it to the server menu
            JMenuItem connectMenuItem = new JMenuItem("Connect...");
            connectMenuItem.setMnemonic('c');
            connectMenuItem.addActionListener(event -> clientApp.promptConnect(AppWindow.this));
            clientOptionsMenu.add(connectMenuItem);
        }
    }

    /**
     * the rot component in the application window. it defines what goes into
     *   the central section of the GUI.
     *
     * @class   MyMenuBar
     *
     * @date    2015-09-29T18:55:03-0800
     *
     * @author  Eric Tsang
     */
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
