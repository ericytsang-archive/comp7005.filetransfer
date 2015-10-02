package filetransfer.logic;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JOptionPane;

import filetransfer.net.Server;

/**
 * contains all the logic and state information used to handle starting and
 *   stopping the application's server.
 *
 * @file    ServerLogic.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   ServerLogic
 *
 * @date    2015-10-02T11:05:31-0800
 *
 * @author  Eric Tsang
 */
public class ServerLogic
{
    // constants: dialog titles

    /**
     * string displayed in the title of the dialog that indicates that the
     *   server has succeeded to start.
     */
    private static final String TITLE_START_SERVER_SUCCEEDED = "Successfully Started Server";

    /**
     * string displayed in the title of the dialog that indicates that the
     *   server has failed to start.
     */
    private static final String TITLE_START_SERVER_FAILED = "Error Starting Server";

    /**
     * string displayed in the title of the dialog that indicates that the
     *   server has succeeded to stop.
     */
    private static final String TITLE_STOP_SERVER_SUCCEEDED = "Successfully Stopped Server";

    /**
     * string displayed in the title of the dialog that indicates that the
     *   server has failed to stop.
     */
    private static final String TITLE_STOP_SERVER_FAILED = "Error Stopping Server";

    // constants: dialog messages

    /**
     * string displayed in the body of the dialog box that is used to prompt the
     *   user to enter a port number in order to start the server.
     */
    private static final String PROMPT_START_SERVER = "Enter server port number:";

    /**
     * string displayed in the body of the dialog box that is used to inform the
     *   user that the server has already stopped; it cannot be stopped again.
     */
    private static final String MESSAGE_SERVER_ALREADY_STOPPED = "The server is already stopped";

    /**
     * string displayed in the body of the dialog box that is used to inform the
     *   user that the server has successfully been stopped.
     */
    private static final String MESSAGE_SERVER_STOPPED = "Server stopped";

    // instance data: general

    /**
     * reference to the application's server.
     */
    private Server server;

    // public interface: server methods

    /**
     * prompts the user to enter the required information to start the server;
     *   starts the server if the entered information is valid.
     *
     * @method  promptStartServer
     *
     * @date    2015-10-02T10:56:26-0800
     *
     * @author  Eric Tsang
     *
     * @param   parentComponent reference to the parent component so we may
     *   display dialog boxes if needed.
     */
    public synchronized void promptStartServer(Component parentComponent)
    {
        // stop previous server if possible
        try
        {
            server.stopServer();
            server = null;
        }
        catch(IOException|NullPointerException e)
        {
            // server has already been closed, or was never open during this
            // session...
        }

        // prompt the user for a port number to start the new server on
        String stringPort = JOptionPane.showInputDialog(parentComponent,PROMPT_START_SERVER);

        // start the server on the specified port
        try
        {
            int portNumber = Integer.parseUnsignedInt(stringPort);
            server = new AppServer(portNumber);
            JOptionPane.showMessageDialog(parentComponent,makeServerStartedMessage(portNumber),TITLE_START_SERVER_SUCCEEDED,JOptionPane.INFORMATION_MESSAGE);
        }

        // failed to start the sever; it is probably an invalid port, or the
        // port is in use
        catch(IOException e)
        {
            int portNumber = Integer.parseUnsignedInt(stringPort);
            JOptionPane.showMessageDialog(parentComponent,makePortInUseMessage(portNumber),TITLE_START_SERVER_FAILED,JOptionPane.ERROR_MESSAGE);
        }

        // the entered port number is invalid
        catch(NumberFormatException e)
        {
            JOptionPane.showMessageDialog(parentComponent,makeInvalidPortMessage(stringPort),TITLE_START_SERVER_FAILED,JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * attempts to stop the server, and informs the user of the outcome of the
     *   operation using dialog boxes.
     *
     * @method  stopServer
     *
     * @date    2015-10-02T10:58:44-0800
     *
     * @author  Eric Tsang
     *
     * @param   parentComponent reference to the parent component, so we may
     *   display dialog boxes.
     */
    public synchronized void stopServer(Component parentComponent)
    {
        // try to stop the server...
        try
        {
            server.stopServer();
            JOptionPane.showMessageDialog(parentComponent,MESSAGE_SERVER_STOPPED,TITLE_STOP_SERVER_SUCCEEDED,JOptionPane.INFORMATION_MESSAGE);
        }

        // IOException: the server has already been stopped...
        // NullPointerException: the server was never started yet...
        catch(IOException|NullPointerException e)
        {
            JOptionPane.showMessageDialog(parentComponent,MESSAGE_SERVER_ALREADY_STOPPED,TITLE_STOP_SERVER_FAILED,JOptionPane.WARNING_MESSAGE);
        }
    }

    // private interface: dialog message builders

    /**
     * used to create the message displayed in the dialog box used to inform the
     *   user that the server has started.
     *
     * @method  makeServerStartedMessage
     *
     * @date    2015-10-02T10:59:37-0800
     *
     * @author  Eric Tsang
     *
     * @param   portNumber port number to display in the message.
     *
     * @return  a string addressed to the user indicating that the server has
     *   started on the passed port.
     */
    private String makeServerStartedMessage(int portNumber)
    {
        return "Server started on port "+portNumber;
    }

    /**
     * used to create the message displayed in the dialog box used to inform the
     *   user that the port number is not a valid port number.
     *
     * @method  makeInvalidPortMessage
     *
     * @date    2015-10-02T10:59:37-0800
     *
     * @author  Eric Tsang
     *
     * @param   stringPort port number to display in the message.
     *
     * @return  a string addressed to the user indicating that the passed port
     *   number is invalid.
     */
    private String makeInvalidPortMessage(String stringPort)
    {
        return "\""+stringPort+"\" is not a valid port number";
    }

    /**
     * used to create the message displayed in the dialog box used to inform the
     *   user that the entered port is in use.
     *
     * @method  makePortInUseMessage
     *
     * @date    2015-10-02T11:01:54-0800
     *
     * @author  Eric Tsang
     *
     * @param   portNumber port number to display in the message.
     *
     * @return  a string addressed to the user indicating that the port number
     *   is in use.
     */
    private String makePortInUseMessage(int portNumber)
    {
        return "Port "+portNumber+" is already in use";
    }
}
