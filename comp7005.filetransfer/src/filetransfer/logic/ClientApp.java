package filetransfer.logic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import filetransfer.gui.LocalListAdapter;
import filetransfer.gui.RemoteListAdapter;
import filetransfer.net.PullClient;
import filetransfer.utils.JsonableUtils;

public class ClientApp
{
    // constants: dialog prompts
    private static final String PROMPT_GET_REMOTE_HOSTNAME = "Enter remote IP address, or hostname:";
    private static final String PROMPT_GET_REMOTE_PORT = "Enter remote port number:";
    private static final String MESSAGE_MISSING_HOSTNAME = "Nothing is not a valid hostname";

    // constants: dialog titles
    private static final String TITLE_CONNECT_FAILED = "Error Connecting to Remote Host";
    private static final String MESSAGE_PULLING_DIR_FILES = "Pulling files...";

    // instance data: general
    private File currentDirectory;
    private InetSocketAddress remoteAddress;
    private RemoteListAdapter remoteListAdapter;

    // public interface: constructors

    public ClientApp()
    {
        currentDirectory = new File("./");
        remoteListAdapter = new RemoteListAdapter(this);
    }

    // public interface: server methods

    public File getCurrentDirectory()
    {
        return currentDirectory;
    }

    public void setCurrentDirectory(File newDirectory)
    {
        // todo: there may need to be some sort of error checking... :o test pris~
        currentDirectory = newDirectory;
    }

    public void promptConnect(Component parentComponent)
    {
        // get the hostname, or ip address from user
        String remoteHost = JOptionPane.showInputDialog(parentComponent,PROMPT_GET_REMOTE_HOSTNAME);

        // get the port number from the user
        String remotePort = JOptionPane.showInputDialog(parentComponent,PROMPT_GET_REMOTE_PORT);

        try
        {
            // create the remote address, and remember it for future operations
            int portNumber = Integer.parseUnsignedInt(remotePort);
            if(remoteHost == null || portNumber == 0) throw new IllegalArgumentException("remote port must be specified");

            // try to resolve the remote address
            remoteAddress = new InetSocketAddress(remoteHost,portNumber);

            // attempt to query files from the address right away
            remoteListAdapter.present(parentComponent,pullDirectoryFiles(parentComponent,"./"));
        }

        // invalid port number
        catch(NumberFormatException e)
        {
            JOptionPane.showMessageDialog(parentComponent,makeInvalidPortMessage(remotePort),TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
        }

        // port number out of range, or invalid hostname
        catch(IllegalArgumentException e)
        {
            if(remoteHost != null)
            {
                int portNumber = Integer.parseUnsignedInt(remotePort);
                JOptionPane.showMessageDialog(parentComponent,makePortOutOfRangeMessage(portNumber),TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(parentComponent,MESSAGE_MISSING_HOSTNAME,TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public synchronized List<JsonableFile> pullDirectoryFiles(Component parentComponent,String path)
    {
        ProgressMonitor progressMonitor = new ProgressMonitor(null,MESSAGE_PULLING_DIR_FILES,null,0,4);
        progressMonitor.setMillisToDecideToPopup(100);

        try
        {
            // prepare the request string
            progressMonitor.setProgress(0);
            JSONObject json = new JSONObject();
            json.put(Protocol.KEY_TYPE,Protocol.TYPE_PULL_DIR_FILES);
            json.put(Protocol.KEY_PATH,path);

            // do the pulling
            progressMonitor.setProgress(1);
            PullClient puller = new PullClient(remoteAddress);
            String response = puller.pull(json.toString());

            // parse the received response string and return
            progressMonitor.setProgress(3);
            return JsonableUtils.fromJsonArray(JsonableFile.class,new JSONArray(response));
        }

        catch(IOException e)
        {
            JOptionPane.showMessageDialog(parentComponent,makeConnectFailedMessage(remoteAddress.getHostString(),remoteAddress.getPort()),TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
            return Collections.emptyList();
        }

        // cleanup and end progress dialog
        finally
        {
            progressMonitor.setProgress(4);
            progressMonitor.close();
        }
    }

    public LocalListAdapter getLocalListAdapter()
    {
        return new LocalListAdapter(this);
    }

    public RemoteListAdapter getRemoteListAdapter()
    {
        return remoteListAdapter;
    }

    // private interface: dialog message builders

    private String makeConnectFailedMessage(String remoteHost,int remotePort)
    {
        return "Failed to connect to "+remoteHost+":"+remotePort;
    }

    private String makeInvalidPortMessage(String stringPort)
    {
        return "\""+stringPort+"\" is not a valid port number";
    }

    private String makePortOutOfRangeMessage(int portNumber)
    {
        return "The specified port number ("+portNumber+") is our of range; valid ports range from 1 to ";
    }
}
