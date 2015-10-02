package filetransfer.logic;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

/**
 * contains all the logic associated with connecting to a remote host, and
 *   maintaining state information about the client, and the connection with the
 *   remote host.
 *
 * @file    ClientLogic.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   ClientLogic
 *
 * @date    2015-10-02T12:18:02-0800
 *
 * @author  Eric Tsang
 */
public class ClientLogic
{
    // constants: dialog prompts

    /**
     * message displayed in the body of the dialog that prompts the user to
     *   enter the remote host's IP address or host name.
     */
    private static final String PROMPT_GET_REMOTE_HOSTNAME = "Enter remote IP address, or hostname:";

    /**
     * message displayed in the body of the dialog that prompts the user to
     *   enter the port number to connect to on the remote host.
     */
    private static final String PROMPT_GET_REMOTE_PORT = "Enter remote port number:";

    /**
     * message displayed in the body of the dialog indicating that the host name is invalid.
     */
    private static final String MESSAGE_MISSING_HOSTNAME = "Nothing is not a valid hostname";

    // constants: dialog titles

    /**
     * message displayed in the title of the dialog box used to indicate that
     *   there was an error connecting to the remote host; no connection was
     *   established.
     */
    private static final String TITLE_CONNECT_FAILED = "Error Connecting to Remote Host";

    // instance data: general

    /**
     * current local directory that the user is working in.
     */
    private File currentDirectory;

    /**
     * path to the current directory on the remote host that the client is
     *   connected to that the user is working in.
     */
    private String currentRemoteDirectory;

    /**
     * address of the remote host that this client is connected to.
     */
    private InetSocketAddress remoteAddress;

    /**
     * reference to the client's local list adapter.
     */
    private LocalListAdapter localListAdapter;

    /**
     * reference to the client's remote list adapter.
     */
    private RemoteListAdapter remoteListAdapter;

    /**
     * a thread
     */
    private ThreadPoolExecutor threadPoolExecutor;

    // public interface: constructors

    /**
     * instantiates a client logic instance
     *
     * @method  ClientLogic
     *
     * @date    2015-10-02T11:55:19-0800
     *
     * @author  Eric Tsang
     *
     * @return  returns a new instance of the client logic class.
     */
    public ClientLogic()
    {
        currentDirectory = new File(".");
        currentRemoteDirectory = ".";
        remoteAddress = new InetSocketAddress("0.0.0.0",0);
        localListAdapter = new LocalListAdapter(this);
        remoteListAdapter = new RemoteListAdapter(this);
        threadPoolExecutor = new ThreadPoolExecutor(1,1,1,TimeUnit.SECONDS,new LinkedBlockingQueue<>());
    }

    // public interface: server methods

    /**
     * returns the current directory that this application is working in.
     *
     * @method  getCurrentDirectory
     *
     * @date    2015-10-02T11:56:47-0800
     *
     * @author  Eric Tsang
     *
     * @return  the current directory that this application is working in.
     */
    public File getCurrentDirectory()
    {
        return currentDirectory.getName().equals(".")
                ? currentDirectory.getAbsoluteFile().getParentFile()
                : currentDirectory;
    }

    /**
     * sets the client's current local working directory.
     *
     * @method  setCurrentDirectory
     *
     * @date    2015-10-02T11:57:14-0800
     *
     * @author  Eric Tsang
     *
     * @param   newDirectory the client's new current local working directory.
     */
    public void setCurrentDirectory(File newDirectory)
    {
        currentDirectory = newDirectory;
    }

