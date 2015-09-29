package filetransfer.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import filetransfer.net.PullClient;
import filetransfer.net.PullServer;

public class AppPullServer extends PullServer
{
    public static List<JsonableFile> pullDirectoryFiles(InetSocketAddress remoteAddress,ProgressMonitor progressMonitor,String path) throws IOException
    {
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

        // cleanup and end progress dialog
        finally
        {
            progressMonitor.setProgress(4);
            progressMonitor.close();
        }
    }

    /**
     * instantiates a server.
     *
     * @param listenPort port to bind the server to; connection requests
     *                   received on this port will be accepted.
     * @throws IOException when the server socket fails to bind to the given
     *                     port.
     */
    public AppPullServer(int listenPort) throws IOException
    {
        super(listenPort);
    }

    @Override
    protected String onPullRequest(Socket remote,String request)
    {
        try
        {
            JSONObject json = new JSONObject(request);
            switch(json.getInt(Protocol.KEY_TYPE))
            {
            case Protocol.TYPE_PULL_DIR_FILES:
                return handlePullDirectoryFiles(json.getString(Protocol.KEY_PATH));
            default:
                throw new RuntimeException("default case");
            }
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String handlePullDirectoryFiles(String path)
    {
        // add all the files in the current directory to a list to be returned
        LinkedList<JsonableFile> files = new LinkedList<>();
        //noinspection ConstantConditions
        for(File file : new File(path).listFiles())
        {
            files.add(new JsonableFile(file));
        }

        // put parent directory as element in list to be returned if it exists
        File parentFile = new File(path).getAbsoluteFile().getParentFile();
        if(parentFile != null)
        {
            files.addFirst(new JsonableFile(parentFile.isDirectory(),parentFile.getAbsolutePath(),".."));
        }

        return JsonableUtils.toJsonArray(files).toString();
    }
}
