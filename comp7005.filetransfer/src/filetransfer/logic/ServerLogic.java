package filetransfer.logic;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JOptionPane;

import filetransfer.net.Server;

public class ServerLogic
{
    // constants: dialog titles
    private static final String TITLE_START_SERVER_SUCCEEDED = "Successfully Started Server";
    private static final String TITLE_START_SERVER_FAILED = "Error Starting Server";
    private static final String TITLE_STOP_SERVER_SUCCEEDED = "Successfully Stopped Server";
    private static final String TITLE_STOP_SERVER_FAILED = "Error Stopping Server";

    // constants: dialog messages
    private static final String PROMPT_START_SERVER = "Enter server port number:";
    private static final String MESSAGE_SERVER_ALREADY_STOPPED = "The server is already stopped";
    private static final String MESSAGE_SERVER_STOPPED = "Server stopped";

    // instance data: general
    private Server server;

    // public interface: server methods

    public synchronized void promptStartServer(Component parentComponent)
    {
        // stop previous server if needed
        if(server != null)
        {
            try
            {
                server.stopServer();
                server = null;
            }
            catch(IOException e)
            {
                // server has already been closed...
            }
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

    private String makeServerStartedMessage(int portNumber)
    {
        return "Server started on port "+portNumber;
    }

    private String makeInvalidPortMessage(String stringPort)
    {
        return "\""+stringPort+"\" is not a valid port number";
    }

    private String makePortInUseMessage(int portNumber)
    {
        return "Port "+portNumber+" is already in use";
    }
}