    /**
     * prompts the user to enter the required information needed to connect to a
     *   remote host.
     *
     * @method  promptConnect
     *
     * @date    2015-10-02T11:58:10-0800
     *
     * @author  Eric Tsang
     *
     * @param   parentComponent reference to the parent component used to
     *   display dialog boxes.
     */
    public void promptConnect(Component parentComponent)
    {
        // get the hostname, or ip address from user
        String remoteHost = JOptionPane.showInputDialog(parentComponent,PROMPT_GET_REMOTE_HOSTNAME);

        // get the port number from the user
        String remotePort = JOptionPane.showInputDialog(parentComponent,PROMPT_GET_REMOTE_PORT);

        try
        {
            // create the remote address, and remember it for future operations
            int portNumber = Integer.parseInt(remotePort);
            if(remoteHost == null || portNumber == 0) throw new IllegalArgumentException("remote port must be specified");

            // try to resolve the remote address
            remoteAddress = new InetSocketAddress(remoteHost,portNumber);

            // attempt to query files from the address right away
            threadPoolExecutor.execute(() ->
                remoteListAdapter.present(pullDirectoryFiles(parentComponent,"."))
            );
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
                int portNumber = Integer.parseInt(remotePort);
                JOptionPane.showMessageDialog(parentComponent,makePortOutOfRangeMessage(portNumber),TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(parentComponent,MESSAGE_MISSING_HOSTNAME,TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * returns the client's local list adapter.
     *
     * @method  getLocalListAdapter
     *
     * @date    2015-10-02T11:59:08-0800
     *
     * @author  Eric Tsang
     *
     * @return  the client's local list adapter.
     */
    public LocalListAdapter getLocalListAdapter()
    {
        return localListAdapter;
    }

    /**
     * returns the client's remote list adapter.
     *
     * @method  getRemoteListAdapter
     *
     * @date    2015-10-02T12:01:50-0800
     *
     * @author  Eric Tsang
     *
     * @return  the client's remote list adapter.
     */
    public RemoteListAdapter getRemoteListAdapter()
    {
        return remoteListAdapter;
    }

    /**
     * issues a download request to the remote host, and downloads the file.
     *
     * @method  pullFile
     *
     * @date    2015-10-02T12:02:13-0800
     *
     * @author  Eric Tsang
     *
     * @param   parentComponent used to display dialog boxes.
     * @param   path path to the file to download on the remote host.
     */
    public void pullFile(Component parentComponent,String path)
    {
        ProgressMonitor progressMonitor = new ProgressMonitor(parentComponent,makeDownloadingFileMessage(path),null,0,100);

        try
        {
            progressMonitor.setMillisToDecideToPopup(0);
            AppServer.pullFile(remoteAddress,progressMonitor,path,currentDirectory);
            getLocalListAdapter().presentCurrentDirectory();
        }

        catch(IOException e)
        {
            JOptionPane.showMessageDialog(parentComponent,makeConnectFailedMessage(remoteAddress.getHostString(),remoteAddress.getPort()),TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
        }

        // cleanup and end progress dialog
        finally
        {
            progressMonitor.setProgress(100);
            progressMonitor.close();
        }
    }

    /**
     * issues an upload request to the remote host.
     *
     * @method  pushFile
     *
     * @date    2015-10-02T12:03:21-0800
     *
     * @author  Eric Tsang
     *
     * @param   parentComponent used to display dialog boxes.
     * @param   fileToSend file to upload.
     */
    public void pushFile(Component parentComponent,File fileToSend)
    {
        ProgressMonitor progressMonitor = new ProgressMonitor(parentComponent,makeUploadingFileMessage(fileToSend),null,0,100);

        try
        {
            progressMonitor.setMillisToDecideToPopup(0);
            AppServer.pushFile(remoteAddress,progressMonitor,currentRemoteDirectory,fileToSend);
            getRemoteListAdapter().present(pullDirectoryFiles(parentComponent,currentRemoteDirectory));
        }

        catch(IOException e)
        {
            JOptionPane.showMessageDialog(parentComponent,makeConnectFailedMessage(remoteAddress.getHostString(),remoteAddress.getPort()),TITLE_CONNECT_FAILED,JOptionPane.ERROR_MESSAGE);
        }

        // cleanup and end progress dialog
        finally
        {
            progressMonitor.setProgress(100);
            progressMonitor.close();
        }
    }

    /**
     * pulls the files that are located in the specified directory on the remote
     *   host.
     *
     * @method  pullDirectoryFiles
     *
     * @date    2015-10-02T12:06:17-0800
     *
     * @author  Eric Tsang
     *
     * @param   parentComponent used to display dialog boxes.
     * @param   path path to the directory on the remote host.
     *
     * @return  information about the files that reside in the specified
     *   directory.
     */
    public List<JsonableFile> pullDirectoryFiles(Component parentComponent,String path)
    {
        ProgressMonitor progressMonitor = new ProgressMonitor(parentComponent,makePullingDirectoryFilesMessage(path),null,0,4);
        currentRemoteDirectory = path;

        try
        {
            progressMonitor.setMillisToDecideToPopup(0);
            return AppServer.pullDirectoryFiles(remoteAddress,progressMonitor,path);
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

    // private interface: dialog message builders

    /**
     * returns a message that indicates to the user that the application is
     *   currently downloading files in the specified directory.
     *
     * @method  makePullingDirectoryFilesMessage
     *
     * @date    2015-10-02T12:08:06-0800
     *
     * @author  Eric Tsang
     *
     * @param   path path to display in the message.
     *
     * @return  a message that indicates to the user that the application is
     *   currently downloading files in the specified directory.
     */
    private String makePullingDirectoryFilesMessage(String path)
    {
        return "Downloading files in directory: "+path;
    }

    /**
     * returns a message that indicates to the user that the application is
     *   currently uploading a file to the remote host.
     *
     * @method  makeUploadingFileMessage
     *
     * @date    2015-10-02T12:08:06-0800
     *
     * @author  Eric Tsang
     *
     * @param   File fileToSend file to use in the message
     *
     * @return  a message that indicates to the user that the application is
     *   currently uploading a file to the remote host.
     */
    private String makeUploadingFileMessage(File fileToSend)
    {
        return "Uploading: "+fileToSend.getAbsolutePath();
    }

    /**
     * returns a message that indicates to the user that the application is
     *   currently downloading a file from the remote host.
     *
     * @method  makeDownloadingFileMessage
     *
     * @date    2015-10-02T12:08:06-0800
     *
     * @author  Eric Tsang
     *
     * @param   path path to a file to use in the message
     *
     * @return  a message that indicates to the user that the application is
     *   currently downloading a file from the remote host.
     */
    private String makeDownloadingFileMessage(String path)
    {
        return "Downloading: "+path;
    }

    /**
     * returns a message that indicates to the user that the application has
     *   failed to connect to the remote host.
     *
     * @method  makePullingDirectoryFilesMessage
     *
     * @date    2015-10-02T12:08:06-0800
     *
     * @author  Eric Tsang
     *
     * @param   remoteHost host name of the remote host.
     * @param   remotePort port on the remote host.
     *
     * @return  a message that indicates to the user that the application has
     *   failed to connect to the remote host.
     */
    private String makeConnectFailedMessage(String remoteHost,int remotePort)
    {
        return "Failed to connect to "+remoteHost+":"+remotePort;
    }

    /**
     * returns a message that indicates to the user that the specified port is
     *   invalid.
     *
     * @method  makePullingDirectoryFilesMessage
     *
     * @date    2015-10-02T12:08:06-0800
     *
     * @author  Eric Tsang
     *
     * @param   stringPort port that is invalid.
     *
     * @return  a message that indicates to the user that the specified port is
     *   invalid.
     */
    private String makeInvalidPortMessage(String stringPort)
    {
        return "\""+stringPort+"\" is not a valid port number";
    }

    /**
     * returns a message that indicates to the user that the specified port is
     *   out of range.
     *
     * @method  makePullingDirectoryFilesMessage
     *
     * @date    2015-10-02T12:08:06-0800
     *
     * @author  Eric Tsang
     *
     * @param   portNumber port umber that is out of range.
     *
     * @return  a message that indicates to the user that the specified port is
     *   out of range.
     */
    private String makePortOutOfRangeMessage(int portNumber)
    {
        return "The specified port number ("+portNumber+") is out of range; valid port numbers range from 1 to 65535";
    }
}
